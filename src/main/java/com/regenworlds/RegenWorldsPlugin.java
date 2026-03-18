package com.regenworlds;

import com.regenworlds.command.RegenWorldsCommand;
import com.regenworlds.discord.DiscordNotifier;
import com.regenworlds.listener.PortalActivateListener;
import com.regenworlds.listener.PortalListener;
import com.regenworlds.model.VoidWorldType;
import com.regenworlds.service.RegenScheduler;
import com.regenworlds.service.ReturnLocationStore;
import com.regenworlds.service.VoidWorldManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RegenWorldsPlugin extends JavaPlugin {
    private DiscordNotifier discord;
    private RegenScheduler regenScheduler;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        String overworldName = getConfig().getString("worlds.overworld_void", "void_overworld");
        String netherName = getConfig().getString("worlds.nether_void", "void_nether");
        String endName = getConfig().getString("worlds.end_void", "void_end");

        PortalListener.registerVoidWorld(overworldName);
        PortalListener.registerVoidWorld(netherName);
        PortalListener.registerVoidWorld(endName);

        VoidWorldManager worldManager = new VoidWorldManager(this, overworldName, netherName, endName);
        ReturnLocationStore returnStore = new ReturnLocationStore(this);

        discord = new DiscordNotifier(this);
        discord.start(
                getConfig().getString("discord.token", ""),
                getConfig().getString("discord.channel_id", ""));

        double minHours = getConfig().getDouble("regen.min_hours", 24);
        double maxHours = getConfig().getDouble("regen.max_hours", 48);

        regenScheduler = new RegenScheduler(this, worldManager, discord, minHours, maxHours);
        regenScheduler.start();

        for (VoidWorldType type : VoidWorldType.values()) {
            worldManager.regenerateVoidWorld(type);
        }

        PortalListener portalListener = new PortalListener(this, worldManager, returnStore);
        getServer().getPluginManager().registerEvents(portalListener, this);
        getServer().getPluginManager().registerEvents(new PortalActivateListener(), this);

        getCommand("regenworlds").setExecutor(new RegenWorldsCommand(regenScheduler));

        getLogger().info("RegenWorlds enabled.");
    }

    @Override
    public void onDisable() {
        if (regenScheduler != null) regenScheduler.stop();
        if (discord != null) discord.shutdown();
        getLogger().info("RegenWorlds disabled.");
    }
}
