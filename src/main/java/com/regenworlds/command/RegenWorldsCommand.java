package com.regenworlds.command;

import com.regenworlds.lang.LangManager;
import com.regenworlds.service.RegenScheduler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;

public class RegenWorldsCommand implements CommandExecutor {
    private final RegenScheduler scheduler;
    private final LangManager lang;

    public RegenWorldsCommand(RegenScheduler scheduler, LangManager lang) {
        this.scheduler = scheduler;
        this.lang = lang;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = sender instanceof Player p ? p : null;

        if (!sender.hasPermission("regenworlds.admin")) {
            sender.sendMessage(lang.get(player, "no_permission"));
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(lang.get(player, "usage"));
            return true;
        }
        return switch (args[0].toLowerCase()) {
            case "status" -> {
                Duration remaining = Duration.between(Instant.now(), Instant.ofEpochMilli(scheduler.getNextRegenAt()));
                sender.sendMessage(lang.get(player, "status",
                        "hours", String.valueOf(remaining.toHours()),
                        "minutes", String.valueOf(remaining.toMinutes() % 60)));
                yield true;
            }
            case "regen" -> {
                sender.sendMessage(lang.get(player, "regen_started"));
                scheduler.forceRegen();
                yield true;
            }
            default -> {
                sender.sendMessage(lang.get(player, "usage"));
                yield true;
            }
        };
    }
}
