package fr.sweeftyz.teamfights.events.listeners;

import fr.sweeftyz.teamfights.Main;

import fr.sweeftyz.teamfights.utils.ActionMessage;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


public class CPSLimitEvent implements Listener {

    private final Main instance;
    private Map<Player, Integer> clickCounters = new HashMap<>();
    private Map<Player, Double> playerCps = new HashMap<>();

    private List<UUID> messageCD = new ArrayList<>();


    public CPSLimitEvent(final Main instance){
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR)) {

            Player p = event.getPlayer();
            if (playerCps.containsKey(p)) {
                if (playerCps.get(p) < 9) {
                    handleClick(player);
                } else {
                    handleClick(player);
                    event.setCancelled(true);
                }
            } else {
                handleClick(player);
            }
        }

    }


    private void handleClick(Player player) {
        final int[] clicks = {clickCounters.getOrDefault(player, 0)};
        clicks[0]++;
        clickCounters.put(player, clicks[0]);

        new BukkitRunnable() {
            @Override
            public void run() {
                int currentClicks = clickCounters.getOrDefault(player, 0);
                if (currentClicks == clicks[0]) {
                    // Player hasn't clicked in 2 seconds, reset click counter
                    clickCounters.remove(player);
                    cancel();
                } else {
                    // Calculate clicks per second and send message
                    double cps = (currentClicks - clicks[0]);
                    if(cps < 0) {
                        cps = 0;
                    }
                    new ActionMessage("&cCPS: &e" + cps).send(player,2);
                    clicks[0] = currentClicks;
                    playerCps.put(player,cps);
                }
            }
        }.runTaskTimer(instance, 15, 20);
    }

}
