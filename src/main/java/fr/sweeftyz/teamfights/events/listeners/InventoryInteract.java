package fr.sweeftyz.teamfights.events.listeners;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.GameState;
import fr.sweeftyz.teamfights.enums.Teams;
import fr.sweeftyz.teamfights.manager.GameManager;
import fr.sweeftyz.teamfights.menu.PlayerTeamMenu;
import fr.sweeftyz.teamfights.utils.ColorsUtil;
import fr.sweeftyz.teamfights.utils.scoreboard.GameTablist;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryInteract implements Listener {


    private final Main instance;
    private final GameManager gameManager;
    public InventoryInteract(final Main instance){
        this.instance = instance;
        this.gameManager = instance.getGameManager();
    }


    @EventHandler
    public void onInventoryInteract(InventoryClickEvent e){
        Player player = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if(gameManager.getRound() != null) {
            if (gameManager.getRound().getDeadPlayers().contains(player.getUniqueId())) {
                e.setCancelled(true);
            }
        }


        if(e.getClickedInventory() == null) return;

        if(clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        if(instance.getStateManager().isState(GameState.WAITING)) e.setCancelled(true);

        if(e.getClickedInventory().getTitle().equals(PlayerTeamMenu.getTitle())){
            e.setCancelled(true);
            if(clickedItem.getItemMeta().getDisplayName().equals(ColorsUtil.translate.apply("&cRouge"))){
                if(instance.getTeamsManager().getTeamFromPlayer(player).equals(Teams.RED)){
                    player.sendMessage(ColorsUtil.translate.apply("&eVous êtes déjà dans cette équipe ! "));

                    return;
                }
                if(instance.getTeamsManager().addMember(Teams.RED,player)){
                    player.sendMessage(ColorsUtil.translate.apply("&eVous avez rejoint l'équipe " + Teams.RED.getTeamName()));
                    new GameTablist(instance).updateTabListColor(player);
                    this.reopenInventory();
                }else{
                    player.sendMessage(ColorsUtil.translate.apply("&eL'équipe est déjà remplis ! "));
                }

            }else if (clickedItem.getItemMeta().getDisplayName().equals(ColorsUtil.translate.apply("&9Bleu"))){
                if(instance.getTeamsManager().getTeamFromPlayer(player).equals(Teams.BLUE)){
                    player.sendMessage(ColorsUtil.translate.apply("&eVous avez déjà dans cette équipe !"));
                    return;
                }

                if(instance.getTeamsManager().addMember(Teams.BLUE,player)){
                    player.sendMessage(ColorsUtil.translate.apply("&eVous avez rejoint l'équipe " + Teams.BLUE.getTeamName()));
                    new GameTablist(instance).updateTabListColor(player);

                    this.reopenInventory();
                }else{
                    player.sendMessage(ColorsUtil.translate.apply("&eL'équipe est déjà remplis !"));
                }
            }
        }
    }

    private void reopenInventory(){
        instance.getServer().getOnlinePlayers()
                .forEach(player -> {
                    if(player.getOpenInventory().getTopInventory().getTitle().equals(PlayerTeamMenu.getTitle())){
                        new PlayerTeamMenu(instance).openInv(player);
                    }
                });
    }
}
