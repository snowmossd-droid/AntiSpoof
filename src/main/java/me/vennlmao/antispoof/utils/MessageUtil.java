package me.vennlmao.antispoof.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class MessageUtil {
    public static String color(String msg) {
        return msg.replace("&", "\u00a7");
    }
    public static Component toComponent(String msg) {
        return LegacyComponentSerializer.legacySection().deserialize(color(msg));
    }
}
