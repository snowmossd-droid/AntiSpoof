package me.vennlmao.antispoof.models;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {

    private final UUID uuid;
    private final String name;
    private String brand = "";
    private final Set<String> channels = new LinkedHashSet<>();
    private boolean brandReceived = false;
    private boolean checked = false;

    public PlayerData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; this.brandReceived = true; }
    public Set<String> getChannels() { return channels; }
    public void addChannel(String channel) { channels.add(channel); }
    public boolean isBrandReceived() { return brandReceived; }
    public boolean isChecked() { return checked; }
    public void setChecked(boolean v) { this.checked = v; }
}
