package fr.sweeftyz.teamfights.manager;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.GameState;

public class StateManager {


    private final Main instance;
    private GameState currentState;

    public StateManager(final Main instance){
        this.instance = instance;
        this.currentState = GameState.WAITING;
    }


    public void setGameState(GameState state){
        this.currentState = state;
    }

    public GameState getCurrentState() {
        return this.currentState;
    }

    public boolean isState(GameState state){
        return this.currentState == state;

    }
}
