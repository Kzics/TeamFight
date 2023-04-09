package fr.sweeftyz.teamfights.events.listeners.custom;

import fr.sweeftyz.teamfights.enums.Teams;
import fr.sweeftyz.teamfights.enums.WinMode;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NextRoundEvent extends Event implements Cancellable {


    private final Teams lastLostTeam;
    public static HandlerList hList = new HandlerList();

    private final WinMode winMode;
    private final World roundWorld;


    public NextRoundEvent(Teams lastLostTeam, WinMode winMode, World world){
        this.lastLostTeam = lastLostTeam;
        this.winMode = winMode;
        this.roundWorld = world;

    }

    public static HandlerList getHandlerList() {
        return hList;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean b) {

    }

    @Override
    public HandlerList getHandlers() {
        return hList;
    }

    public Teams getLastWinnerTeam() {
        return lastLostTeam;
    }

    public WinMode getWinMode() {
        return winMode;
    }

    public World getRoundWorld() {
        return roundWorld;
    }
}
