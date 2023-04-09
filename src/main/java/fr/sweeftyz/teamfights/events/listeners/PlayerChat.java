package fr.sweeftyz.teamfights.events.listeners;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.Teams;
import fr.sweeftyz.teamfights.utils.ColorsUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChat implements Listener {


    private final Main instance;
    public PlayerChat(final Main instance){
        this.instance = instance;
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        Player player = e.getPlayer();
        Teams team = instance.getTeamsManager().getTeamFromPlayer(player);

        e.setFormat(ColorsUtil.translate.apply(team.getTeamColorCode() + "%s&7> &f %s"));
    }
}
