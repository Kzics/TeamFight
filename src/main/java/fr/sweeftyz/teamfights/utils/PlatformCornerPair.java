package fr.sweeftyz.teamfights.utils;

import fr.sweeftyz.teamfights.Main;
import org.bukkit.Location;

public class PlatformCornerPair {


    private final Location corner1;
    private final Location corner2;
    private final Main instance;
    public PlatformCornerPair(final Main instance) {
        this.instance = instance;
        this.corner1 = new Location(instance.getServer().getWorld("world"),-1061,2,-127);
        this.corner2 = new Location(instance.getServer().getWorld("world"),-1042,8,-93);
    }

    public Location getFirstCorner(){
        return this.corner1;
    }

    public Location getSecondCorner(){
        return this.corner2;
    }


}
