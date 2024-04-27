package ch.ksrminecraft.entityplugin;

import ch.ksrminecraft.entityplugin.listeners.SpwanListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class EntityPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new SpwanListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
