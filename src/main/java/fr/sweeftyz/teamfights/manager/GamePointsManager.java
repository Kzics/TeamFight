package fr.sweeftyz.teamfights.manager;

import fr.sweeftyz.teamfights.Main;

public class GamePointsManager {


    private final Main instance;
    private int bluePoints;
    private int redPoints;
    public GamePointsManager(final Main instance){
        this.instance = instance;
        this.redPoints = 0;
        this.bluePoints = 0;
    }

    public int getRedPoints() {
        return redPoints;
    }

    public int getBluePoints() {
        return bluePoints;
    }

    public void addBluePoints(int amount){
        this.setBluePoints(this.getBluePoints() + amount);
    }
    public void removeBluePoints(int amount){
        int result = this.getBluePoints() - amount;
        if(result < 0){
            this.setBluePoints(0);
        }else{
            this.setBluePoints(this.getBluePoints() - amount);
        }
    }

    private void setBluePoints(int amount){
        this.bluePoints = amount;
    }

    private void setRedPoints(int amount){
        this.redPoints = amount;
    }

    public void addRedPoints(int amount){
        this.setRedPoints(this.getRedPoints() + amount);
    }
    public void removeRedPoints(int amount){
        int result = this.getBluePoints() - amount;
        if(result < 0){
            this.setBluePoints(0);
        }else{
            this.setBluePoints(this.getBluePoints() - amount);
        }
    }

}
