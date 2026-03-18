package com.regenworlds.service;

import com.regenworlds.model.VoidWorldType;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Random;

public class VoidWorldManager {
    private static final int PORTAL_BASE_Y = 64;
    private static final int PLATFORM_Y = PORTAL_BASE_Y - 1;

    private final JavaPlugin plugin;
    private final String overworldName;
    private final String netherName;
    private final String endName;

    public VoidWorldManager(JavaPlugin plugin, String overworldName, String netherName, String endName) {
        this.plugin = plugin;
        this.overworldName = overworldName;
        this.netherName = netherName;
        this.endName = endName;
    }

    public String getWorldName(VoidWorldType type) {
        return switch (type) {
            case NETHER -> netherName;
            case END -> endName;
            case OVERWORLD -> overworldName;
        };
    }

    public World getOrCreateVoidWorld(VoidWorldType type) {
        String name = getWorldName(type);
        World existing = Bukkit.getWorld(name);
        if (existing != null) return existing;
        return createVoidWorld(name, type, new Random().nextLong());
    }

    public Location getPortalOrigin(World world) {
        return new Location(world, -1, PORTAL_BASE_Y, 0);
    }

    public Location getPortalTeleportLocation(World world, float yaw, float pitch) {
        return new Location(world, 0.5, PORTAL_BASE_Y + 1, 0.5, yaw, pitch);
    }

    public World regenerateVoidWorld(VoidWorldType type) {
        String name = getWorldName(type);
        World world = Bukkit.getWorld(name);

        if (world != null) {
            world.getPlayers().forEach(p -> {
                p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                p.sendMessage("§c[RegenWorlds] §fМир был пересоздан. Вы перемещены в главный мир.");
            });
            Bukkit.unloadWorld(world, false);
        }

        deleteWorldFolder(new File(Bukkit.getWorldContainer(), name));
        return createVoidWorld(name, type, new Random().nextLong());
    }

    private World createVoidWorld(String name, VoidWorldType type, long seed) {
        copyDatapacks(name);

        World world = new WorldCreator(name)
                .environment(type.toEnvironment())
                .seed(seed)
                .createWorld();

        if (world == null) {
            plugin.getLogger().severe("Failed to create world: " + name);
            return null;
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            applyBorder(world);
            buildSpawnPlatformAndPortal(world);
            plugin.getLogger().info("Initialized void world: " + name);
        }, 40L);

        return world;
    }

    private void buildSpawnPlatformAndPortal(World world) {
        world.loadChunk(0, 0, true);

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                world.getBlockAt(x, PLATFORM_Y, z).setType(Material.OBSIDIAN);
            }
        }

        PortalFrameBuilder.buildCryingObsidianPortal(getPortalOrigin(world));
        world.setSpawnLocation(0, PORTAL_BASE_Y + 1, 0);
    }

    private void copyDatapacks(String worldName) {
        World main = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
        File src = main != null
                ? new File(main.getWorldFolder(), "datapacks")
                : new File("datapacks");

        if (!src.exists() || !src.isDirectory()) return;

        File dst = new File(Bukkit.getWorldContainer(), worldName + "/datapacks");
        dst.mkdirs();

        File[] files = src.listFiles();
        if (files == null) return;

        for (File dp : files) {
            try {
                copyRecursive(dp.toPath(), dst.toPath().resolve(dp.getName()));
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to copy datapack " + dp.getName() + ": " + e.getMessage());
            }
        }
        plugin.getLogger().info("Copied datapacks from " + main.getName() + " to " + worldName);
    }

    private void copyRecursive(Path src, Path dst) throws IOException {
        Files.walkFileTree(src, new SimpleFileVisitor<>() {
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(dst.resolve(src.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, dst.resolve(src.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void applyBorder(World world) {
        WorldBorder border = world.getWorldBorder();
        border.setCenter(0, 0);
        border.setSize(200);
    }

    private void deleteWorldFolder(File folder) {
        if (!folder.exists()) return;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) deleteWorldFolder(f);
                else f.delete();
            }
        }
        folder.delete();
    }
}
