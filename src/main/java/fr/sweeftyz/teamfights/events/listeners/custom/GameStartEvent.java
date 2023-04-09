package fr.sweeftyz.teamfights.events.listeners.custom;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.Teams;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameStartEvent extends Event implements Cancellable {
    public static HandlerList hList = new HandlerList();

    private final Main instance;
    public GameStartEvent(final Main instance){
        this.instance = instance;
    }

    public static HandlerList getHandlerList() {
        return hList;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    public List<Player> getBlueTeam(){
        return instance.getTeamsManager()
                .getMembers(Teams.BLUE)
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Player> getRedTeam(){
        return instance.getTeamsManager()
                .getMembers(Teams.RED)
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    @Override
    public void setCancelled(boolean b) {

    }

    @Override
    public HandlerList getHandlers() {
        return hList;
    }
}
