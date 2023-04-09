package fr.sweeftyz.teamfights.utils.items;

import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.NBTTagString;
import org.bukkit.Material;

import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NBTUtils {


    public static ItemStack getUpdatedNmsStack(ItemStack farmItem, String key, String value){

        net.minecraft.server.v1_10_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(farmItem);
        NBTTagCompound tag = nmsItem.getTag() != null ? nmsItem.getTag() : new NBTTagCompound();
        tag.set(key,new NBTTagString(value));
        nmsItem.setTag(tag);

        farmItem.getItemMeta().spigot().setUnbreakable(true);

        return CraftItemStack.asCraftMirror(nmsItem);
    }

    public static boolean hasKey(ItemStack it,String key){
        if(it == null || it.getType().equals(Material.AIR)) return false;

        net.minecraft.server.v1_10_R1.ItemStack nmsItem = CraftItemStack.asNMSCopy(it);

        if(!nmsItem.hasTag()) return false;

        return nmsItem.getTag().hasKey(key);

    }
}
