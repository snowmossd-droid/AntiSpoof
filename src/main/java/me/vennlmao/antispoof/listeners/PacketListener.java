package me.vennlmao.antispoof.listeners;

import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import me.vennlmao.antispoof.AntiSpoof;
import me.vennlmao.antispoof.models.PlayerData;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;

public class PacketListener extends PacketListenerAbstract {

    private final AntiSpoof plugin;

    public PacketListener(AntiSpoof plugin) {
        super(PacketListenerPriority.LOWEST);
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        PlayerData data = plugin.getCheckManager().getData(player.getUniqueId());
        if (data == null) return;
        if (event.getPacketType() != PacketType.Play.Client.PLUGIN_MESSAGE) return;

        try {
            WrapperPlayClientPluginMessage wrapper = new WrapperPlayClientPluginMessage(event);
            String channel = wrapper.getChannelName();
            byte[] payload = wrapper.getData();

            if (channel.equals("minecraft:brand") || channel.equals("MC|Brand")) {
                String brand = decodeBrand(payload);
                data.setBrand(brand);
                if (data.isChecked()) {
                    data.setChecked(false);
                    plugin.getCheckManager().scheduleCheck(player);
                }
            } else {
                data.addChannel(channel);
                if (data.isChecked()) {
                    data.setChecked(false);
                    plugin.getCheckManager().scheduleCheck(player);
                }
            }
        } catch (Exception ignored) {}
    }

    private String decodeBrand(byte[] raw) {
        if (raw == null || raw.length == 0) return "";
        try {
            int offset = 0;
            int value = 0;
            int shift = 0;
            byte b;
            do {
                if (offset >= raw.length) break;
                b = raw[offset++];
                value |= (b & 0x7F) << shift;
                shift += 7;
            } while ((b & 0x80) != 0);
            int len = Math.min(value, raw.length - offset);
            if (len <= 0) return new String(raw, StandardCharsets.UTF_8).trim();
            return new String(raw, offset, len, StandardCharsets.UTF_8).trim();
        } catch (Exception e) {
            return new String(raw, StandardCharsets.UTF_8).trim();
        }
    }
}
