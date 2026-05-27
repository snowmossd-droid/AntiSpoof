package me.vennlmao.antispoof.managers;

import me.vennlmao.antispoof.AntiSpoof;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class LogManager {
    private final Logger logger;
    private PrintWriter writer;
    private final SimpleDateFormat lineFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat fileFmt = new SimpleDateFormat("yyyy-MM-dd");

    public LogManager(AntiSpoof plugin) {
        this.logger = plugin.getLogger();
        if (!plugin.getConfigManager().isLogEnabled()) return;
        try {
            File dir = new File(plugin.getDataFolder(), "logs");
            dir.mkdirs();
            writer = new PrintWriter(new FileWriter(new File(dir, "detections-" + fileFmt.format(new Date()) + ".log"), true), true);
        } catch (IOException e) { logger.warning("Cannot open log: " + e.getMessage()); }
    }

    public void log(String player, String client, String brand, String channels, String reason) {
        String line = "[" + lineFmt.format(new Date()) + "] player=" + player + " client=" + client + " brand=" + brand + " channels=" + channels + " reason=" + reason;
        logger.info(line);
        if (writer != null) writer.println(line);
    }

    public void close() { if (writer != null) writer.close(); }
}
