package fr.sweeftyz.teamfights.events.listeners;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.GameState;
import fr.sweeftyz.teamfights.manager.GameManager;
import fr.sweeftyz.teamfights.menu.PlayerTeamMenu;
import fr.sweeftyz.teamfights.utils.items.NBTUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerInteract implements Listener {

    private final Main instance;
    private final GameManager gameManager;
    public PlayerInteract(final Main instance){
        this.instance = instance;
        this.gameManager = instance.getGameManager();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();

        if(instance.getStateManager().isState(GameState.WAITING) || instance.getStateManager().isState(GameState.STARTING)){
            e.setCancelled(true);
            if(NBTUtils.hasKey(e.getItem(),"teamChoose")){
                new PlayerTeamMenu(instance)
                        .openInv(player);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        e.setCancelled(true);
    }

    @EventHandler
    public void onPick(PlayerPickupItemEvent e){
        Player player = e.getPlayer();

        if(gameManager.getRound().getDeadPlayers().contains(player.getUniqueId())){
            e.setCancelled(true);
        }
    }
}
