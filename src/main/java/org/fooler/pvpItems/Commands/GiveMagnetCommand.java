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
import org.fooler.pvpItems.Utils.ItemBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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


        if (!(cmd.getName().equalsIgnoreCase("pvpitems"))) return true;
        if (!args[0].equalsIgnoreCase("give") || args.length != 3) {
            sender.sendMessage("Использование: /pvpitems give <предмет> <количество>");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Количество должно быть числом");
            return true;
        }

        if (args[1].equalsIgnoreCase("magnet")) {
            ItemStack attracterInstance = new ItemBuilder(plugin, Material.PRISMARINE_SHARD)
                    .amount(amount)
                    .name(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("items.magnet.name"))))
                    .lore("magnet")
                    .flag("magnet")
                    .build();

            player.getInventory().addItem(attracterInstance);
        } else if (args[1].equalsIgnoreCase("dezor")) {
            ItemStack dezorInstance = new ItemBuilder(plugin, Material.ENDER_EYE)
                    .amount(amount)
                    .name(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("items.dezor.name"))))
                    .lore("dezor")
                    .flag("dezor")
                    .build();

        }



        return true;
    }


}
