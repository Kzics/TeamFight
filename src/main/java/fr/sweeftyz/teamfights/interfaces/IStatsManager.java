package fr.sweeftyz.teamfights.interfaces;

import fr.sweeftyz.teamfights.enums.Teams;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IStatsManager {

    void addKills(UUID playerUUID);
    void addDeaths(UUID playerUUID);

    int getKills(UUID playerUUID);
    int getDeaths(UUID playerUUID);
    int getKillsOf(Teams team);
    void addHits(UUID playerUUID);
    int getHits(UUID playerUUID);

    Player getHighestKiller();
    List<UUID> getSortedKillers();
    Map<UUID,int[]> getStatsMap();
}
