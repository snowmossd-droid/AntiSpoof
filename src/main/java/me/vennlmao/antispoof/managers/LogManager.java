package me.vennlmao.antispoof.managers;

import me.vennlmao.antispoof.AntiSpoof;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogManager {

    private final AntiSpoof plugin;
    private PrintWriter writer;
    private final SimpleDateFormat lineFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat fileFormat = new SimpleDateFormat("yyyy-MM-dd");

    public LogManager(AntiSpoof plugin) {
        this.plugin = plugin;
        if (!plugin.getConfigManager().isLogEnabled()) return;
        try {
            File dir = new File(plugin.getDataFolder(), "logs");
            dir.mkdirs();
            File logFile = new File(dir, "detections-" + fileFormat.format(new Date()) + ".log");
            writer = new PrintWriter(new FileWriter(logFile, true), true);
        } catch (IOException e) {
            plugin.getLogger().warning("Cannot open log file: " + e.getMessage());
        }
    }

    public void log(String player, String client, String brand, String channels, String reason) {
        String line = "[" + lineFormat.format(new Date()) + "]"
                + " player=" + player
                + " client=" + client
                + " brand=" + brand
                + " channels=" + channels
                + " reason=" + reason;
        plugin.getLogger().info(line);
        if (writer != null) writer.println(line);
    }

    public void close() {
        if (writer != null) writer.close();
    }
}
