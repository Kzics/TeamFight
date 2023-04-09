package fr.sweeftyz.teamfights.manager;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.Teams;
import fr.sweeftyz.teamfights.interfaces.IStatsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerStatsManager implements IStatsManager {
    private final Main instance;
    private final HashMap<UUID, int[]> playerStats;

    public PlayerStatsManager(final Main instance){
        this.instance = instance;
        playerStats = new HashMap<>();
    }

    @Override
    public void addKills(UUID playerUUID) {
        int[] stats = playerStats.getOrDefault(playerUUID, new int[3]);
        stats[0]++;
        playerStats.put(playerUUID, stats);
    }

    @Override
    public void addDeaths(UUID playerUUID) {
        int[] stats = playerStats.getOrDefault(playerUUID, new int[3]);
        stats[1]++;
        playerStats.put(playerUUID, stats);
    }

    @Override
    public int getKills(UUID playerUUID) {
        return playerStats.getOrDefault(playerUUID, new int[3])[0];
    }

    @Override
    public int getDeaths(UUID playerUUID) {
        return playerStats.getOrDefault(playerUUID, new int[3])[1];
    }

    @Override
    public int getKillsOf(Teams team) {
        Set<Player> members = instance.getTeamsManager().getMembers(team)
                .stream().map(Bukkit::getPlayer)
                .collect(Collectors.toCollection(HashSet::new));

        return members.stream()
                .filter(Objects::nonNull)
                .mapToInt(player -> getKills(player.getUniqueId()))
                .sum();
    }

    @Override
    public void addHits(UUID playerUUID) {
        int[] stats = playerStats.getOrDefault(playerUUID, new int[3]);
        stats[2]++;
        playerStats.put(playerUUID, stats);
    }

    @Override
    public int getHits(UUID playerUUID) {
        return playerStats.getOrDefault(playerUUID, new int[3])[2];
    }

    @Override
    public Player getHighestKiller() {
        Optional<Player> playerWithMostKills = instance.getTeamsManager().getPlayers().values().stream()
                .flatMap(List::stream)
                .map(uuid -> instance.getServer().getPlayer(uuid))
                .filter(Objects::nonNull)
                .max(Comparator.comparingInt(player -> getKills(player.getUniqueId())));

        return !playerStats.containsKey(playerWithMostKills.get().getUniqueId()) ?
                null : playerWithMostKills.orElse(null);
    }

    @Override
    public List<UUID> getSortedKillers(){
        Map<UUID, Integer> killsMap = new HashMap<>();

        instance.getTeamsManager().getPlayers().values().stream()
                .flatMap(List::stream)
                .forEach(playerUUID -> killsMap.put(playerUUID, getKills(playerUUID)));

        return killsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public Map<UUID, int[]> getStatsMap() {
        return this.playerStats;
    }


}
