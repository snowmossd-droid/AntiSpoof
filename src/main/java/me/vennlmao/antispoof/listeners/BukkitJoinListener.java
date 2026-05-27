package me.vennlmao.antispoof.listeners;

import me.vennlmao.antispoof.AntiSpoof;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitJoinListener implements Listener {

    private final AntiSpoof plugin;

    public BukkitJoinListener(AntiSpoof plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        if (player.hasPermission("antispoof.bypass")) return;
        plugin.getCheckManager().register(player.getUniqueId(), player.getName());
        plugin.getCheckManager().scheduleCheck(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        plugin.getCheckManager().unregister(event.getPlayer().getUniqueId());
    }
}
