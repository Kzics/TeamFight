package fr.sweeftyz.teamfights.menu;

import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.Teams;
import fr.sweeftyz.teamfights.utils.ColorsUtil;
import fr.sweeftyz.teamfights.utils.items.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlayerTeamMenu {


    private final Main instance;
    private final Inventory inv;
    private static String title;

    public PlayerTeamMenu(final Main instance){
        this.instance = instance;
        this.inv = instance.getServer().createInventory(null,9,"Choix d'équipes");

        title = inv.getTitle();
    }



    public void openInv(Player player){
        ItemStack redWool = new ItemUtils(Material.WOOL,1)
                .setDisplayName(ColorsUtil.translate.apply("&cRouge"))
                .addLores(this.getTranslatedTeam(Teams.RED))
                .setDurability((short)14)
                .build();

        ItemStack blueWool = new ItemUtils(Material.WOOL,1)
                .setDisplayName(ColorsUtil.translate.apply("&9Bleu"))
                .addLores(this.getTranslatedTeam(Teams.BLUE))
                .setDurability((short) 11)
                .build();

        this.inv.setItem(0,redWool);
        this.inv.setItem(1,blueWool);

        player.openInventory(this.inv);
    }

    public static String getTitle(){
        return title;
    }

    private List<String> getTranslatedTeam(Teams team){
        List<String> translated = new ArrayList<>();

        List<UUID> members = instance.getTeamsManager().getMembers(team);
        String teamColor = team.getTeamColorCode();
        try {
            List<Player> playerList = members.stream().map(member -> instance.getServer().getPlayer(member))
                    .collect(Collectors.toList());

            for(int i = 0;i<5;i++) {
                if (i < playerList.size()) {
                    translated.add(String.format(ColorsUtil.translate.apply("&7-%s %s \n"), teamColor, playerList.get(i).getName()));
                }else{
                    translated.add(ColorsUtil.translate.apply("&7-"));
                }
            }

        }catch (Exception e){
            instance.getServer().getLogger().severe("Error while translating uuid to players from team " + team.getTeamName());
        }

        return translated;
    }


}
