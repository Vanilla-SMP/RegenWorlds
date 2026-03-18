package com.regenworlds.command;

import com.regenworlds.service.RegenScheduler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.Duration;
import java.time.Instant;

public class RegenWorldsCommand implements CommandExecutor {
    private final RegenScheduler scheduler;

    public RegenWorldsCommand(RegenScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("regenworlds.admin")) {
            sender.sendMessage("§cНедостаточно прав.");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage("§b[RegenWorlds] §fИспользование: /regenworlds <reload|status|regen>");
            return true;
        }
        return switch (args[0].toLowerCase()) {
            case "status" -> {
                long next = scheduler.getNextRegenAt();
                Duration remaining = Duration.between(Instant.now(), Instant.ofEpochMilli(next));
                long hours = remaining.toHours();
                long minutes = remaining.toMinutes() % 60;
                sender.sendMessage("§b[RegenWorlds] §fСледующий ресет через: §e" + hours + "ч " + minutes + "м");
                yield true;
            }
            case "regen" -> {
                sender.sendMessage("§b[RegenWorlds] §fЗапускаю ресет void миров...");
                scheduler.forceRegen();
                yield true;
            }
            default -> {
                sender.sendMessage("§b[RegenWorlds] §fИспользование: /regenworlds <status|regen>");
                yield true;
            }
        };
    }
}
