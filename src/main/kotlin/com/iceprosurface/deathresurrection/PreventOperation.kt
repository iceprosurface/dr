package com.iceprosurface.deathresurrection

import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerArmorStandManipulateEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent

class PreventOperation: Listener {
    private fun checkPermission (player: Player, event: Cancellable): Boolean {
        val instance = DeathResurrection.instance
        val worlds = instance.config.config?.getStringList("worlds")
        if (worlds == null || !worlds.contains(player.world.name)) {
            return false
        }
        if (instance.isPlayerBaned(player)) {
            event.isCancelled = true
            return false
        }
        return true
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        checkPermission(event.player, event)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerArmorStandManipulateEvent(event: PlayerArmorStandManipulateEvent) {
        checkPermission(event.player, event)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerInteractEntity(event: PlayerArmorStandManipulateEvent) {
        checkPermission(event.player, event)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (checkPermission(event.player, event)) {
            return
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        if (damager is Player) {
            checkPermission(damager, event)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onEntityPickupItem(event: EntityPickupItemEvent) {
        val entity: Entity = event.entity
        if (entity is Player) {
            checkPermission(entity, event)
        }
    }
}