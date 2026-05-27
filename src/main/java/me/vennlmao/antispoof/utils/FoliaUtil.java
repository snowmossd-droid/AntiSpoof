package me.vennlmao.antispoof.utils;

import me.vennlmao.antispoof.AntiSpoof;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FoliaUtil {
    private static final boolean FOLIA;
    static {
        boolean f;
        try { Class.forName("io.papermc.paper.threadedregions.RegionizedServer"); f = true; }
        catch (ClassNotFoundException e) { f = false; }
        FOLIA = f;
    }

    public static void runLater(AntiSpoof plugin, Player player, Runnable task, long delay) {
        if (FOLIA) player.getScheduler().runDelayed(plugin, st -> task.run(), null, delay);
        else Bukkit.getScheduler().runTaskLater(plugin, task, delay);
    }
    public static void runForPlayer(AntiSpoof plugin, Player player, Runnable task) {
        if (FOLIA) player.getScheduler().run(plugin, st -> task.run(), null);
        else Bukkit.getScheduler().runTask(plugin, task);
    }
    public static void runGlobal(AntiSpoof plugin, Runnable task) {
        if (FOLIA) Bukkit.getGlobalRegionScheduler().run(plugin, st -> task.run());
        else Bukkit.getScheduler().runTask(plugin, task);
    }
}
