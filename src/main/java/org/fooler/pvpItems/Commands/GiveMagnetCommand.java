package org.fooler.pvpItems.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.fooler.pvpItems.PvpItems;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GiveMagnetCommand implements CommandExecutor {
    private final PvpItems plugin;
    FileConfiguration config;

    public GiveMagnetCommand(PvpItems plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String lable, String[] args) {

        if (!(sender instanceof Player player)) return false;


        if (!(cmd.getName().equalsIgnoreCase("pvpitems") && args.length > 0)) return false;
        if (!(args[0].equalsIgnoreCase("give"))) {
            sender.sendMessage("Использование: /pvpitems give <предмет> <количество>");
            return false;
        }

        if (args[1].equalsIgnoreCase("magnet")) {
            ItemStack magnet = new ItemStack(Material.PRISMARINE_SHARD);
            ItemMeta magnetMeta = magnet.getItemMeta();
            magnetMeta.setDisplayName(ChatColor.GOLD + "Attracter");

            List<String> lore = new ArrayList<>();
            for (String loreString : config.getStringList("items.magnet.lore")) {
                lore.add(ChatColor.WHITE + loreString);
            }
            magnetMeta.setLore(lore);

            magnetMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "magnet"), PersistentDataType.BYTE, (byte) 1);

            magnet.setItemMeta(magnetMeta);
            player.getInventory().addItem(magnet);
        } else {
            sender.sendMessage("Использование: /pvpitems give <предмет> <количество>");
            return false;
        }




        return true;
    }


}
