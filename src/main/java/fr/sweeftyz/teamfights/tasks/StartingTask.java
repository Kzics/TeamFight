package fr.sweeftyz.teamfights.tasks;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.Round;
import fr.sweeftyz.teamfights.enums.Teams;
import fr.sweeftyz.teamfights.utils.ActionMessage;
import fr.sweeftyz.teamfights.utils.ColorsUtil;
import fr.sweeftyz.teamfights.utils.MiscUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class StartingTask extends BukkitRunnable {

    private boolean running;
    private int timer;
    private final Main instance;
    public StartingTask(final Main instance){
        this.running = false;
        this.timer = 10;
        this.instance = instance;
    }
    @Override
    public void run() {


        if(!running) running = true;

        if(timer == 10){
            instance.getServer().broadcastMessage(ColorsUtil.translate.apply("&eLancement de la partie dans &c" + timer + " secondes"));
        }else if(timer < 6 && timer != 0){
            instance.getServer().getOnlinePlayers().forEach(this::launchingTimer);
        }


        if(timer == 0){
            instance.getTasksManager().cancelStartingTask();

            instance.getTeamsManager().autoFillTeam();

            for (Player player: instance.getServer().getOnlinePlayers()){
                player.sendMessage("Lancement...");

            }
            instance.getTasksManager().runPlayingTask();
            instance.getGameManager().clearInv();
            for(Teams team: Teams.values()){
                instance.getGameManager().giveStuff(team);
            }

            instance.getGameManager().getTablistManager().clearTabList();

            instance.getGameManager().firstRound(new Round(instance,instance.getGameManager().getCurrentRound()+1,instance.getGameManager().getPlayerStatsManager()));

        }

        timer --;
    }

    public boolean isRunning() {
        return running;
    }

    public void launchingTimer(Player player){
        new ActionMessage("&eLancement dans &c"
                + MiscUtils.getCorrectTimer.apply(timer))
                .send(player,2);

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 1, 1);

    }

}
