package fr.sweeftyz.teamfights.manager;

import com.mojang.authlib.GameProfile;
import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.Round;
import fr.sweeftyz.teamfights.enums.Teams;
import fr.sweeftyz.teamfights.utils.ColorsUtil;
import fr.sweeftyz.teamfights.utils.MiscUtils;
import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_10_R1.WorldSettings;
import org.bukkit.Bukkit;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_10_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class TablistManager {
    private final Main instance;
    private final PacketPlayOutPlayerInfo removePacket;
    private final PacketPlayOutPlayerInfo addPacket;
    private final List<PacketPlayOutPlayerInfo.PlayerInfoData> tablist;
    public TablistManager(final Main instance){
        this.instance = instance;
        this.removePacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER);
        this.addPacket = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER);
        this.tablist = new ArrayList<>();

    }


    public void addLine(String name) {

        IChatBaseComponent chatBaseComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + name + "\"}");

        GameProfile profile = new GameProfile(UUID.randomUUID(),String.valueOf(10+ this.tablist.size()));

        this.tablist.add(addPacket.new PlayerInfoData(profile, 0, WorldSettings.a(0), chatBaseComponent));

    }


    public TablistManager build(){

        try {
            Field bField = null;

            bField = MiscUtils.getField(removePacket, "b");
            bField.setAccessible(true);

            bField.set(addPacket, this.tablist);

            this.sendPacket(addPacket);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


        return this;
    }

    private void sendPacket(PacketPlayOutPlayerInfo packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public void clearTabList() {
        List<PacketPlayOutPlayerInfo.PlayerInfoData> dataList = ((List<PacketPlayOutPlayerInfo.PlayerInfoData>) MiscUtils.getPrivateField(removePacket, "b"));
        for(Player player : Bukkit.getOnlinePlayers()){
            GameProfile profile = new GameProfile(player.getUniqueId(),player.getName());
            IChatBaseComponent baseComp = CraftChatMessage.fromString(player.getName())[0];
            PacketPlayOutPlayerInfo.PlayerInfoData playerData = removePacket.new PlayerInfoData(profile,0, WorldSettings.a(0),baseComp);

            dataList.add(playerData);

        }
        for (PacketPlayOutPlayerInfo.PlayerInfoData line : this.tablist) {
            PacketPlayOutPlayerInfo.PlayerInfoData playerData = removePacket.new PlayerInfoData(line.a(), line.b(), line.c(), line.d());
            dataList.add(playerData);
        }

        this.tablist.clear();

        this.sendPacket(removePacket);

    }


    public void createGameTab(){
        Round currentRound = instance.getGameManager().getRound();
        List<Player> playerListB = instance.getTeamsManager().getMembers(Teams.BLUE)
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());


        List<Player> playerListR = instance.getTeamsManager().getMembers(Teams.RED)
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Player> spectators = instance.getGameManager().getGameSpectator()
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Player mvp = instance.getGameManager().getPlayerStatsManager().getHighestKiller();

        for(int i = 0; i<54;i++){
            if(i == 0) instance.getGameManager().getTablistManager().addLine(ColorsUtil.translate.apply(Teams.RED.getTeamColorCode() + "&lRouge"));

            if(i <= playerListR.size()-1){
                String currentName = playerListR.get(i).getUniqueId().equals(mvp == null || playerListB.isEmpty() ? UUID.randomUUID():
                        mvp.getUniqueId()) ?
                        playerListR.get(i).getName() + "  &6&lMVP": playerListR.get(i).getName();

                instance.getGameManager().getTablistManager().addLine(ColorsUtil.translate.apply(Teams.RED.getTeamColorCode() + (currentRound.isDead(playerListR.get(i).getUniqueId()) ?
                        "&m"+currentName : currentName)));

            }else if(i == 10){
                instance.getGameManager().getTablistManager().addLine(ColorsUtil.translate.apply("&7&lSpectateurs ("+spectators.size()+ ")"));
                instance.getGameManager().getTablistManager().addLine(ColorsUtil.translate.apply("             "));

            }else if(i >10 && i<11 + spectators.size()){
                spectators.forEach(s->instance.getGameManager().getTablistManager().addLine(ColorsUtil.translate.apply("&7" + s.getName())));

            }else if(i == 18){
                instance.getGameManager().getTablistManager().addLine(ColorsUtil.translate.apply("&e&lStatistiques"));
                instance.getGameManager().getTablistManager().addLine(ColorsUtil.translate.apply(Teams.RED.getTeamColorCode() + MiscUtils.getPointSymbol()
                        + " " +instance.getGameManager().getRedPoints() + " - " + instance.getGameManager().getBluePoints() + " " + Teams.BLUE.getTeamColorCode() + MiscUtils.getPointSymbol()));

                instance.getGameManager().getTablistManager().addLine(ColorsUtil.translate.apply("             "));

                instance.getGameManager().getTablistManager().addLine(ColorsUtil.translate.apply("&eKills: " + Teams.RED.getTeamColorCode() +" "+ MiscUtils.getPointSymbol()
                        + instance.getGameManager().getPlayerStatsManager().getKillsOf(Teams.RED) + " - " + instance.getGameManager().getPlayerStatsManager().getKillsOf(Teams.BLUE)
                 + " "+Teams.BLUE.getTeamColorCode() + MiscUtils.getPointSymbol()));

            }else if(i == 35){
                instance.getGameManager().getTablistManager().addLine(ColorsUtil.translate.apply(Teams.BLUE.getTeamColorCode() +"&lBleu"));

            } else if(i >= 36 && i < 36 + playerListB.size()){
                String currentName = playerListB.get(i-36).getUniqueId().equals(mvp == null || playerListB.isEmpty() ? UUID.randomUUID():
                        mvp.getUniqueId()) ?
                        playerListB.get(i-36).getName() + "  &6&lMVP": playerListB.get(i-36).getName();

                instance.getGameManager().getTablistManager().addLine(ColorsUtil.translate.apply(Teams.BLUE.getTeamColorCode() + (currentRound.isDead(playerListB.get(i-36).getUniqueId()) ?
                        "&m" + currentName : currentName
                        )));
            }else{
                instance.getGameManager().getTablistManager().addLine("           ");
            }
        }
        instance.getGameManager().getTablistManager().build();
    }

}
