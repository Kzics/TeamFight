package fr.sweeftyz.teamfights.utils.scoreboard;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.Teams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class GameTablist {

    private final Main instance;

    public GameTablist(final Main instance){
        this.instance = instance;

    }

    public void updateTabListColor(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = null;

        if (instance.getTeamsManager().isInTeam(Teams.RED,player)) {
            team = scoreboard.getTeam("red");
            if (team == null) {
                team = scoreboard.registerNewTeam("red");
                team.setPrefix(ChatColor.RED + "");
            }
        } else if (instance.getTeamsManager().isInTeam(Teams.BLUE,player)) {
            team = scoreboard.getTeam("blue");
            if (team == null) {
                team = scoreboard.registerNewTeam("blue");
                team.setPrefix(ChatColor.BLUE + "");
            }
        }

        if (team != null) {
            team.addEntry(player.getName());
        }
    }


}
