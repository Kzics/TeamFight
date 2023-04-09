package fr.sweeftyz.teamfights.utils;

import com.google.common.base.Strings;
import fr.sweeftyz.teamfights.Main;
import fr.sweeftyz.teamfights.enums.GameState;
import fr.sweeftyz.teamfights.enums.Teams;
import fr.sweeftyz.teamfights.enums.WinMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;

public class MiscUtils {

    public static Function<Integer,String> getCorrectTimer = (timer)-> timer > 1 ? String.format("%s secondes",timer) : String.format("%s seconde",timer);

    public static String convertSeconds(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        String result = "";
        if (hours > 0) {
            result += hours + "h ";
        }
        if (minutes > 0 || hours > 0) {
            result += minutes + "m ";
        }
        result += secs + "s";
        return result;
    }

    public static String getProgressBar(int current, int max, int totalBars, String symbol, ChatColor completedColor,
                                        ChatColor notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat("" + completedColor + symbol, progressBars)
                + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
    }

    public static String stateTranslator(GameState state){
        switch (state){
            case PLAYING:
                return "En jeu";
            case END:
                return "Fin de partie";
            case PREROUND:
                return "Avant-Manche";
        }

        return "Non spécifié";

    }

    public static void resetPlayer(Player player){
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.getInventory().clear();
    }

    public static Object getPrivateField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    public static Field getField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static void changeArmor(Player player, List<ItemStack> armorItems) {
        if (armorItems.size() != 4) {
            throw new IllegalArgumentException("La liste d'objets ItemStack d'armure doit contenir exactement 4 éléments.");
        }

        for (int i = 0; i < 4; i++) {
            ItemStack armorItem = armorItems.get(i);

            if (armorItem.getType() == Material.AIR) {
                continue;
            }

            ItemMeta meta = armorItem.getItemMeta();
            meta.spigot().setUnbreakable(true);

            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,2,false);

            armorItem.setItemMeta(meta);

            switch (i) {
                case 0:
                    player.getInventory().setHelmet(armorItem);
                    break;
                case 1:
                    player.getInventory().setChestplate(armorItem);
                    break;
                case 2:
                    player.getInventory().setLeggings(armorItem);
                    break;
                case 3:
                    player.getInventory().setBoots(armorItem);
                    break;
                default:
                    break;
            }
        }
    }

    public static Long minToMiliseconds(int min){
        return min *60000L;
    }

    public static void sendEndRoundMessage(final Main instance, final WinMode winMode,@Nullable  final Teams winnerTeam){
        for (Player p: instance.getServer().getOnlinePlayers()) {
            new ActionMessage("&6&l" + winMode.getWinName()).sendTitle(p, 10, 70, 5);

            if (winnerTeam != null) {
                new ActionMessage(String.format("&aVictoire %s %s", winnerTeam.getTeamColorCode(),
                        winnerTeam.getTeamName()))
                        .sendSubTitle(p, 10, 70, 5);
            }
        }
    }

    public static void hideFromAll(Player player){
        for(Player o : Bukkit.getOnlinePlayers()){
            if(o.equals(player)) continue;

            if(o.canSee(player)){
                o.hidePlayer(player);
            }
        }
    }

    public static <T> T nthElement(Iterable<T> data, int n){
        int index = 0;
        for(T element : data){
            if(index == n){
                return element;
            }
            index++;
        }
        return null;
    }

    public static void showFromAll(Player player){
        for(Player o : Bukkit.getOnlinePlayers()){
            if(o.equals(player)) continue;

            if(!o.canSee(player)){
                o.showPlayer(player);
            }
        }
    }

    public static String getPointSymbol(){
        return "⬤";
    }


}
