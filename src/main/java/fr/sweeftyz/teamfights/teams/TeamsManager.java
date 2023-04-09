package fr.sweeftyz.teamfights.teams;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.Teams;
import fr.sweeftyz.teamfights.utils.scoreboard.GameTablist;
import org.bukkit.entity.Player;

import java.util.*;

public class TeamsManager {

    private final Main instance;
    private Map<Teams, List<UUID>> playerTeams;

    public TeamsManager(final Main instance){
        this.instance = instance;
        this.playerTeams = new HashMap<>();

        this.playerTeams.put(Teams.RED,new ArrayList<>());
        this.playerTeams.put(Teams.BLUE,new ArrayList<>());
    }


    public boolean addMember(Teams team, Player player){

        if(this.isJoinable(team)){
            if(this.hasTeam(player)){
                Teams otherTeam = this.getTeamFromPlayer(player);
                this.removeMember(otherTeam,player);
            }
            List<UUID> members = this.getMembers(team);
            members.add(player.getUniqueId());

            this.playerTeams.put(team,members);
            System.out.println(playerTeams);

            return true;
        }
        return false;
    }

    public Teams getTeamFromPlayer(Player player){
        for(Map.Entry<Teams, List<UUID>> entry : this.playerTeams.entrySet()){
            if(isInTeam(entry.getKey(), player)){
                return entry.getKey();
            }
        }
        return Teams.NONE;
    }    public Teams getTeamFromPlayer(UUID playerUUID){
        for(Map.Entry<Teams, List<UUID>> entry : this.playerTeams.entrySet()){
            if(isInTeam(entry.getKey(), playerUUID)){
                return entry.getKey();
            }
        }
        return Teams.NONE;
    }

    public List<UUID> getMembers(Teams teams){
        return this.playerTeams.get(teams) == null ? new ArrayList<>() : this.playerTeams.get(teams);
    }

    public boolean isInTeam(Teams team,Player player){
        List<UUID> members = this.getMembers(team);
        if(members.isEmpty()) return false;

        return members.contains(player.getUniqueId());
    }
    public boolean isInTeam(Teams team,UUID playerUUID){
        List<UUID> members = this.getMembers(team);
        if(members.isEmpty()) return false;

        return members.contains(playerUUID);
    }

    public boolean hasTeam(Player player){
        return this.getTeamFromPlayer(player) != Teams.NONE;
    }

    public boolean removeMember(Teams team,Player player){
        if(this.isInTeam(team,player)){
            List<UUID> members = this.getMembers(team);
            members.remove(player.getUniqueId());

            this.playerTeams.put(team,members);

            return true;
        }

        return false;

    }

    public boolean isJoinable(Teams team){
        return this.playerTeams.get(team).size() < 5 || this.playerTeams.isEmpty();
    }

    public void setMembers(Teams team,List<UUID> players){
        this.playerTeams.put(team,players);
    }

    public Map<Teams, List<UUID>> getPlayers() {
        return playerTeams;
    }

    public void autoFillTeam(){
        for(Player player: instance.getServer().getOnlinePlayers()){
            Teams playerTeam = this.getTeamFromPlayer(player);

            if(playerTeam.equals(Teams.NONE)){
                int redTeamSize = this.getMembers(Teams.RED).size();
                int blueTeamSize = this.getMembers(Teams.BLUE).size();

                if(redTeamSize == blueTeamSize){
                    Random random = new Random();
                    int randomTeam = random.nextInt(2);

                    if(randomTeam == 0){
                        this.addMember(Teams.RED, player);
                    }
                    else{
                        this.addMember(Teams.BLUE, player);
                    }
                }
                else{
                    if(redTeamSize < blueTeamSize){
                        this.addMember(Teams.RED, player);
                    }

                    else{
                        this.addMember(Teams.BLUE, player);
                    }
                }
            }

            new GameTablist(instance).updateTabListColor(player);

        }
    }

    public int getMaxTeamPLayer(){
        return 2;
    }
}
