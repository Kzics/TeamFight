package fr.sweeftyz.teamfights.events.listeners;


import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.GameState;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityVelocity;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public class PlayerVelocity implements Listener {


    private final Main instance;
    public PlayerVelocity(final Main instance){
        this.instance = instance;

    }

    private double horizontalFactor = 0.52D;
    private double verticalFactor = 0.352D;

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        event.setCancelled(true);
    }

    private Vector getVector(Player victim, Player attacker) {
        Vector vector = victim.getLocation().toVector().subtract(attacker.getLocation().toVector()).setY(0);
        return Math.sqrt(Math.pow(vector.getX(), 2.0D) + Math.pow(vector.getZ(), 2.0D)) <= 0.5D ? attacker.getEyeLocation().getDirection().setY(0).normalize() : vector.normalize();
    }

    public double getHorizontalFactor() {
        return this.horizontalFactor;
    }

    public double getVerticalFactor() {
        return this.verticalFactor;
    }
}
