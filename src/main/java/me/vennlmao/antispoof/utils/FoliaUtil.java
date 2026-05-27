package me.vennlmao.antispoof.utils;

import me.vennlmao.antispoof.AntiSpoof;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FoliaUtil {

    private static final boolean FOLIA;

    static {
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        FOLIA = folia;
    }

    public static void runLater(AntiSpoof plugin, Player player, Runnable task, long delayTicks) {
        if (FOLIA) {
            player.getScheduler().runDelayed(plugin, st -> task.run(), null, delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    public static void runForPlayer(AntiSpoof plugin, Player player, Runnable task) {
        if (FOLIA) {
            player.getScheduler().run(plugin, st -> task.run(), null);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void runGlobal(AntiSpoof plugin, Runnable task) {
        if (FOLIA) {
            Bukkit.getGlobalRegionScheduler().run(plugin, st -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void runAsync(AntiSpoof plugin, Runnable task) {
        if (FOLIA) {
            Bukkit.getAsyncScheduler().runNow(plugin, st -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }
}
