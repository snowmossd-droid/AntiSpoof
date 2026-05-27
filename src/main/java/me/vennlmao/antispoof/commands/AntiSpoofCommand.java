package me.vennlmao.antispoof.commands;

import me.vennlmao.antispoof.AntiSpoof;
import me.vennlmao.antispoof.models.PlayerData;
import me.vennlmao.antispoof.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AntiSpoofCommand implements CommandExecutor {
    private final AntiSpoof plugin;

    public AntiSpoofCommand(AntiSpoof plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("antispoof.admin")) {
            sender.sendMessage(MessageUtil.color("&cBan khong co quyen!"));
            return true;
        }
        if (args.length == 0) { sendHelp(sender, label); return true; }
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.getConfigManager().load();
                sender.sendMessage(MessageUtil.color("&a[AntiSpoof] Da reload config!"));
            }
            case "check" -> {
                if (args.length < 2) { sender.sendMessage(MessageUtil.color("&cDung: /" + label + " check <player>")); return true; }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) { sender.sendMessage(MessageUtil.color("&cKhong tim thay: &e" + args[1])); return true; }
                PlayerData data = plugin.getCheckManager().getData(t.getUniqueId());
                if (data == null) { sender.sendMessage(MessageUtil.color("&cKhong co du lieu.")); return true; }
                data.setChecked(false);
                plugin.getCheckManager().runCheck(t);
                sender.sendMessage(MessageUtil.color("&a[AntiSpoof] Da check &e" + t.getName()));
            }
            case "info" -> {
                if (args.length < 2) { sender.sendMessage(MessageUtil.color("&cDung: /" + label + " info <player>")); return true; }
                Player t = Bukkit.getPlayer(args[1]);
                if (t == null) { sender.sendMessage(MessageUtil.color("&cKhong tim thay: &e" + args[1])); return true; }
                PlayerData data = plugin.getCheckManager().getData(t.getUniqueId());
                if (data == null) { sender.sendMessage(MessageUtil.color("&cKhong co du lieu.")); return true; }
                sender.sendMessage(MessageUtil.color("&8[&cAS&8] &e" + t.getName()));
                sender.sendMessage(MessageUtil.color("  &7Brand: &f" + (data.getBrand().isEmpty() ? "(trong)" : data.getBrand())));
                sender.sendMessage(MessageUtil.color("  &7Channels(" + data.getChannels().size() + "): &f" + data.getChannels()));
            }
            default -> sendHelp(sender, label);
        }
        return true;
    }

    private void sendHelp(CommandSender s, String label) {
        s.sendMessage(MessageUtil.color("&8[&cAntiSpoof&8] &e/" + label + " reload&7, &e/" + label + " check <p>&7, &e/" + label + " info <p>"));
    }
}
