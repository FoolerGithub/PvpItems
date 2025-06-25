package org.fooler.pvpItems;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.fooler.pvpItems.Commands.GiveMagnetCommand;
import org.fooler.pvpItems.Listeners.RightClickListener;

import java.util.Objects;

public final class PvpItems extends JavaPlugin {
    public static StateFlag PVP_ITEMS_USAGE;

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
        this.saveDefaultConfig();


        PluginCommand basicCmd = Objects.requireNonNull(getCommand("pvpitems"));
        getServer().getPluginManager().registerEvents(new RightClickListener(this), this);
        basicCmd.setExecutor(new GiveMagnetCommand(this));
    }

    @Override
    public void onDisable() {}

}
