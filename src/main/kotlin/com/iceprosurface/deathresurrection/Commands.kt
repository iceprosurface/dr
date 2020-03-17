package com.iceprosurface.deathresurrection

import org.bukkit.ChatColor
import org.bukkit.block.BlockFace
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.util.*

class Commanders : TabCompleter, CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val instance: DeathResurrection = DeathResurrection.instance
        val server = instance.server
        val config: FileConfiguration? = instance.config.config
        if (sender !is Player) {
            return true
        }
        if (args.size <= 1 && args[0] === RESURRECT) {
            sender.sendMessage(ChatColor.RED.toString() + config!!.getString("MustHaveTarget"))
            return true
        }
        val targetPlayer: Player?
        when (args[0]) {
            RESURRECT -> {
                targetPlayer = server.getPlayer(args[1])
                if (targetPlayer == null) {
                    sender.sendMessage(ChatColor.RED.toString() + config!!.getString("MustHaveTarget"))
                    return true
                }
                val feet = targetPlayer.location.block
                val ground = feet.getRelative(BlockFace.DOWN)
                if (!ground.type.isSolid) {
                    sender.sendMessage(ChatColor.RED.toString() + config!!.getString("NotSafe"))
                    return true
                }
                if (sender === targetPlayer) {
                    sender.sendMessage(ChatColor.RED.toString() + config!!.getString("ResurrectSelf"))
                    return true
                }
                val oped = args.size == 3 && sender.isOp && args[2] === "oped"
                if (!oped && instance.isPlayerBaned(sender)) {
                    sender.sendMessage(ChatColor.RED.toString() + config!!.getString("ResurrectOtherInExiled"))
                    return true
                }
                if (!instance.isPlayerBaned(targetPlayer.uniqueId)) {
                    sender.sendMessage(ChatColor.RED.toString() +
                            (config!!.getString("AlreadyExiled")
                                    ?.replace("%player%", targetPlayer.displayName) ?: ""))
                    return true
                }
                val isSuccess = instance.payCostOfResurrection(sender)
                if (!isSuccess) {
                    return true
                }
                instance.resurrectPlayer(targetPlayer)
                instance.server.broadcastMessage(ChatColor.GREEN.toString() +
                        (config!!.getString("SuccessResurrect")
                                ?.replace("%player%", targetPlayer.displayName)
                                ?.replace("%from%", sender.displayName) ?: ""))
                return true
            }
            EXILE -> {
                targetPlayer = server.getPlayer(args[1])
                if (targetPlayer == null) {
                    sender.sendMessage(ChatColor.RED.toString() + config!!.getString("MustHaveTarget"))
                    return true
                }
                if (sender.isOp) {
                    instance.banPlayer(targetPlayer)
                    instance.server.broadcastMessage(ChatColor.YELLOW.toString() +
                            (config!!.getString("SuccessExiled")
                                    ?.replace("%player%", targetPlayer.displayName) ?: ""))
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + config!!.getString("OpOnly"))
                }
                return true
            }
            CONFIG -> {
                if (sender.isOp) {
                    if (args[1] == "reload") {
                        sender.sendMessage(ChatColor.GREEN.toString() + "finish reload")
                        instance.config.reload()
                    }
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + config!!.getString("OpOnly"))
                }
                return true
            }
            RESPAWN -> {
                if (sender.isOp) {
                    val cfg = instance.config.config
                    val nowWorld = sender.world
                    val worldName = nowWorld.name
                    val location = sender.location
                    val respawn = intArrayOf(location.blockX, location.blockY, location.blockZ)
                    cfg?.set("RespawnPoint", respawn)
                    cfg?.set("RespawnWorld", worldName)
                    instance.config.setConfig()
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + config!!.getString("OpOnly"))
                }
                return true
            }
            INFO -> {
                if (instance.isPlayerBaned(sender)) {
                    sender.sendMessage(ChatColor.RED.toString() + config!!.getString("Exiled"))
                } else {
                    sender.sendMessage(ChatColor.GREEN.toString() + config!!.getString("Normal"))
                }
                return true
            }
            else -> {
            }
        }
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String>? {
        val instance: DeathResurrection = DeathResurrection.instance
        val server = instance.server
        val completions: MutableList<String> = ArrayList()
        if (args.size == 1) {
            StringUtil.copyPartialMatches(args[0], listOf(*COMMANDS), completions)
        } else if (args.size == 2) {
            val players: MutableList<String> = ArrayList()
            when(args[0]) {
                CONFIG -> {
                    completions.add("reload")
                }
                else -> {
                    server.onlinePlayers.forEach { player: Player -> players.add(player.displayName) }
                    StringUtil.copyPartialMatches(args[1], players, completions)
                }
            }
        }
        completions.sort()
        return completions
    }

    companion object {
        private const val RESURRECT = "resurrect"
        private const val EXILE = "exile"
        private const val CONFIG = "config"
        private const val RESPAWN = "respawn"
        private const val INFO = "info"
        val COMMANDS = arrayOf(
            RESURRECT,
            EXILE,
            CONFIG,
            RESPAWN,
            INFO
        )
    }
}