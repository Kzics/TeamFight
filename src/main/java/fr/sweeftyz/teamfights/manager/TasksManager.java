package fr.sweeftyz.teamfights.manager;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.GameState;
import fr.sweeftyz.teamfights.interfaces.ITaskManager;
import fr.sweeftyz.teamfights.tasks.PlayingTask;
import fr.sweeftyz.teamfights.tasks.PreRoundTask;
import fr.sweeftyz.teamfights.tasks.StartingTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TasksManager implements ITaskManager {

    private final Main instance;
    private final StartingTask startingTask;
    private final PlayingTask playingTask;

    public TasksManager(final Main instance){
        this.instance = instance;
        this.startingTask = new StartingTask(instance);
        this.playingTask = new PlayingTask(instance);
    }


    @Override
    public StartingTask getStartingTask() {
        return this.startingTask;
    }

    @Override
    public void runStartingTask() {
        instance.getStateManager().setGameState(GameState.STARTING);

        this.getStartingTask().runTaskTimer(instance,0,20);
    }

    @Override
    public void cancelStartingTask() {
        if(this.isRunningStartingTask()){
            this.getStartingTask().cancel();
        }

    }

    @Override
    public boolean isRunningStartingTask() {
        return this.getStartingTask().isRunning();
    }

    @Override
    public PlayingTask getPlayingTask() {
        return this.playingTask;
    }

    @Override
    public void runPlayingTask() {
        this.getPlayingTask().runTaskTimer(instance,0,10);
        instance.getStateManager().setGameState(GameState.PLAYING);

    }

    @Override
    public void cancelPlayingTask() {
        if(this.isRunningPlayingTask()){
            this.getPlayingTask().cancel();
        }
    }

    @Override
    public boolean isRunningPlayingTask() {
        return this.getPlayingTask().isRunning();
    }


    @Override
    public void runPreRoundTask() {
        new PreRoundTask(instance).runTaskTimer(instance,0,20);
    }

    @Override
    public void runCustomRegenTask() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(instance,()->{
            for (Player player : Bukkit.getOnlinePlayers()){
                player.setHealth(player.getHealth() + 0.5);
            }
        },140L);
    }
}
