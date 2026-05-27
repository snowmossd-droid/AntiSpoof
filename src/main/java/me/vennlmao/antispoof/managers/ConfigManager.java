package me.vennlmao.antispoof.managers;

import me.vennlmao.antispoof.AntiSpoof;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConfigManager {
    private final AntiSpoof plugin;
    private String kickMessage, alertMessage, bedrockPrefix;
    private int checkDelayTicks;
    private boolean logEnabled, vanillaSpoofCheck, geyserSpoofCheck, forgeCheck, fabricCheck;
    private List<Pattern> vanillaBrandPatterns, safeChannelPatterns, forgeBrandPatterns;
    private List<Pattern> forgeRequiredChannels, fabricBrandPatterns, fabricRequiredChannels;
    private List<Pattern> blacklistedBrands, blacklistedChannels;

    public ConfigManager(AntiSpoof plugin) { this.plugin = plugin; load(); }

    public void load() {
        plugin.reloadConfig();
        FileConfiguration c = plugin.getConfig();
        kickMessage = c.getString("kick-message");
        alertMessage = c.getString("alert-message");
        checkDelayTicks = c.getInt("check-delay-ticks");
        logEnabled = c.getBoolean("log-enabled");
        vanillaSpoofCheck = c.getBoolean("vanilla-spoof-check");
        geyserSpoofCheck = c.getBoolean("geyser-spoof-check");
        bedrockPrefix = c.getString("bedrock-prefix");
        forgeCheck = c.getBoolean("forge-consistency-check");
        fabricCheck = c.getBoolean("fabric-consistency-check");
        vanillaBrandPatterns = compile(c.getStringList("vanilla-brand-patterns"));
        safeChannelPatterns = compile(c.getStringList("safe-channels"));
        forgeBrandPatterns = compile(c.getStringList("forge-brand-patterns"));
        forgeRequiredChannels = compile(c.getStringList("forge-required-channels"));
        fabricBrandPatterns = compile(c.getStringList("fabric-brand-patterns"));
        fabricRequiredChannels = compile(c.getStringList("fabric-required-channels"));
        blacklistedBrands = compile(c.getStringList("blacklisted-brands"));
        blacklistedChannels = compile(c.getStringList("blacklisted-channels"));
    }

    private List<Pattern> compile(List<String> list) {
        return list.stream().map(Pattern::compile).collect(Collectors.toList());
    }

    public boolean matches(List<Pattern> patterns, String input) {
        if (input == null) return false;
        for (Pattern p : patterns) if (p.matcher(input).find()) return true;
        return false;
    }

    public boolean anyMatch(List<Pattern> patterns, Iterable<String> inputs) {
        for (String i : inputs) if (matches(patterns, i)) return true;
        return false;
    }

    public String getKickMessage() { return kickMessage; }
    public String getAlertMessage() { return alertMessage; }
    public int getCheckDelayTicks() { return checkDelayTicks; }
    public boolean isLogEnabled() { return logEnabled; }
    public boolean isVanillaSpoofCheck() { return vanillaSpoofCheck; }
    public boolean isGeyserSpoofCheck() { return geyserSpoofCheck; }
    public String getBedrockPrefix() { return bedrockPrefix; }
    public boolean isForgeConsistencyCheck() { return forgeCheck; }
    public boolean isFabricConsistencyCheck() { return fabricCheck; }
    public List<Pattern> getVanillaBrandPatterns() { return vanillaBrandPatterns; }
    public List<Pattern> getSafeChannelPatterns() { return safeChannelPatterns; }
    public List<Pattern> getForgeBrandPatterns() { return forgeBrandPatterns; }
    public List<Pattern> getForgeRequiredChannels() { return forgeRequiredChannels; }
    public List<Pattern> getFabricBrandPatterns() { return fabricBrandPatterns; }
    public List<Pattern> getFabricRequiredChannels() { return fabricRequiredChannels; }
    public List<Pattern> getBlacklistedBrands() { return blacklistedBrands; }
    public List<Pattern> getBlacklistedChannels() { return blacklistedChannels; }
}
