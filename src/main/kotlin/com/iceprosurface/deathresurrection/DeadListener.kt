package com.iceprosurface.deathresurrection

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class DeadListener : Listener {
    @EventHandler
    fun onDeath(playerDeathEvent: PlayerDeathEvent) {
        val player = playerDeathEvent.entity
        val instance: DeathResurrection = DeathResurrection.instance
        if (!instance.isPlayerBaned(player)) {
            val firstDeathStr = instance.config.config?.getString("FirstDeath") ?: return
            player.server.broadcastMessage(
                ChatColor.YELLOW.toString() +
                firstDeathStr.replace("%name%", player.displayName)
            )
            playThunder(player)
        } else {
            val exiledDeath = instance.config.config?.getString("ExiledDeath") ?: return
            player.server.broadcastMessage(
                exiledDeath.replace("%name%", player.displayName)
            )
        }
        DeathResurrection.instance.banPlayer(player)
    }

    private fun playThunder(player: Player) {
        val instance: DeathResurrection = DeathResurrection.instance
        val respawnPoint = instance.config.config?.getIntegerList("RespawnPoint")
        val respawnWorld = instance.config.config?.getString("RespawnWorld") ?: return
        val world = instance.server.getWorld(respawnWorld) ?: return
        if (respawnPoint == null || respawnPoint.size != 3) {
            return
        }
        val respawn = Location(
                world,
                respawnPoint[0].toDouble(),
                respawnPoint[1].toDouble(),
                respawnPoint[2].toDouble()
        )
        val sound = Sound.ITEM_TRIDENT_THUNDER
        world.playSound(respawn, sound, 3.0f, 0.5f)
        for (worldPlayer in world.players) {
            if (player === worldPlayer) {
                continue
            }
            worldPlayer.playSound(worldPlayer.location, sound, 2f, 0.3f)
        }
    }
}