//
// Miners tools メインクラス
//
package me.enchan.miners_tools;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Miners tools
 */
public class MinersTools extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Hello, SpigotMC!");

        // イベントリスナに登録
        getServer().getPluginManager().registerEvents(new PlayerInteractionListener(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("See you again, SpigotMC!");
    }
}
