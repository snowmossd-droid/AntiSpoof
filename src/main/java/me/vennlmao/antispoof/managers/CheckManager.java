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

    public CheckManager(AntiSpoof plugin) {
        this.plugin = plugin;
    }

    public void register(UUID uuid, String name) {
        dataMap.put(uuid, new PlayerData(uuid, name));
    }

    public void unregister(UUID uuid) {
        dataMap.remove(uuid);
    }

    public PlayerData getData(UUID uuid) {
        return dataMap.get(uuid);
    }

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
        if (result.isDetected()) {
            handleDetection(player, data, result);
        }
    }

    private DetectionResult analyze(Player player, PlayerData data) {
        ConfigManager cfg = plugin.getConfigManager();
        String brand = data.getBrand();
        Set<String> channels = data.getChannels();

        if (cfg.isGeyserSpoofCheck()) {
            boolean claimsBedrock = player.getName().startsWith(cfg.getBedrockPrefix());
            if (!claimsBedrock && brand.equalsIgnoreCase("Geyser")) {
                return DetectionResult.flag("Geyser Spoof", "Java player spoofing Bedrock brand");
            }
        }

        if (cfg.matches(cfg.getBlacklistedBrands(), brand)) {
            return DetectionResult.flag(brand, "Blacklisted client brand");
        }

        for (String channel : channels) {
            if (cfg.matches(cfg.getBlacklistedChannels(), channel)) {
                return DetectionResult.flag(channel, "Blacklisted plugin channel: " + channel);
            }
        }

        if (cfg.isVanillaSpoofCheck()) {
            boolean isVanillaBrand = cfg.matches(cfg.getVanillaBrandPatterns(), brand);
            if (isVanillaBrand && !channels.isEmpty()) {
                boolean hasSuspiciousChannel = channels.stream()
                        .anyMatch(c -> !cfg.matches(cfg.getSafeChannelPatterns(), c));
                if (hasSuspiciousChannel) {
                    return DetectionResult.flag("Vanilla Spoof", "Vanilla brand but has mod channels: " + channels);
                }
            }
        }

        if (cfg.isForgeConsistencyCheck()) {
            boolean isForge = cfg.matches(cfg.getForgeBrandPatterns(), brand);
            boolean hasForgeChannel = cfg.anyMatch(cfg.getForgeRequiredChannels(), channels);
            if (isForge && !hasForgeChannel && !channels.isEmpty()) {
                return DetectionResult.flag("Forge Spoof", "Claims Forge brand but no Forge channels");
            }
        }

        if (cfg.isFabricConsistencyCheck()) {
            boolean isFabric = cfg.matches(cfg.getFabricBrandPatterns(), brand);
            boolean hasFabricChannel = cfg.anyMatch(cfg.getFabricRequiredChannels(), channels);
            if (isFabric && !hasFabricChannel && !channels.isEmpty()) {
                return DetectionResult.flag("Fabric Spoof", "Claims Fabric brand but no Fabric channels");
            }
        }

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
            if (player.isOnline()) {
                player.kick(MessageUtil.toComponent(kickMsg));
            }
        });
    }
}
