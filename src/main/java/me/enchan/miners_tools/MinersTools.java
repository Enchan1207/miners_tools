package me.enchan.miners_tools;

import org.bukkit.plugin.java.JavaPlugin;


public class MinersTools extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Hello, SpigotMC!");
    }

    @Override
    public void onDisable() {
        getLogger().info("See you again, SpigotMC!");
    }
}
