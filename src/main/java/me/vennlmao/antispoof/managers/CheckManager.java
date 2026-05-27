package me.vennlmao.antispoof.managers;

import me.vennlmao.antispoof.AntiSpoof;
import me.vennlmao.antispoof.models.DetectionResult;
import me.vennlmao.antispoof.models.PlayerData;
import me.vennlmao.antispoof.utils.FoliaUtil;
import me.vennlmao.antispoof.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CheckManager {
    private final AntiSpoof plugin;
    private final Map<UUID, PlayerData> dataMap = new ConcurrentHashMap<>();

    public CheckManager(AntiSpoof plugin) { this.plugin = plugin; }

    public void register(UUID uuid, String name) { dataMap.put(uuid, new PlayerData(uuid, name)); }
    public void unregister(UUID uuid) { dataMap.remove(uuid); }
    public PlayerData getData(UUID uuid) { return dataMap.get(uuid); }

    public void scheduleCheck(Player player) {
        FoliaUtil.runLater(plugin, player, () -> runCheck(player), plugin.getConfigManager().getCheckDelayTicks());
    }

    public void runCheck(Player player) {
        if (player == null || !player.isOnline()) return;
        if (player.hasPermission("antispoof.bypass")) return;
        PlayerData data = dataMap.get(player.getUniqueId());
        if (data == null || data.isChecked()) return;
        data.setChecked(true);
        DetectionResult result = analyze(player, data);
        if (result.isDetected()) handleDetection(player, data, result);
    }

    private DetectionResult analyze(Player player, PlayerData data) {
        ConfigManager cfg = plugin.getConfigManager();
        String brand = data.getBrand();
        Set<String> channels = data.getChannels();

        if (cfg.isGeyserSpoofCheck()) {
            if (!player.getName().startsWith(cfg.getBedrockPrefix()) && brand.equalsIgnoreCase("Geyser"))
                return DetectionResult.flag("Geyser Spoof", "Java player spoofing Bedrock brand");
        }

        if (cfg.matches(cfg.getBlacklistedBrands(), brand))
            return DetectionResult.flag(brand, "Blacklisted client brand");

        for (String ch : channels)
            if (cfg.matches(cfg.getBlacklistedChannels(), ch))
                return DetectionResult.flag(ch, "Blacklisted plugin channel: " + ch);

        if (cfg.isVanillaSpoofCheck() && cfg.matches(cfg.getVanillaBrandPatterns(), brand) && !channels.isEmpty()) {
            boolean suspicious = channels.stream().anyMatch(c -> !cfg.matches(cfg.getSafeChannelPatterns(), c));
            if (suspicious) return DetectionResult.flag("Vanilla Spoof", "Vanilla brand with mod channels: " + channels);
        }

        if (cfg.isForgeConsistencyCheck() && cfg.matches(cfg.getForgeBrandPatterns(), brand)
                && !cfg.anyMatch(cfg.getForgeRequiredChannels(), channels) && !channels.isEmpty())
            return DetectionResult.flag("Forge Spoof", "Forge brand but no Forge channels");

        if (cfg.isFabricConsistencyCheck() && cfg.matches(cfg.getFabricBrandPatterns(), brand)
                && !cfg.anyMatch(cfg.getFabricRequiredChannels(), channels) && !channels.isEmpty())
            return DetectionResult.flag("Fabric Spoof", "Fabric brand but no Fabric channels");

        return DetectionResult.clean();
    }

    private void handleDetection(Player player, PlayerData data, DetectionResult result) {
        ConfigManager cfg = plugin.getConfigManager();
        String brand = data.getBrand();
        String channels = data.getChannels().toString();
        plugin.getLogManager().log(player.getName(), result.getClientName(), brand, channels, result.getReason());

        String kickMsg = cfg.getKickMessage()
                .replace("{client}", result.getClientName())
                .replace("{brand}", brand)
                .replace("{reason}", result.getReason());
        String alertMsg = MessageUtil.color(cfg.getAlertMessage()
                .replace("{player}", player.getName())
                .replace("{client}", result.getClientName())
                .replace("{brand}", brand)
                .replace("{reason}", result.getReason()));

        FoliaUtil.runGlobal(plugin, () -> {
            Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p.hasPermission("antispoof.notify"))
                    .forEach(p -> p.sendMessage(alertMsg));
            Bukkit.getConsoleSender().sendMessage(alertMsg);
        });

        FoliaUtil.runForPlayer(plugin, player, () -> {
            if (player.isOnline()) player.kick(MessageUtil.toComponent(kickMsg));
        });
    }
}
