package fr.sweeftyz.teamfights;

import fr.sweeftyz.teamfights.enums.GameState;
import fr.sweeftyz.teamfights.events.ListenerManager;
import fr.sweeftyz.teamfights.events.listeners.PlayerChat;
import fr.sweeftyz.teamfights.events.listeners.*;
import fr.sweeftyz.teamfights.manager.GameManager;
import fr.sweeftyz.teamfights.manager.StateManager;
import fr.sweeftyz.teamfights.manager.TasksManager;
import fr.sweeftyz.teamfights.manager.handler.BlocksLimitHandler;
import fr.sweeftyz.teamfights.requests.ApiClient;
import fr.sweeftyz.teamfights.teams.TeamsManager;
import fr.sweeftyz.teamfights.utils.scoreboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin {


    private StateManager stateManager;
    private TasksManager tasksManager;
    private TeamsManager teamsManager;
    private GameManager gameManager;
    private HashMap<UUID, FastBoard> fastBoardHashMap;
    private ApiClient apiClient;

    private BlocksLimitHandler blocksLimitHandler;
    @Override
    public void onEnable() {
        this.stateManager = new StateManager(this);
        this.teamsManager = new TeamsManager(this);
        this.gameManager = new GameManager(this);
        this.blocksLimitHandler = new BlocksLimitHandler(this,6);
        this.tasksManager = new TasksManager(this);

        this.apiClient = new ApiClient();

        stateManager.setGameState(GameState.WAITING);


        this.fastBoardHashMap = new HashMap<>();

        new ListenerManager(this)
                .registerEvents(
                        new PlayerJoins(this),
                        new InventoryInteract(this),
                        new PlayerInteract(this),
                        new PlayerFight(this),
                        new CPSLimitEvent(this),
                        new BlockPlace(this),
                        new BlockBreakEvent(this),
                        new MobSpawn(),
                        new PlayerChat(this),
                        new PlayerVelocity(this),
                        new GameEvents(this));
    }



    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.removePlayerTeams(player);
        }
        blocksLimitHandler.resetMap(Bukkit.getWorld("world").getSpawnLocation());
    }

    public StateManager getStateManager() {
        return stateManager;
    }
    public TasksManager getTasksManager() {
        return tasksManager;
    }
    public TeamsManager getTeamsManager() {
        return teamsManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public HashMap<UUID, FastBoard> getFastBoardHashMap() {
        return fastBoardHashMap;
    }

    public BlocksLimitHandler getBlocksLimitHandler() {
        return blocksLimitHandler;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void removePlayerTeams(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getPlayerTeam(player);

        if (team != null) {
            String playerName = player.getName();
            for (Team t : scoreboard.getTeams()) {
                if (t.hasEntry(playerName)) {
                    t.removeEntry(playerName);
                }
            }
        }
    }
}
