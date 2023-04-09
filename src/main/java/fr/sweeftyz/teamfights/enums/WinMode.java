package fr.sweeftyz.teamfights.enums;

public enum WinMode {
    ACE("ACE D'EQUIPE"),
    ONEVONE("1 contre 1"),
    CLUTCH("Clutch"),
    CLASSIC("CLASSIC"),
    DRAW("EGALITE");

    private String winName;
    WinMode(String name){
        this.winName = name;
    }

    public String getWinName() {
        return winName;
    }
}
