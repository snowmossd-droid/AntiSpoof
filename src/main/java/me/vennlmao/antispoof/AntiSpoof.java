package me.vennlmao.antispoof;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import me.vennlmao.antispoof.commands.AntiSpoofCommand;
import me.vennlmao.antispoof.listeners.BukkitJoinListener;
import me.vennlmao.antispoof.listeners.PacketListener;
import me.vennlmao.antispoof.managers.CheckManager;
import me.vennlmao.antispoof.managers.ConfigManager;
import me.vennlmao.antispoof.managers.LogManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiSpoof extends JavaPlugin {

    private static AntiSpoof instance;
    private ConfigManager configManager;
    private CheckManager checkManager;
    private LogManager logManager;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        logManager = new LogManager(this);
        checkManager = new CheckManager(this);
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListener(this));
        PacketEvents.getAPI().init();
        getServer().getPluginManager().registerEvents(new BukkitJoinListener(this), this);
        getCommand("antispoof").setExecutor(new AntiSpoofCommand(this));
        getLogger().info("AntiSpoof enabled.");
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        if (logManager != null) logManager.close();
    }

    public static AntiSpoof getInstance() { return instance; }
    public ConfigManager getConfigManager() { return configManager; }
    public CheckManager getCheckManager() { return checkManager; }
    public LogManager getLogManager() { return logManager; }
}
