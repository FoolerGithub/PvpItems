package org.fooler.pvpItems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CommandCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            return complete(Arrays.asList("give"), args[0]); // new args in future
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return complete(Arrays.asList("magnet", "reflector"), args[1]);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return complete(Arrays.asList("16", "32", "64"), args[2]);
        }

        return Collections.emptyList();

    }

    private List<String> complete(List<String> options, String current) {

        return options.stream().filter(opt -> opt.toLowerCase().startsWith(current.toLowerCase()))
                .collect(Collectors.toList());
    }
}
