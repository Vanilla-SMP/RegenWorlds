package com.regenworlds.service;

import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Orientable;

public class PortalFrameBuilder {

    public static boolean isInsideCryingObsidianPortal(Block portalBlock) {
        if (portalBlock.getType() != Material.NETHER_PORTAL) return false;
        Axis axis = ((Orientable) portalBlock.getBlockData()).getAxis();
        return hasAdjacentCryingObsidian(portalBlock, axis);
    }

    private static boolean hasAdjacentCryingObsidian(Block block, Axis axis) {
        int x = block.getX(), y = block.getY(), z = block.getZ();
        var world = block.getWorld();

        Block bottom = world.getBlockAt(x, y - 1, z);
        Block top = world.getBlockAt(x, y + 1, z);

        if (axis == Axis.X) {
            Block left = world.getBlockAt(x - 1, y, z);
            Block right = world.getBlockAt(x + 1, y, z);
            return isCrying(bottom) || isCrying(top) || isCrying(left) || isCrying(right);
        } else {
            Block left = world.getBlockAt(x, y, z - 1);
            Block right = world.getBlockAt(x, y, z + 1);
            return isCrying(bottom) || isCrying(top) || isCrying(left) || isCrying(right);
        }
    }

    private static boolean isCrying(Block block) {
        return block.getType() == Material.CRYING_OBSIDIAN;
    }

    public static boolean isValidPortalAtBase(Location base, boolean xAxis) {
        int width = 4;
        int height = 5;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean isFrame = x == 0 || x == width - 1 || y == 0 || y == height - 1;
                Location bl = xAxis
                        ? base.clone().add(x, y, 0)
                        : base.clone().add(0, y, x);
                Material mat = bl.getBlock().getType();
                if (isFrame && mat != Material.CRYING_OBSIDIAN) return false;
                if (!isFrame && mat != Material.AIR && mat != Material.CAVE_AIR && mat != Material.NETHER_PORTAL) return false;
            }
        }
        return true;
    }

    public static void buildCryingObsidianPortal(Location base) {
        int width = 4;
        int height = 5;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                boolean isFrame = x == 0 || x == width - 1 || y == 0 || y == height - 1;
                Block block = base.clone().add(x, y, 0).getBlock();
                block.setType(isFrame ? Material.CRYING_OBSIDIAN : Material.AIR);
            }
        }
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                Block block = base.clone().add(x, y, 0).getBlock();
                block.setType(Material.NETHER_PORTAL);
                Orientable portal = (Orientable) block.getBlockData();
                portal.setAxis(Axis.X);
                block.setBlockData(portal);
            }
        }
    }

    public static boolean isProtectedPortalBlock(Block block, Location portalOrigin) {
        int width = 4;
        int height = 5;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Location loc = portalOrigin.clone().add(x, y, 0);
                if (loc.getBlockX() == block.getX()
                        && loc.getBlockY() == block.getY()
                        && loc.getBlockZ() == block.getZ()) return true;
            }
        }
        return false;
    }
}
