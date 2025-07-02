package org.fooler.pvpItems.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {
    private final Plugin plugin;
    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(Plugin plugin, Material material) {
        this.plugin = plugin;
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }


    public ItemBuilder name(String displayName) {
        meta.setDisplayName(displayName);
        return this;
    }

    public ItemBuilder lore(String itemType) {
        List<String> lore = plugin.getConfig().getStringList("items." + itemType + ".lore").stream().map((line) -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toList());
        meta.setLore(lore);
        return this;
    }

    public ItemBuilder flag (String key) {
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), PersistentDataType.BYTE, (byte) 1);
        item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder amount (int value) {
        item.setAmount(Math.max(1, Math.min(value, 64)));
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
