package com.regenworlds.listener;

import com.regenworlds.service.PortalFrameBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PortalActivateListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null || event.getItem().getType() != Material.FLINT_AND_STEEL) return;

        Block clicked = event.getClickedBlock();
        if (clicked == null) return;
        if (clicked.getType() != Material.CRYING_OBSIDIAN) return;

        tryIgnitePortal(clicked, true);
        tryIgnitePortal(clicked, false);
    }

    private void tryIgnitePortal(Block cryingObsidian, boolean xAxis) {
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                Block candidate = xAxis
                        ? cryingObsidian.getWorld().getBlockAt(
                            cryingObsidian.getX() + dx,
                            cryingObsidian.getY() + dy,
                            cryingObsidian.getZ())
                        : cryingObsidian.getWorld().getBlockAt(
                            cryingObsidian.getX(),
                            cryingObsidian.getY() + dy,
                            cryingObsidian.getZ() + dx);

                if (PortalFrameBuilder.isValidPortalAtBase(candidate.getLocation(), xAxis)) {
                    fillPortal(candidate.getLocation(), xAxis);
                    return;
                }
            }
        }
    }

    private void fillPortal(org.bukkit.Location base, boolean xAxis) {
        int width = 4;
        int height = 5;
        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                Block block = xAxis
                        ? base.clone().add(x, y, 0).getBlock()
                        : base.clone().add(0, y, x).getBlock();
                if (block.getType() == Material.AIR || block.getType() == Material.CAVE_AIR) {
                    block.setType(Material.NETHER_PORTAL);
                    org.bukkit.block.data.Orientable portal = (org.bukkit.block.data.Orientable) block.getBlockData();
                    portal.setAxis(xAxis ? org.bukkit.Axis.X : org.bukkit.Axis.Z);
                    block.setBlockData(portal);
                }
            }
        }
    }
}
