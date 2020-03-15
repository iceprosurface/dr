package com.iceprosurface.deathresurrection;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Commanders implements TabCompleter, CommandExecutor {
    private static final String RESURRECT = "resurrect";
    private static final String EXILE = "exile";
    private static final String CONFIG = "config";
    private static final String[] COMMANDS = {RESURRECT, EXILE, CONFIG};

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        DeathResurrection instance = DeathResurrection.getInstance();
        Server server = instance.getServer();
        FileConfiguration config = instance.getConfig();
        if (!(sender instanceof Player)) {
            return true;
        }
        Player from = (Player) sender;
        if (args.length <= 1) {
            sender.sendMessage(ChatColor.RED + config.getString("MustHaveTarget"));
            return true;
        }
        Player targetPlayer = server.getPlayer(args[1]);
        switch (args[0]) {
            case RESURRECT:
                assert targetPlayer != null;
                Block feet = targetPlayer.getLocation().getBlock();
                Block ground = feet.getRelative(BlockFace.DOWN);
                if (!ground.getType().isSolid()) {
                    sender.sendMessage(ChatColor.RED + config.getString("NotSafe"));
                    return true;
                }
                if (!from.isOp() && instance.isPlayerBaned(from)) {
                    sender.sendMessage(ChatColor.RED + config.getString("ResurrectSelf"));
                    return true;
                }
                if (!instance.isPlayerBaned(targetPlayer.getUniqueId())) {
                    sender.sendMessage(ChatColor.RED +
                            config.getString("AlreadyExiled")
                                    .replace("%player%", targetPlayer.getDisplayName())
                    );
                    return true;
                }
                boolean isSuccess = instance.payCostOfResurrection(from);
                if (!isSuccess) {
                    return true;
                }
                instance.unbanPlayer(targetPlayer);
                sender.sendMessage(ChatColor.GREEN +
                        config.getString("SuccessResurrect")
                                .replace("%player%", targetPlayer.getDisplayName())
                                .replace("%from%", from.getDisplayName())
                );
                return true;
            case EXILE:
                if (from.isOp()) {
                    instance.banPlayer(targetPlayer);
                    sender.sendMessage(ChatColor.GREEN +
                            config.getString("SuccessExiled")
                                    .replace("%player%", targetPlayer.getDisplayName())
                    );
                } else {
                    sender.sendMessage(ChatColor.RED + config.getString("OpOnly"));
                }
                return true;
            case CONFIG:
                if (args[1].equals("reload")) {
                    from.sendMessage(ChatColor.GREEN + "finish reload");
                    instance.config.reload();
                }
                return true;
            default:
        }
        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        DeathResurrection instance = DeathResurrection.getInstance();
        Server server = instance.getServer();
        final List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], Arrays.asList(COMMANDS), completions);
        } else if (args.length == 2) {
            List<String> players = new ArrayList<>();
            if (CONFIG.equals(args[0])) {
                completions.add("reload");
            } else {
                server.getOnlinePlayers().forEach((player -> players.add(player.getDisplayName())));
                StringUtil.copyPartialMatches(args[1], players, completions);
            }
        }
        Collections.sort(completions);
        return completions;
    }

}
