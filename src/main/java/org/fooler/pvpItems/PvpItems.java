package org.fooler.pvpItems;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.fooler.pvpItems.Commands.CommandCompleter;
import org.fooler.pvpItems.Commands.GiveCommand;
import org.fooler.pvpItems.Listeners.InverterCastListener;
import org.fooler.pvpItems.Listeners.MagnetCastListener;
import org.fooler.pvpItems.Managers.CooldownManager;
import org.fooler.pvpItems.Managers.InverterManager;

import java.util.Objects;

public final class PvpItems extends JavaPlugin {
    public static StateFlag PVP_ITEMS_USAGE;
    private static volatile PvpItems instance;
    private CooldownManager cooldownManager;
    private InverterManager inverterManager;
    ConfigurationSection itemsSection;

    @Override
    public void onLoad() {
        if (WorldGuard.getInstance().getFlagRegistry().get("pvpitems-use") == null) {

            FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
            try {
                StateFlag flag = new StateFlag("pvpitems-use", true);
                registry.register(flag);
                PVP_ITEMS_USAGE = flag;
            } catch (FlagConflictException e) {
                Flag<?> existing = registry.get("pvpitems-use");
                if (existing instanceof StateFlag) {
                    PVP_ITEMS_USAGE = (StateFlag) existing;
                }
            }
        }
    }


    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        inverterManager = new InverterManager();

        this.cooldownManager = new CooldownManager(this);
        this.itemsSection = getConfig().getConfigurationSection("items");
        cooldownManager.registerCooldownType("magnet", itemsSection.getInt("magnet.cooldown-time"));
        cooldownManager.registerCooldownType("reflector", itemsSection.getInt("reflector.cooldown-time"));


        getServer().getPluginManager().registerEvents(new MagnetCastListener(this, cooldownManager), this);
        getServer().getPluginManager().registerEvents(new InverterCastListener(this, cooldownManager), this);

        this.getCommand("pvpitems").setExecutor(new GiveCommand(this));
        this.getCommand("pvpitems").setTabCompleter(new CommandCompleter());
    }

    public ConfigurationSection getItemsSection() {
        return itemsSection;
    }

    @Override
    public void onDisable() {}

    public static PvpItems getInstance() {
        return instance;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public InverterManager getInverterManager() {
        return inverterManager;
    }
}
