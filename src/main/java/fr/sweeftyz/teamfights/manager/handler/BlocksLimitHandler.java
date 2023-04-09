package fr.sweeftyz.teamfights.manager.handler;

import fr.sweeftyz.teamfights.Main;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlocksLimitHandler {


    private final Main instance;
    private final int maxHeight;
    public BlocksLimitHandler(final Main instance,int maxHeight){
        this.instance = instance;
        this.maxHeight = maxHeight;
    }

    public int getMaxHeight(){
        return this.maxHeight;
    }


    public void resetMap(Location center) {
        for (int x = center.getBlockX() - 100; x <= center.getBlockX() + 100; x++) {
            for (int y = center.getBlockY() - 100; y <= center.getBlockY() + 100; y++) {
                for (int z = center.getBlockZ() - 100; z <= center.getBlockZ() + 100; z++) {
                    Block block = center.getWorld().getBlockAt(x, y, z);
                    if (block.getType() != Material.AIR && block.getType().isBlock()) {
                        if (block.hasMetadata("manche")) {
                            block.setType(Material.AIR);
                        }
                    }
                }
            }
        }
    }


}
