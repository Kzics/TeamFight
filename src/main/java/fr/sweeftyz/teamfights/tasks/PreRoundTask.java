package fr.sweeftyz.teamfights.tasks;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.GameState;
import fr.sweeftyz.teamfights.enums.Teams;
import fr.sweeftyz.teamfights.utils.ActionMessage;
import fr.sweeftyz.teamfights.utils.ColorsUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PreRoundTask extends BukkitRunnable {


    private final Main instance;
    private int timer;
    private static final int EFFECT_DURATION = 999999; // Durée de l'effet en ticks (environ 13 minutes)
    private static final int EFFECT_AMPLIFIER = 127; // Amplificateur de l'effet (le maximum possible)
    public PreRoundTask(final Main instance){
        this.instance = instance;
        this.timer = 5;
    }
    @Override
    public void run() {

        if(timer == 5){
            for (Player player : instance.getServer().getOnlinePlayers()){
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, EFFECT_DURATION, 128, false, false)); // Sauts désactivés
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, EFFECT_DURATION, EFFECT_AMPLIFIER, false, false)); // Ralentissement maximum
            }
            instance.getGameManager().getTablistManager().clearTabList();
            instance.getGameManager().getTablistManager().createGameTab();

            for(Teams team: Teams.values()){
                instance.getGameManager().giveStuff(team);
            }
        }

        if(timer == 0){
            instance.getGameManager().setPreRound(false);
            instance.getStateManager().setGameState(GameState.PLAYING);

            for (Player player: instance.getServer().getOnlinePlayers()){
                player.getActivePotionEffects()
                        .forEach(effect->player.removePotionEffect(effect.getType()));
                for (Player p: instance.getServer().getOnlinePlayers()){
                    for (Player o : instance.getServer().getOnlinePlayers()){
                        if(!p.canSee(o)){
                            p.showPlayer(o);
                        }
                    }
                }
            }
            this.cancel();
            return;
        }

        for(Player player : Bukkit.getOnlinePlayers()){
            new ActionMessage(ColorsUtil.translate.apply("&aNouvelle Manche dans " + timer))
                    .send(player,3);

            new ActionMessage(ColorsUtil.translate.apply("&a" + timer))
                    .sendTitle(player,10,70,5);
            new ActionMessage(timer + "")
                    .sendTitle(player,10,70,5);
        }

        timer--;

    }
}
