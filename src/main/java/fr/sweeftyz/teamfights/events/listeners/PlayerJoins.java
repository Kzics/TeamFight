package fr.sweeftyz.teamfights.events.listeners;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.ViaManager;
import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.GameState;
import fr.sweeftyz.teamfights.enums.Teams;
import fr.sweeftyz.teamfights.events.listeners.custom.NextRoundEvent;
import fr.sweeftyz.teamfights.manager.GameManager;
import fr.sweeftyz.teamfights.utils.ColorsUtil;
import fr.sweeftyz.teamfights.utils.MiscUtils;
import fr.sweeftyz.teamfights.utils.items.ItemUtils;
import fr.sweeftyz.teamfights.utils.items.NBTUtils;
import fr.sweeftyz.teamfights.utils.scoreboard.FastBoard;

import fr.sweeftyz.teamfights.utils.scoreboard.GameTablist;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.GameMode;
import org.bukkit.Material;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;


public class PlayerJoins implements Listener {


    private final Main instance;
    public PlayerJoins(final Main instance){
        this.instance = instance;
    }




    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        player.teleport(instance.getGameManager().getRandomWorld().getSpawnLocation());
        AttributeInstance attackInstance = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        attackInstance.setBaseValue(16);

        MiscUtils.resetPlayer(player);
        FastBoard board = new FastBoard(player);
        player.setGameMode(GameMode.SURVIVAL);
        player.setMaxHealth(28);

        if(Via.getAPI().getPlayerVersion(player) != 110){
            player.kickPlayer(ColorsUtil.translate.apply("&cVous devez utilise la version 1.9.4 pour rejoindre !"));
            return;
        }
        //avoid(player);

        if(!instance.getFastBoardHashMap().containsKey(player.getUniqueId())) instance.getFastBoardHashMap().put(player.getUniqueId(),board);


        if(instance.getStateManager().isState(GameState.PLAYING)){

            if(!instance.getTeamsManager().getMembers(Teams.BLUE).contains(player.getUniqueId()) && !instance.getTeamsManager().getMembers(Teams.RED).contains(player.getUniqueId())) {
                player.setGameMode(GameMode.CREATIVE);
                MiscUtils.hideFromAll(player);
                player.setFlying(true);

                instance.getGameManager().getGameSpectator().add(player.getUniqueId());

                instance.getGameManager().getTablistManager().clearTabList();
                instance.getGameManager().getTablistManager().createGameTab();

                return;
            }else{
                new GameTablist(instance).updateTabListColor(player);
                instance.getGameManager().getTablistManager().clearTabList();
                instance.getGameManager().getTablistManager().createGameTab();

            }
        }

        int playerOnline = instance.getServer().getOnlinePlayers().size();

        e.setJoinMessage(null);

        if(instance.getStateManager().isState(GameState.WAITING)) {
            for (Player p : instance.getServer().getOnlinePlayers()) {

                FastBoard b = instance.getFastBoardHashMap().get(p.getUniqueId());

                b.updateTitle(ColorsUtil.translate.apply("&3Team&bFights"));

                b.updateLines(ColorsUtil.translate.apply("&7&m--------------------"),
                        "",
                        ColorsUtil.translate.apply("&bJoueurs : "+playerOnline + "&7/&b4"),
                        "",

                        ColorsUtil.translate.apply("&7&m--------------------"));
            }

        }

        if(playerOnline != 4 && instance.getStateManager().getCurrentState().equals(GameState.WAITING)){
            this.giveTeamChooseItem(player);
            instance.getServer().broadcastMessage(ColorsUtil.translate.apply("&7" + player.getName() + "&e à rejoins (&b" + playerOnline + "&7/&b4"+ "&e)"));

        }else if(playerOnline != 4 && instance.getStateManager().getCurrentState().equals(GameState.STARTING)){
            instance.getServer().broadcastMessage(ColorsUtil.translate.apply("&cLancement annulé, pas assez de joueurs ! "));
            instance.getStateManager().setGameState(GameState.WAITING);
            instance.getTasksManager().cancelStartingTask();

        }else if(playerOnline == 4 && instance.getStateManager().getCurrentState().equals(GameState.WAITING)){
            this.giveTeamChooseItem(player);

            instance.getServer().broadcastMessage(ColorsUtil.translate.apply("&7" + player.getName() + "&e à rejoins (&b" + playerOnline + "&7/&b4"+ "&e)"));
            instance.getTasksManager().runStartingTask();
        }
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        GameManager gameManager = instance.getGameManager();
        Teams leftTeam = instance.getTeamsManager().getTeamFromPlayer(player);

        interruptGame();

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        e.setQuitMessage(ColorsUtil.translate.apply( "&7"+player.getName()+"Quitte la partie, il peut néanmoins toujours revenir !"));

        int playerOnline = instance.getServer().getOnlinePlayers().size();

        if(instance.getStateManager().isState(GameState.WAITING) || instance.getStateManager().isState(GameState.STARTING)){
            instance.getServer().broadcastMessage(ColorsUtil.translate.apply("&7" + player.getName() + "&e a quitté (&b" + (playerOnline-1) + "&7/&b4"+ "&e)"));
        }else{
            gameManager.getRound().getDeadPlayers().add(player.getUniqueId());
        }


        instance.getGameManager().getGameSpectator()
                .removeIf(p-> instance.getGameManager().getGameSpectator().contains(p));

        instance.getFastBoardHashMap().remove(player.getUniqueId());

        gameManager.getRound().removeAlivePlayers(player);

        if(instance.getStateManager().isState(GameState.PLAYING)) {
            if (gameManager.getRound().getAlivePlayersFrom(leftTeam).isEmpty()) {
                gameManager.setWinner(Teams.getInvertTeam(leftTeam));
            }else{
                instance.getGameManager().getRound().getDeadPlayers().add(player.getUniqueId());
                gameManager.getTablistManager().clearTabList();
                gameManager.getTablistManager().createGameTab();
            }
        }
    }

    public void removePlayerTeams(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Team team = scoreboard.getPlayerTeam(player);

        if (team != null) {
            String playerName = player.getName();
            for (Team t : scoreboard.getTeams()) {
                if (t.hasEntry(playerName)) {
                    t.removeEntry(playerName);
                }
            }
        }
    }


 /*   public void avoid(Player player){
        ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline().addBefore("packet_handler", "protocol_handler", new ChannelDuplexHandler() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                System.out.println(msg);
                if (msg instanceof PacketHandshakingInSetProtocol) {
                    System.out.println(msg);
                    final PacketHandshakingInSetProtocol packet = (PacketHandshakingInSetProtocol) msg;
                    final int version = packet.b();
                    System.out.println(version);
                }
                super.write(ctx, msg, promise);
            }
        });
    }*/



    private void giveTeamChooseItem(Player player){
        ItemStack chooseItem = new ItemUtils(Material.NETHER_STAR,1)
                .setDisplayName(ColorsUtil.translate.apply("&aChoisir une Équipe"))
                .build();

        chooseItem = NBTUtils.getUpdatedNmsStack(chooseItem,"teamChoose","1");

        player.getInventory().setItem(4,chooseItem);
    }


    public void interruptGame(){
        if (instance.getServer().getOnlinePlayers().size() == 0 && instance.getStateManager().isState(GameState.PLAYING)){
            this.instance.getServer().shutdown();
        }
    }









}
