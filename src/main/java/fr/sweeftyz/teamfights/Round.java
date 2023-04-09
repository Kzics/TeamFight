package fr.sweeftyz.teamfights;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.Teams;
import fr.sweeftyz.teamfights.manager.PlayerStatsManager;
import fr.sweeftyz.teamfights.tasks.PlayingTask;
import fr.sweeftyz.teamfights.utils.ColorsUtil;
import fr.sweeftyz.teamfights.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Round {

    private final Main instance;
    private final Map<Teams, List<UUID>> alivePlayers;

    private final Map<UUID,UUID> lastHit;

    private final Map<UUID,int[]> roundStats;
    private final int currentRound;
    private final List<UUID> spectatorList;
    private final int startedTime;
    private final PlayerStatsManager playerStatsManager;


    public Round(final Main instance, int currentRound, PlayerStatsManager playerStatsManager){
        this.instance = instance;
        this.alivePlayers = new HashMap<>();
        this.lastHit = new HashMap<>();
        this.playerStatsManager = playerStatsManager;
        this.roundStats = new HashMap<>();
        this.startedTime = PlayingTask.getTimer();

        this.alivePlayers.put(Teams.BLUE,new ArrayList<>());
        this.alivePlayers.put(Teams.RED,new ArrayList<>());

        for(Player player : Bukkit.getOnlinePlayers()){
            MiscUtils.showFromAll(player);

            if(player.isFlying()) player.setFlying(false);

            Teams playerTeam = instance.getTeamsManager().getTeamFromPlayer(player);
            List<UUID> members = alivePlayers.get(playerTeam) == null ? new ArrayList<>() :alivePlayers.get(playerTeam);
            members.add(player.getUniqueId());
            this.alivePlayers.put(playerTeam,members);
        }

        for (Player player : instance.getServer().getOnlinePlayers()) {
            for (Player otherPlayer : instance.getServer().getOnlinePlayers()) {
                if(player.equals(otherPlayer)) continue;

                if (!player.canSee(otherPlayer)) {
                    player.showPlayer(otherPlayer);
                }
            }

            Set<Player> hiddenPlayers = player.spigot().getHiddenPlayers();

            hiddenPlayers.forEach(player::showPlayer);
        }
        this.currentRound = currentRound;
        this.spectatorList = new ArrayList<>();

    }


    public List<Player> getAlivePlayersFrom(Teams team){
        try{
            return this.alivePlayers.get(team)
                    .stream().map(Bukkit::getPlayer)
                    .collect(Collectors.toList());

        }catch (NullPointerException e){
            return new ArrayList<>();
        }
    }

    public void removeAlivePlayers(Player player){
        Teams playerTeam = instance.getTeamsManager().getTeamFromPlayer(player);
        List<UUID> members = this.alivePlayers.get(playerTeam);

        members.remove(player.getUniqueId());
        this.alivePlayers.put(playerTeam,members);
    }

    public Map<UUID, int[]> getRoundStats() {
        return roundStats;
    }

    public void addStatsToIndex(Player player,int index){
        int[] current = this.roundStats.getOrDefault(player.getUniqueId(), new int[2]);

        current[index]++;

        this.roundStats.put(player.getUniqueId(),current);

        System.out.println(Arrays.toString(this.roundStats.get(player.getUniqueId())));
    }

    public boolean isDead(UUID playerUUID){
        return this.getDeadPlayers()
                .stream()
                .anyMatch(uuid -> uuid.equals(playerUUID));
    }

    public List<UUID> allPlayers(){
        List<UUID> red = this.getAlivePlayersFrom(Teams.RED).stream()
                .filter(Objects::nonNull)
                .map(Entity::getUniqueId).collect(Collectors.toList());

        List<UUID> blue = this.getAlivePlayersFrom(Teams.BLUE).stream()
                .filter(Objects::nonNull)
                .map(Entity::getUniqueId).collect(Collectors.toList());

        List<UUID> list = new ArrayList<>(red);
        list.addAll(blue);
        return list;
    }

    public void sendEndMessage(){
        List<UUID> sortedRoundKillers = this.getSortedRoundKillers();
        List<UUID> sortedRoundHits = this.getSortedRoundHits();

        for (Player player : instance.getServer().getOnlinePlayers()){

            player.sendMessage(ColorsUtil.translate.apply("&6&l&nClassement Kills"));
            for(int i = 0;i<sortedRoundKillers.size();i++) {
                Player currentKiller = Objects.nonNull(instance.getServer().getPlayer(sortedRoundKillers.get(i)))
                        ? instance.getServer().getPlayer(sortedRoundKillers.get(i)) : null;
                if(currentKiller == null){
                    player.sendMessage(String.format(ColorsUtil.translate.apply("&7%s. &6&l%s &7- &6&l%s"),(i+1),"Déconnecté","##"));
                }else {
                    player.sendMessage(String.format(ColorsUtil.translate.apply("&7%s. &6&l%s &7- &6&l%s"), (i + 1), currentKiller.getName(),this.getCurrentRoundKills(currentKiller.getUniqueId())));
                }
            }

            player.sendMessage(ColorsUtil.translate.apply("&6&l&nClassement Hits"));


            for(int i = 0;i<sortedRoundHits.size();i++){
                Player currentHitter = Objects.nonNull(instance.getServer().getPlayer(sortedRoundHits.get(i)))
                        ? instance.getServer().getPlayer(sortedRoundHits.get(i)) : null;
                if(currentHitter == null){
                    player.sendMessage(String.format(ColorsUtil.translate.apply("&7%s. &6&l%s &7- &6&l%s"),(i+1),"Déconnecté","##"));
                }else {
                    player.sendMessage(String.format(ColorsUtil.translate.apply("&7%s. &6&l%s &7- &6&l%s"), (i + 1), currentHitter.getName(),this.getCurrentRoundHits(currentHitter.getUniqueId())));
                }
            }
        }

    }

    public List<UUID> getSortedRoundKillers(){
        Map<UUID, Integer> killsMap = new HashMap<>();

        instance.getTeamsManager().getPlayers().values().stream()
                .flatMap(List::stream)
                .forEach(playerUUID -> killsMap.put(playerUUID, this.getCurrentRoundKills(playerUUID)));

        return killsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
    public List<UUID> getSortedRoundHits(){
        Map<UUID, Integer> killsMap = new HashMap<>();

        instance.getTeamsManager().getPlayers().values().stream()
                .flatMap(List::stream)
                .forEach(playerUUID -> killsMap.put(playerUUID, this.getCurrentRoundHits(playerUUID)));

        return killsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private int getCurrentRoundHits(UUID playerUUID){
        int beforeHits = this.roundStats.getOrDefault(playerUUID,new int[2])[1];
        int afterHits = instance.getGameManager().getPlayerStatsManager().getHits(playerUUID);

        if(instance.getGameManager().getCurrentRound() == 1){
            return afterHits;
        }

        return beforeHits;
    }
    private int getCurrentRoundKills(UUID playerUUID){
        int beforeKills = this.roundStats.getOrDefault(playerUUID,new int[2])[0];
        int afterKills = instance.getGameManager().getPlayerStatsManager().getKills(playerUUID);

        if(instance.getGameManager().getCurrentRound() == 1){
            return afterKills;
        }

        return beforeKills;
    }

    public int getStartedTime() {
        return startedTime;
    }

    public List<UUID> getDeadPlayers() {
        return spectatorList;
    }

    public Map<UUID, UUID> getLastHit() {
        return lastHit;
    }
}
