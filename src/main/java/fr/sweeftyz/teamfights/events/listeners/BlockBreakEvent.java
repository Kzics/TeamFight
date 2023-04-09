package fr.sweeftyz.teamfights.events.listeners;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.GameState;
import fr.sweeftyz.teamfights.manager.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BlockBreakEvent implements Listener {

    private final Main instance;
    private final GameManager gameManager;
    public BlockBreakEvent(final Main instance){
        this.instance = instance;
        this.gameManager = instance.getGameManager();
    }


    @EventHandler
    public void onBreak(org.bukkit.event.block.BlockBreakEvent e){
        Player p = e.getPlayer();

        if(instance.getStateManager().isState(GameState.WAITING) ||instance.getStateManager().isState(GameState.STARTING)){
            e.setCancelled(true);
        }

        if(!e.getBlock().hasMetadata("manche") || gameManager.getRound().getDeadPlayers().contains(p.getUniqueId())){
            e.setCancelled(true);
        }
    }
}
