package fr.sweeftyz.teamfights.manager;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.Round;
import fr.sweeftyz.teamfights.enums.DeathType;
import fr.sweeftyz.teamfights.enums.GameState;
import fr.sweeftyz.teamfights.enums.Teams;
import fr.sweeftyz.teamfights.enums.WinMode;
import fr.sweeftyz.teamfights.events.listeners.custom.GameStartEvent;
import fr.sweeftyz.teamfights.utils.ActionMessage;
import fr.sweeftyz.teamfights.utils.ColorsUtil;
import fr.sweeftyz.teamfights.utils.MiscUtils;
import fr.sweeftyz.teamfights.utils.items.ItemUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class GameManager extends GamePointsManager{

    private final Main instance;

    private int currentRound;
    private Round round;
    private boolean preRound;
    private Teams winnerTeam;
    private final TablistManager tablistManager;
    private final PlayerStatsManager playerStatsManager;
    private final Location blueSpawn;
    private final Location redSpawn;
    private WinMode winMode;
    private List<UUID> gameSpectator;
    public GameManager(final Main instance){
        super(instance);

        this.instance  = instance;
        this.currentRound = 1;
        this.preRound = false;
        this.tablistManager = new TablistManager(instance);
        this.playerStatsManager = new PlayerStatsManager(instance);
        this.gameSpectator = new ArrayList<>();
        this.blueSpawn = new Location(instance.getServer().getWorld("world"),-1057, 3, -97);
        this.redSpawn = new Location(instance.getServer().getWorld("world"),-1057, 3, -122);
    }

    public int getCurrentRound(){
        return this.currentRound;
    }

    public Round getRound() {
        return round;
    }

    public void nextRound(WinMode winMode){

        for(UUID uuid : this.getRound().getDeadPlayers()){
            try{
                Player player = Bukkit.getPlayer(uuid);

                if(!player.getGameMode().equals(GameMode.SURVIVAL)) player.setGameMode(GameMode.SURVIVAL);
                //teleportation dans sa zone de team
            }catch (Exception e){
                continue;
            }
        }
        int redIndex = 0;
        int blueIndex = 0;


        for (Player p: instance.getServer().getOnlinePlayers()){
            new ActionMessage("&eLe round ce termine par : &a"+winMode.getWinName())
                    .send(p,2);

            if(instance.getTeamsManager().getTeamFromPlayer(p).equals(Teams.RED)){
                p.teleport(redSpawn.add(redIndex,0,0));
                redIndex+= 2;
            }else{
                Location tempBlueSpawn = blueSpawn.clone();

                p.teleport(tempBlueSpawn.add(blueIndex,0,0).setDirection(tempBlueSpawn.getDirection().multiply(-1)));
                blueIndex +=2;
            }
        }

        this.currentRound += 1;
        this.round = new Round(instance,currentRound,playerStatsManager);

        instance.getStateManager().setGameState(GameState.PREROUND);

        instance.getServer().getOnlinePlayers().forEach(MiscUtils::resetPlayer);

        instance.getGameManager().getTablistManager().createGameTab();
    }

    public void firstRound(Round round){
        this.round = round;

    }

    public void start(){

        GameStartEvent gameStartEvent = new GameStartEvent(instance);
        instance.getServer().getPluginManager().callEvent(gameStartEvent);

        int redIndex = 0;
        int blueIndex = 0;

        for (Player p: instance.getServer().getOnlinePlayers()){

            if(instance.getTeamsManager().getTeamFromPlayer(p).equals(Teams.RED)){
                p.teleport(redSpawn.add(redIndex,0,0));
                redIndex+= 2;
            }else{
                Location tempBlueSpawn = blueSpawn.clone();
                p.teleport(tempBlueSpawn.add(blueIndex,0,0).setDirection(tempBlueSpawn.getDirection().multiply(-1)));
                blueIndex +=2;
            }

            instance.getServer().getOnlinePlayers().forEach(MiscUtils::resetPlayer);

            Arrays.stream(Teams.values())
                    .forEach((team) -> {
                        if(instance.getTeamsManager().getMembers(Teams.RED).contains(p.getUniqueId())){
                            this.giveStuff(team);
                        }else if(instance.getTeamsManager().getMembers(Teams.BLUE).contains(p.getUniqueId())){
                            this.giveStuff(team);
                        }
                    });

        }
    }

    public boolean isPreRound() {
        return preRound;
    }

    public void setPreRound(boolean preRound){
        this.preRound = preRound;
    }

    public boolean checkWinner(){
        return this.getBluePoints() == 5 || this.getRedPoints() == 5 || this.winnerTeam != null;
    }
    public void setWinner(Teams teams){
        this.winnerTeam = teams;
    }

    public Teams getWinner(){
        if(this.getRedPoints() == 5){
            this.winnerTeam = Teams.RED;
        }else if(this.getBluePoints() == 5){
            this.winnerTeam = Teams.BLUE;
        }

        return this.winnerTeam;
    }

    public void clearInv() {
        instance.getServer().getOnlinePlayers()
                .forEach(p -> p.getInventory().clear());
    }
    public void killPlayer(Player player, Player killer, DeathType type){
        this.getRound().removeAlivePlayers(player);

        player.setGameMode(GameMode.SPECTATOR);

        new ActionMessage(ColorsUtil.translate.apply("&c&lVous êtes eliminé"))
                .sendTitle(player,10,70,5);

        this.getRound().getDeadPlayers()
                .add(player.getUniqueId());
    }


    public void clearEntities(World world){
        world.getEntities()
                .stream()
                .filter(ent-> ent instanceof Item)
                .forEach(Entity::remove);
    }
    public void giveStuff(Teams team){
        List<ItemStack> armorList = new ArrayList<>();

        ItemStack sword = new ItemUtils(Material.IRON_SWORD,1)
                .addEnchants(Enchantment.DAMAGE_ALL,2)
                .build();
        ItemStack pickaxe = new ItemUtils(Material.IRON_PICKAXE,1)
                .addEnchants(Enchantment.DIG_SPEED,3)
                .build();
        ItemStack goldenApple = new ItemUtils(Material.GOLDEN_APPLE,20)
                .setDisplayName(ColorsUtil.translate.apply("&eGapp"))
                .build();

        ItemStack leatherHelmet = new ItemUtils(Material.LEATHER_HELMET,1)
                .addEnchants(Enchantment.PROTECTION_ENVIRONMENTAL,1)
                .build();

        ItemStack leatherChestplate = new ItemUtils(Material.LEATHER_CHESTPLATE,1)
                .build();

        ItemStack leatherLeggings = new ItemUtils(Material.LEATHER_LEGGINGS,1)
                .addEnchants(Enchantment.PROTECTION_ENVIRONMENTAL,1)
                .build();

        ItemStack leatherBoots = new ItemUtils(Material.LEATHER_BOOTS,1)
                .addEnchants(Enchantment.PROTECTION_ENVIRONMENTAL,1)
                .build();

        armorList.add(leatherHelmet);
        armorList.add(leatherChestplate);
        armorList.add(leatherLeggings);
        armorList.add(leatherBoots);

        if(team.equals(Teams.RED)){
            for(ItemStack armor : armorList){
                LeatherArmorMeta leatherMeta =(LeatherArmorMeta) leatherHelmet.getItemMeta();
                leatherMeta.setColor(Color.RED);
                armor.setItemMeta(leatherMeta);
            }
            List<Player> members = instance.getTeamsManager().getMembers(Teams.RED)
                    .stream()
                    .map(Bukkit::getPlayer)
                    .collect(Collectors.toList());

            members.forEach(mem->{
                if(mem != null) {
                    mem.getInventory().addItem(sword);
                    mem.getInventory().setItem(8, pickaxe);
                    mem.getInventory().addItem(goldenApple);
                    MiscUtils.changeArmor(mem, armorList);

                    for (int i = 0; i < 7; i++) {
                        mem.getInventory().addItem(new ItemStack(Material.SANDSTONE, 64));
                    }
                }
            });

        }else if(team.equals(Teams.BLUE)){
            for(ItemStack armor : armorList){
                LeatherArmorMeta leatherMeta =(LeatherArmorMeta) leatherHelmet.getItemMeta();
                leatherMeta.setColor(Color.BLUE);
                armor.setItemMeta(leatherMeta);
            }
            List<Player> members = instance.getTeamsManager().getMembers(Teams.BLUE)
                    .stream()
                    .map(Bukkit::getPlayer)
                    .collect(Collectors.toList());

            if(members == null || members.isEmpty()) return;

            members.forEach(mem->{
                if(mem != null) {
                    mem.getInventory().addItem(sword);
                    mem.getInventory().setItem(8, pickaxe);
                    mem.getInventory().addItem(goldenApple);
                    MiscUtils.changeArmor(mem, armorList);
                    for (int i = 0; i < 7; i++) {
                        mem.getInventory().addItem(new ItemStack(Material.SANDSTONE, 64));
                    }
                }
            });



        }
    }


    public World getRandomWorld(){
        try {
            List<File> fileList = Arrays.asList(instance.getServer().getWorldContainer()
                    .listFiles());
            List<File> worlds = fileList.stream().filter(f->f.getName().startsWith("team"))
                    .collect(Collectors.toList());

            int chosenIndex = new Random().nextInt(worlds.size());

            System.out.println(chosenIndex);

            System.out.println(worlds);
            System.out.println(worlds.get(chosenIndex).getName());

            return instance.getServer().getWorld(worlds.get(chosenIndex).getName());
        }catch (Exception e){
            System.out.println("Erreur lors du choix des maps !");
        }

        return Bukkit.getWorld("world");


    }

    public void checkAce(Teams deathTeam) {
        Teams invertTeam = Teams.getInvertTeam(deathTeam);
        if (this.round.getAlivePlayersFrom(invertTeam).size() == instance.getTeamsManager().getMaxTeamPLayer()) {
            this.setWinMode(WinMode.ACE);
        }else{
            this.setWinMode(WinMode.CLASSIC);
        }
    }
    public WinMode getWinMode(){
        return this.winMode == null ? WinMode.CLASSIC : winMode;
    }

    public void setWinMode(WinMode mode){
        this.winMode = mode;
    }

    public TablistManager getTablistManager() {
        return tablistManager;
    }

    public PlayerStatsManager getPlayerStatsManager() {
        return playerStatsManager;
    }

    public List<UUID> getGameSpectator() {
        return gameSpectator;
    }

    public void sendEndMessage(Player player){
        Teams winner = this.getWinner();
        String bestPlayer = this.getPlayerStatsManager().getHighestKiller() == null ? "Aucun" :this.getPlayerStatsManager().getHighestKiller().getName();
        List<UUID> sortedKillers = this.getPlayerStatsManager().getSortedKillers();
        player.sendMessage(ColorsUtil.translate.apply("&7&m--------- &6&lVictoire " + winner.getTeamColorCode()
                + winner.getTeamName() + "&7&m ---------"));
        player.sendMessage(" ");
        player.sendMessage(" ");
        player.sendMessage(ColorsUtil.translate.apply("&6&lMeilleur joueur: " + bestPlayer));
        player.sendMessage(" ");
        player.sendMessage(ColorsUtil.translate.apply("&7Classement"));

        for(int i = 0;i<sortedKillers.size();i++){
            int playerKills = instance.getGameManager().getPlayerStatsManager().getKills(sortedKillers.get(i)) == -1 ? 0 :instance.getGameManager().getPlayerStatsManager().getKills(sortedKillers.get(i));

            player.sendMessage(ColorsUtil.translate.apply("&7"+(i+1) +"&7- " +
                    Bukkit.getOfflinePlayer(sortedKillers.get(i)).getName() + "&7 - " + playerKills));
        }


    }
}
