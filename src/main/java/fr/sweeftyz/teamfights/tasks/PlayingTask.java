package fr.sweeftyz.teamfights.tasks;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.Round;
import fr.sweeftyz.teamfights.enums.DeathType;
import fr.sweeftyz.teamfights.enums.GameState;
import fr.sweeftyz.teamfights.enums.Teams;
import fr.sweeftyz.teamfights.enums.WinMode;
import fr.sweeftyz.teamfights.events.listeners.custom.NextRoundEvent;
import fr.sweeftyz.teamfights.manager.GameManager;
import fr.sweeftyz.teamfights.manager.TablistManager;
import fr.sweeftyz.teamfights.utils.ActionMessage;
import fr.sweeftyz.teamfights.utils.ColorsUtil;
import fr.sweeftyz.teamfights.utils.MiscUtils;
import fr.sweeftyz.teamfights.utils.scoreboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlayingTask extends BukkitRunnable {

    private boolean running;
    private static int timer;
    private final Main instance;
    public PlayingTask(final Main instance){
        this.running = false;
        timer = 0;
        this.instance = instance;
    }
    @Override
    public void run() {


        if(instance.getServer().getOnlinePlayers().size() == 0 ) instance.getServer().shutdown();

        String convertedTime = MiscUtils.convertSeconds(timer);

        if(timer == 0) {
            instance.getGameManager().start();
            instance.getGameManager().getTablistManager().createGameTab();
        }

        for (Player player : Bukkit.getOnlinePlayers()){
            GameManager gameManager = instance.getGameManager();
            Round round = gameManager.getRound();
            if(player.getLocation().getY() < -2 && !gameManager.getRound().getDeadPlayers().contains(player.getUniqueId())){
                Teams deathTeam = instance.getTeamsManager().getTeamFromPlayer(player);

                if(gameManager.getRound().getLastHit().get(player.getUniqueId()) == null) {
                    round.removeAlivePlayers(player);

                    new ActionMessage(ColorsUtil.translate.apply("&c&lVous êtes eliminé"))
                            .sendTitle(player,10,70,5);

                    round.getDeadPlayers()
                            .add(player.getUniqueId());
                    instance.getServer().broadcastMessage(ColorsUtil.translate.apply(deathTeam.getTeamColorCode() + player.getName() + "&7 a été tué par le vide ! "));
                    instance.getGameManager().getPlayerStatsManager().addDeaths(player.getUniqueId());

                }else{
                    Teams attackerTeam = instance.getTeamsManager().getTeamFromPlayer(gameManager.getRound().getLastHit().get(player.getUniqueId()));

                    instance.getServer().broadcastMessage(ColorsUtil.translate.apply(deathTeam.getTeamColorCode() + player.getName() + "&7 a été tué par le vide et "+ attackerTeam.getTeamColorCode() +
                            Bukkit.getPlayer(gameManager.getRound().getLastHit().get(player.getUniqueId())).getName()));

                    instance.getGameManager().getPlayerStatsManager().addDeaths(player.getUniqueId());
                    instance.getGameManager().getPlayerStatsManager().addKills(gameManager.getRound().getLastHit().get(player.getUniqueId()));

                    round.removeAlivePlayers(player);

                    new ActionMessage(ColorsUtil.translate.apply("&c&lVous êtes eliminé"))
                            .sendTitle(player,10,70,5);

                    round.getDeadPlayers()
                            .add(player.getUniqueId());
                    gameManager.getRound().getLastHit().remove(player.getUniqueId());
                    player.getInventory().setArmorContents(null);

                }
                player.setGameMode(GameMode.CREATIVE);
                player.setFlying(true);
                MiscUtils.hideFromAll(player);
                player.getInventory().clear();
                if (gameManager.getRound().getAlivePlayersFrom(deathTeam).isEmpty()) {
                    NextRoundEvent nextRoundEvent = new NextRoundEvent(deathTeam, instance.getGameManager().getWinMode(),player.getWorld());
                    instance.getServer().getPluginManager().callEvent(nextRoundEvent);
                }else{
                    gameManager.getTablistManager().clearTabList();
                    gameManager.getTablistManager().createGameTab();
                }
            }

            FastBoard board = instance.getFastBoardHashMap().get(player.getUniqueId());
            Teams playerTeam = instance.getTeamsManager().getTeamFromPlayer(player);

            board.updateTitle(ColorsUtil.translate.apply("&l&3Team&e&lFights"));

            board.updateLines(ColorsUtil.translate.apply("&7&m--------------------"),
                    "",
                    ColorsUtil.translate.apply(String.format("➔&eÉquipe :%s %s",playerTeam.getTeamColorCode(),playerTeam.getTeamName())),
                    "",
                    ColorsUtil.translate.apply("&1&l>> " + MiscUtils.getProgressBar(instance.getGameManager().getBluePoints(),
                            5,5,MiscUtils.getPointSymbol(), ChatColor.BLUE,ChatColor.GRAY)),
                    ColorsUtil.translate.apply("&c&l>> " + MiscUtils.getProgressBar(instance.getGameManager().getRedPoints(),
                            5,5,MiscUtils.getPointSymbol(), ChatColor.RED,ChatColor.GRAY)),
                    "",
                    ColorsUtil.translate.apply("➔&3Manche &e" + instance.getGameManager().getCurrentRound()),
                    ColorsUtil.translate.apply("➔&3Chrono:&e" + convertedTime),
                    ColorsUtil.translate.apply("➔&3Status: &e" + MiscUtils.stateTranslator(instance.getStateManager().getCurrentState())),
                    "",
                    ColorsUtil.translate.apply("&7&m--------------------"),
                    "ipduserv.fr");
        }

        if(instance.getStateManager().isState(GameState.PREROUND)) {
            if (instance.getGameManager().checkWinner()) {
                instance.getStateManager().setGameState(GameState.PLAYING);
            } else {
                if (!instance.getGameManager().isPreRound()) {
                    instance.getGameManager().setPreRound(true);
                    instance.getServer().getScheduler().runTaskLater(instance,()->{
                        instance.getTasksManager().runPreRoundTask();

                    },10);
                }
                return;
            }
        }

        if(instance.getGameManager().checkWinner()){
            Teams winner = instance.getGameManager().getWinner();
            String teamColorCode = winner.getTeamColorCode();
            String teamName = winner.getTeamName();

            for (Player player : Bukkit.getOnlinePlayers()) {
                new ActionMessage("&a2Les gagnants sont l'équipe " + teamColorCode + teamName)
                        .sendTitle(player,5,70,5);

                instance.getGameManager().sendEndMessage(player);

            }

            instance.getServer().getScheduler().runTaskLaterAsynchronously(instance,()->{
                instance.getServer().shutdown();
            },250);

            instance.getStateManager().setGameState(GameState.END);

            this.cancel();
            return;
        }


        timer++;
    }

    public boolean isRunning(){
        return this.running;
    }

    public static int getTimer(){
        return timer;

    }
}
