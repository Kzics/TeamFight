package fr.sweeftyz.teamfights.utils;

import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_10_R1.PlayerConnection;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;


public class ActionMessage {


    private final String message;

    public ActionMessage(final String message){
        this.message = message;
    }

    public void send(Player player,int action){
        CraftPlayer p = (CraftPlayer) player;

        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a(ColorsUtil.translate.apply("{\"text\": \"" + message + "\"}"));

        PacketPlayOutChat chatTitle = new PacketPlayOutChat(chatBaseComponent,(byte) action);

        PlayerConnection playerConnection = p.getHandle().playerConnection;

        playerConnection.sendPacket(chatTitle);
    }

    public void sendTitle(Player player,int i1,int i2,int i3){
        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a(ColorsUtil.translate.apply("{\"text\":\"" + this.message + "\"}")), i1, i2, i3);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void sendSubTitle(Player player,int i1,int i2,int i3){
        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a(ColorsUtil.translate.apply("{\"text\":\"" + this.message + "\"}")), i1, i2, i3);

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

    }
}
