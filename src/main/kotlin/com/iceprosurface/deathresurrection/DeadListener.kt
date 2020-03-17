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
            player.server.broadcastMessage(ChatColor.YELLOW.toString() + player.displayName + "已经在极限模式中死亡，现在将其切换为英灵模式")
            playThunder(player)
        } else {
            player.server.broadcastMessage("英灵" + player.displayName + "再一次阵亡了")
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