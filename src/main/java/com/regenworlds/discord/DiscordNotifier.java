package com.regenworlds.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.Color;
import java.time.Instant;

public class DiscordNotifier {
    private final JavaPlugin plugin;
    private JDA jda;
    private String channelId;

    public DiscordNotifier(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void start(String token, String channelId) {
        this.channelId = channelId;
        if (token.isBlank() || token.equals("YOUR_BOT_TOKEN_HERE")) {
            plugin.getLogger().warning("[RegenWorlds] Discord token not configured.");
            return;
        }
        try {
            jda = JDABuilder.createLight(token, GatewayIntent.getIntents(0)).build().awaitReady();
            plugin.getLogger().info("[RegenWorlds] Discord bot connected.");
        } catch (Exception e) {
            plugin.getLogger().severe("[RegenWorlds] Discord connection failed: " + e.getMessage());
        }
    }

    public void sendRegenNotification(String message) {
        if (jda == null) return;
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) return;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🌀 Void Worlds Regenerated")
                .setDescription(message)
                .setColor(new Color(120, 0, 180))
                .setFooter("RegenWorlds • Void worlds reset with new seeds")
                .setTimestamp(Instant.now());

        channel.sendMessageEmbeds(embed.build()).queue();
    }

    public void shutdown() {
        if (jda != null) jda.shutdown();
    }
}
