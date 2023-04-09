package fr.sweeftyz.teamfights.interfaces;

import fr.sweeftyz.teamfights.tasks.PlayingTask;
import fr.sweeftyz.teamfights.tasks.StartingTask;

public interface ITaskManager {

    StartingTask getStartingTask();
    void runStartingTask();
    void cancelStartingTask();
    boolean isRunningStartingTask();

    PlayingTask getPlayingTask();
    void runPlayingTask();
    void cancelPlayingTask();
    boolean isRunningPlayingTask();

    void runPreRoundTask();
    void runCustomRegenTask();
}
