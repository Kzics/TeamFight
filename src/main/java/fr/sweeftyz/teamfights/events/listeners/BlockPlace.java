package fr.sweeftyz.teamfights.events.listeners;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.GameState;
import fr.sweeftyz.teamfights.manager.GameManager;
import fr.sweeftyz.teamfights.utils.ActionMessage;
import fr.sweeftyz.teamfights.utils.ColorsUtil;
import fr.sweeftyz.teamfights.utils.PlatformCornerPair;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class BlockPlace implements Listener {


    private final Main instance;
    private final GameManager gameManager;
    private final PlatformCornerPair cornerPair;
    private int platformSize = 5; // La taille de la plateforme en nombre de blocs
    public BlockPlace(final Main instance){
        this.instance = instance;
        this.gameManager = instance.getGameManager();
        this.cornerPair = new PlatformCornerPair(instance);

    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        Player p = e.getPlayer();
        Location blockLoc = e.getBlockPlaced().getLocation();

        if(instance.getStateManager().isState(GameState.PREROUND) ||gameManager.getRound().getDeadPlayers().contains(p.getUniqueId())){
            e.setCancelled(true);
        }

        if(e.getBlockPlaced().getLocation().getBlockY() >= instance.getBlocksLimitHandler().getMaxHeight()){
            new ActionMessage("&cHauteur limité à 3 !")
                    .send(p,2);

            e.setCancelled(true);
        }else{
            Block placedBlock = e.getBlock();

            placedBlock.setMetadata("manche",new FixedMetadataValue(instance,instance.getGameManager().getCurrentRound()));
        }

        // On vérifie si le bloc est sur la plateforme rectangulaire ou dans la zone d'extension
        if (!isWithinPlatformAndExtension(blockLoc)) {
            new ActionMessage("Vous ne pouvez pas placer de blocs en dehors de la plateforme !")
                    .send(e.getPlayer(),2);
            e.setCancelled(true);
        }
    }
    private boolean isWithinPlatformAndExtension(Location loc) {
        int x1 = Math.min(cornerPair.getFirstCorner().getBlockX(), cornerPair.getSecondCorner().getBlockX());
        int x2 = Math.max(cornerPair.getFirstCorner().getBlockX(), cornerPair.getSecondCorner().getBlockX());
        int z1 = Math.min(cornerPair.getFirstCorner().getBlockZ(), cornerPair.getSecondCorner().getBlockZ());
        int z2 = Math.max(cornerPair.getFirstCorner().getBlockZ(), cornerPair.getSecondCorner().getBlockZ());
        int y1 = cornerPair.getFirstCorner().getBlockY();
        int y2 = cornerPair.getSecondCorner().getBlockY();

        // On vérifie si la position est à l'intérieur du rectangle et si elle est dans la zone d'extension
        return loc.getBlockX() >= x1 - 5 && loc.getBlockX() <= x2 + 5 &&
                loc.getBlockZ() >= z1 - 5 && loc.getBlockZ() <= z2 + 5 &&
                loc.getBlockY() >= y1 && loc.getBlockY() <= y2;
    }

}
