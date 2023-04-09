package fr.sweeftyz.teamfights.events.listeners;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.Round;
import fr.sweeftyz.teamfights.enums.DeathType;
import fr.sweeftyz.teamfights.enums.GameState;
import fr.sweeftyz.teamfights.enums.Teams;
import fr.sweeftyz.teamfights.events.listeners.custom.NextRoundEvent;
import fr.sweeftyz.teamfights.manager.GameManager;
import fr.sweeftyz.teamfights.utils.ActionMessage;
import fr.sweeftyz.teamfights.utils.ColorsUtil;
import fr.sweeftyz.teamfights.utils.MiscUtils;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityVelocity;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class PlayerFight implements Listener {


    private final Main instance;
    private final GameManager gameManager;
    public PlayerFight(final Main instance){
        this.instance = instance;
        this.gameManager = instance.getGameManager();
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e){
        GameManager gameManager = instance.getGameManager();
        System.out.println(e.getCause());
        if(instance.getStateManager().isState(GameState.PREROUND) || instance.getStateManager().isState(GameState.WAITING)
                || instance.getStateManager().isState(GameState.STARTING)){
            e.setCancelled(true);

            return;
        }

        if(instance.getGameManager().getRound().getDeadPlayers().contains(e.getDamager().getUniqueId()) || instance.getGameManager().getGameSpectator().contains(e.getDamager().getUniqueId())){
            e.setCancelled(true);
        }


        if(instance.getStateManager().isState(GameState.PLAYING)) {
            if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
                Player damaged = (Player) e.getEntity();
                Player damager = (Player) e.getDamager();
                Teams deathTeam = instance.getTeamsManager().getTeamFromPlayer(damaged);
                Teams attackerTeam = instance.getTeamsManager().getTeamFromPlayer(damager);
                Round round = this.gameManager.getRound();

                if(!gameManager.getRound().allPlayers().contains(damaged.getUniqueId())){
                    e.setCancelled(true);

                    return;
                }

                if(attackerTeam.getTeamName().equals(deathTeam.getTeamName())) {
                    e.setCancelled(true);
                    return;
                }

                instance.getGameManager().getPlayerStatsManager().addHits(damager.getUniqueId());
                instance.getGameManager().getRound().addStatsToIndex(damager,1);

                gameManager.getRound().getLastHit().put(damaged.getUniqueId(), damager.getUniqueId());

                System.out.println(damaged.getHealth() - e.getFinalDamage());
                if (damaged.getHealth() - e.getFinalDamage() <= 0) {

                    if (e.getDamager() instanceof Player) {
                        instance.getServer().broadcastMessage(ColorsUtil.translate.apply(deathTeam.getTeamColorCode() + damaged.getName() + "&7 a été tué par " +attackerTeam.getTeamColorCode()+ damager.getName()));
                        round.removeAlivePlayers(damaged);

                        new ActionMessage(ColorsUtil.translate.apply("&c&lVous êtes eliminé"))
                                .sendTitle(damaged,10,70,5);

                        round.getDeadPlayers()
                                .add(damaged.getUniqueId());
                    }

                    gameManager.getRound().getLastHit().remove(damaged.getUniqueId());

                    damaged.getInventory().setArmorContents(null);

                    instance.getGameManager().getPlayerStatsManager().addDeaths(damaged.getUniqueId());
                    instance.getGameManager().getPlayerStatsManager().addKills(damager.getUniqueId());
                    instance.getGameManager().getRound().addStatsToIndex(damager,0);

                    if (gameManager.getRound().getAlivePlayersFrom(deathTeam).isEmpty()) {
                        System.out.println(gameManager.getRound().getAlivePlayersFrom(deathTeam));

                        NextRoundEvent nextRoundEvent = new NextRoundEvent(deathTeam, instance.getGameManager().getWinMode(),damaged.getWorld());
                        instance.getServer().getPluginManager().callEvent(nextRoundEvent);
                    }else{
                        gameManager.getTablistManager().clearTabList();
                        gameManager.getTablistManager().createGameTab();
                    }

                    gameManager.checkAce(deathTeam);

                }

            } else if(e.getDamager() instanceof Monster){
                e.setCancelled(true);
            }
        }
    }
    private org.bukkit.util.Vector getVector(Player victim, Player attacker) {
        org.bukkit.util.Vector vector = victim.getLocation().toVector().subtract(attacker.getLocation().toVector()).setY(0);
        return Math.sqrt(Math.pow(vector.getX(), 2.0D) + Math.pow(vector.getZ(), 2.0D)) <= 0.5D ? attacker.getEyeLocation().getDirection().setY(0).normalize() : vector.normalize();
    }
    @EventHandler
    public void nextRound(NextRoundEvent e){
        instance.getServer().getScheduler().runTaskLater(instance,()->{
            GameManager gameManager = instance.getGameManager();
            if(e.getLastWinnerTeam() == null){
                MiscUtils.sendEndRoundMessage(instance,e.getWinMode(),null);
                instance.getBlocksLimitHandler().resetMap(e.getRoundWorld().getSpawnLocation());
                gameManager.nextRound(e.getWinMode());


                return;
            }


            Teams lostTeam = e.getLastWinnerTeam();
            Teams winnerTeam = Teams.getInvertTeam(lostTeam);

            if(lostTeam.equals(Teams.RED)){
                instance.getGameManager().addBluePoints(1);
            }else{
                instance.getGameManager().addRedPoints(1);
            }

            gameManager.clearEntities(e.getRoundWorld());

            MiscUtils.sendEndRoundMessage(instance,e.getWinMode(),winnerTeam);
            instance.getBlocksLimitHandler().resetMap(e.getRoundWorld().getSpawnLocation());

            gameManager.getRound().sendEndMessage();
            gameManager.nextRound(e.getWinMode());
        },1);

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        e.setDeathMessage(null);
        if(instance.getStateManager().isState(GameState.PLAYING)) {
            e.getEntity().setHealth(28);
            MiscUtils.hideFromAll(e.getEntity());
            e.getEntity().setGameMode(GameMode.CREATIVE);
        }
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        if(instance.getStateManager().isState(GameState.PREROUND)) {
            final Location from = e.getFrom();
            final Location to = e.getTo();
            if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
                e.setTo(from);
            }
        }else if(instance.getStateManager().isState(GameState.WAITING) || instance.getStateManager().isState(GameState.STARTING)){
            if(player.getLocation().getY() <= -2){
                player.teleport(new Location(Bukkit.getWorld("world"),-1051,3,-107));

            }
        }

    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent e){
        if(e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
            e.setAmount(0.10);
        }
    }

    @EventHandler
    public void onAnim(PlayerAnimationEvent e){
        if(e.getAnimationType().equals(PlayerAnimationType.ARM_SWING)){
            e.setCancelled(true);
        }
    }

}
