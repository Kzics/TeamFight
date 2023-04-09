package fr.sweeftyz.teamfights.enums;

public enum Teams {
    BLUE("Bleu","&9"),
    RED("Rouge","&c"),
    NONE("Aucune","&f");


    private final String teamName;
    private final String teamColorCode;
    Teams(String teamName,String teamColorCode){
        this.teamName = teamName;
        this.teamColorCode = teamColorCode;
    }



    public String getTeamName() {
        return teamName;
    }

    public String getTeamColorCode() {
        return teamColorCode;
    }

    public static Teams getInvertTeam(Teams team){
        return team.equals(Teams.BLUE) ? Teams.RED : Teams.BLUE;
    }
}
