package org.fooler.pvpItems.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.fooler.pvpItems.PvpItems;
import org.fooler.pvpItems.Utils.ItemBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class GiveCommand implements CommandExecutor {
    private final PvpItems plugin;
    FileConfiguration config;

    public GiveCommand(PvpItems plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String lable, String[] args) {
        if (!(sender instanceof Player player)) return false;


        if (!(cmd.getName().equalsIgnoreCase("pvpitems"))) return true;
        if (!args[0].equalsIgnoreCase("give") || args.length != 3 || !sender.hasPermission("pvpitems.commands")) {
            sender.sendMessage("Данной команды не существует!");
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

        } else if (args[1].equalsIgnoreCase("reflector")) {
            ItemStack reflectorInstance = new ItemBuilder(plugin, Material.BRICK)
                    .amount(amount)
                    .name(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("items.reflector.name"))))
                    .lore("reflector")
                    .flag("reflector")
                    .build();

            player.getInventory().addItem(reflectorInstance);
        }

        return true;
    }


}
