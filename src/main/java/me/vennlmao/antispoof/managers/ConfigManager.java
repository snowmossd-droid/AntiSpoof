package me.vennlmao.antispoof.managers;

import me.vennlmao.antispoof.AntiSpoof;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConfigManager {

    private final AntiSpoof plugin;

    private String kickMessage;
    private String alertMessage;
    private int checkDelayTicks;
    private boolean logEnabled;

    private boolean vanillaSpoofCheck;
    private boolean geyserSpoofCheck;
    private String bedrockPrefix;

    private boolean forgeConsistencyCheck;
    private boolean fabricConsistencyCheck;

    private List<Pattern> vanillaBrandPatterns;
    private List<Pattern> safeChannelPatterns;
    private List<Pattern> forgeBrandPatterns;
    private List<Pattern> forgeRequiredChannels;
    private List<Pattern> fabricBrandPatterns;
    private List<Pattern> fabricRequiredChannels;
    private List<Pattern> blacklistedBrands;
    private List<Pattern> blacklistedChannels;

    public ConfigManager(AntiSpoof plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.reloadConfig();

        kickMessage = plugin.getConfig().getString("kick-message");
        alertMessage = plugin.getConfig().getString("alert-message");
        checkDelayTicks = plugin.getConfig().getInt("check-delay-ticks");
        logEnabled = plugin.getConfig().getBoolean("log-enabled");

        vanillaSpoofCheck = plugin.getConfig().getBoolean("vanilla-spoof-check");
        geyserSpoofCheck = plugin.getConfig().getBoolean("geyser-spoof-check");
        bedrockPrefix = plugin.getConfig().getString("bedrock-prefix");

        forgeConsistencyCheck = plugin.getConfig().getBoolean("forge-consistency-check");
        fabricConsistencyCheck = plugin.getConfig().getBoolean("fabric-consistency-check");

        vanillaBrandPatterns = compile(plugin.getConfig().getStringList("vanilla-brand-patterns"));
        safeChannelPatterns = compile(plugin.getConfig().getStringList("safe-channels"));
        forgeBrandPatterns = compile(plugin.getConfig().getStringList("forge-brand-patterns"));
        forgeRequiredChannels = compile(plugin.getConfig().getStringList("forge-required-channels"));
        fabricBrandPatterns = compile(plugin.getConfig().getStringList("fabric-brand-patterns"));
        fabricRequiredChannels = compile(plugin.getConfig().getStringList("fabric-required-channels"));
        blacklistedBrands = compile(plugin.getConfig().getStringList("blacklisted-brands"));
        blacklistedChannels = compile(plugin.getConfig().getStringList("blacklisted-channels"));
    }

    private List<Pattern> compile(List<String> list) {
        return list.stream().map(Pattern::compile).collect(Collectors.toList());
    }

    public boolean matches(List<Pattern> patterns, String input) {
        if (input == null) return false;
        for (Pattern p : patterns) {
            if (p.matcher(input).find()) return true;
        }
        return false;
    }

    public boolean anyMatch(List<Pattern> patterns, Iterable<String> inputs) {
        for (String input : inputs) {
            if (matches(patterns, input)) return true;
        }
        return false;
    }

    public String getKickMessage() { return kickMessage; }
    public String getAlertMessage() { return alertMessage; }
    public int getCheckDelayTicks() { return checkDelayTicks; }
    public boolean isLogEnabled() { return logEnabled; }
    public boolean isVanillaSpoofCheck() { return vanillaSpoofCheck; }
    public boolean isGeyserSpoofCheck() { return geyserSpoofCheck; }
    public String getBedrockPrefix() { return bedrockPrefix; }
    public boolean isForgeConsistencyCheck() { return forgeConsistencyCheck; }
    public boolean isFabricConsistencyCheck() { return fabricConsistencyCheck; }
    public List<Pattern> getVanillaBrandPatterns() { return vanillaBrandPatterns; }
    public List<Pattern> getSafeChannelPatterns() { return safeChannelPatterns; }
    public List<Pattern> getForgeBrandPatterns() { return forgeBrandPatterns; }
    public List<Pattern> getForgeRequiredChannels() { return forgeRequiredChannels; }
    public List<Pattern> getFabricBrandPatterns() { return fabricBrandPatterns; }
    public List<Pattern> getFabricRequiredChannels() { return fabricRequiredChannels; }
    public List<Pattern> getBlacklistedBrands() { return blacklistedBrands; }
    public List<Pattern> getBlacklistedChannels() { return blacklistedChannels; }
}
