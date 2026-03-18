package com.regenworlds.listener;

import com.regenworlds.lang.LangManager;
import com.regenworlds.model.PlayerReturn;
import com.regenworlds.model.VoidWorldType;
import com.regenworlds.service.PortalFrameBuilder;
import com.regenworlds.service.ReturnLocationStore;
import com.regenworlds.service.VoidWorldManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PortalListener implements Listener {
    private final JavaPlugin plugin;
    private final VoidWorldManager worldManager;
    private final ReturnLocationStore returnStore;
    private final LangManager lang;
    private final Set<UUID> teleporting = new HashSet<>();

    private static final Set<String> VOID_WORLD_NAMES = new HashSet<>();

    public PortalListener(JavaPlugin plugin, VoidWorldManager worldManager, ReturnLocationStore returnStore, LangManager lang) {
        this.plugin = plugin;
        this.worldManager = worldManager;
        this.returnStore = returnStore;
        this.lang = lang;
    }

    public static void registerVoidWorld(String name) {
        VOID_WORLD_NAMES.add(name);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        UUID uid = player.getUniqueId();
        if (teleporting.contains(uid)) return;

        String worldName = player.getWorld().getName();

        if (VOID_WORLD_NAMES.contains(worldName)) {
            event.setCancelled(true);
            handleReturnPortal(player);
            return;
        }

        Block portalBlock = findNearbyPortalBlock(player);
        plugin.getLogger().info("[RegenWorlds] Portal event: player=" + player.getName()
                + " loc=" + player.getLocation().toVector()
                + " portalBlock=" + (portalBlock != null ? portalBlock.getLocation().toVector() : "null")
                + " frame=" + (portalBlock != null ? PortalFrameBuilder.isInsideCryingObsidianPortal(portalBlock) : "n/a"));

        if (portalBlock != null && PortalFrameBuilder.isInsideCryingObsidianPortal(portalBlock)) {
            event.setCancelled(true);
            handleEnterPortal(player);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!VOID_WORLD_NAMES.contains(event.getBlock().getWorld().getName())) return;
        if (isProtectedBlock(event.getBlock())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(lang.get(event.getPlayer(), "portal_indestructible"));
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (VOID_WORLD_NAMES.contains(event.getBlock().getWorld().getName()) && isProtectedBlock(event.getBlock()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!VOID_WORLD_NAMES.contains(event.getLocation().getWorld().getName())) return;
        event.blockList().removeIf(this::isProtectedBlock);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (!VOID_WORLD_NAMES.contains(event.getBlock().getWorld().getName())) return;
        event.blockList().removeIf(this::isProtectedBlock);
    }

    private boolean isProtectedBlock(Block block) {
        Location origin = worldManager.getPortalOrigin(block.getWorld());
        return PortalFrameBuilder.isProtectedPortalBlock(block, origin);
    }

    private Block findNearbyPortalBlock(Player player) {
        Location loc = player.getLocation();
        for (int dy = -1; dy <= 2; dy++)
            for (int dx = -1; dx <= 1; dx++)
                for (int dz = -1; dz <= 1; dz++) {
                    Block block = loc.getWorld().getBlockAt(loc.getBlockX() + dx, loc.getBlockY() + dy, loc.getBlockZ() + dz);
                    if (block.getType() == Material.NETHER_PORTAL) return block;
                }
        return null;
    }

    private void handleEnterPortal(Player player) {
        UUID uid = player.getUniqueId();
        teleporting.add(uid);

        Location from = player.getLocation().clone();
        returnStore.set(uid, new PlayerReturn(from.getWorld().getName(), from.getX(), from.getY(), from.getZ(), from.getYaw(), from.getPitch()));

        VoidWorldType type = VoidWorldType.fromWorld(player.getWorld());
        World voidWorld = worldManager.getOrCreateVoidWorld(type);

        if (voidWorld == null) {
            player.sendMessage(lang.get(player, "void_world_error"));
            teleporting.remove(uid);
            return;
        }

        Location dest = worldManager.getPortalTeleportLocation(voidWorld, from.getYaw(), from.getPitch());
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            player.teleport(dest);
            player.sendMessage(lang.get(player, "entered_void"));
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> teleporting.remove(uid), 40L);
        });
    }

    private void handleReturnPortal(Player player) {
        UUID uid = player.getUniqueId();
        teleporting.add(uid);

        PlayerReturn ret = returnStore.get(uid);
        if (ret == null) {
            World overworld = Bukkit.getWorlds().get(0);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                player.teleport(overworld.getSpawnLocation());
                player.sendMessage(lang.get(player, "no_return_point"));
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> teleporting.remove(uid), 40L);
            });
            return;
        }

        World returnWorld = Bukkit.getWorld(ret.worldName);
        if (returnWorld == null) returnWorld = Bukkit.getWorlds().get(0);

        World finalWorld = returnWorld;
        Location dest = new Location(finalWorld, ret.x, ret.y, ret.z, ret.yaw, ret.pitch);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            player.teleport(dest);
            returnStore.remove(uid);
            player.sendMessage(lang.get(player, "returned"));
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> teleporting.remove(uid), 40L);
        });
    }
}
