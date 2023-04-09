package fr.sweeftyz.teamfights.events;

import fr.sweeftyz.teamfights.Main;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.Arrays;

public class ListenerManager {
    private final Main instance;
    public ListenerManager(final Main instance){
        this.instance = instance;
    }


    public void registerEvents(Listener... listeners){
        PluginManager pluginManager = instance.getServer().getPluginManager();

        Arrays.stream(listeners)
                .forEach(listener->pluginManager.registerEvents(listener,instance));
    }

}
