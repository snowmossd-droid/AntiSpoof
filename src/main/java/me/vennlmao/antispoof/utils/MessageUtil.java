package me.vennlmao.antispoof.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageUtil {

    public static String color(String msg) {
        return msg.replace("&", "§");
    }

    public static Component toComponent(String legacyMsg) {
        return LegacyComponentSerializer.legacySection().deserialize(color(legacyMsg));
    }
}
