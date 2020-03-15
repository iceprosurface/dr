package com.iceprosurface.deathresurrection;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class PreventOperation implements Listener {

    public boolean checkPermissions (Player player, Cancellable event) {
        DeathResurrection instance = DeathResurrection.getInstance();
        List<String> worlds = instance.config.getConfig().getStringList("worlds");
        if (worlds == null || (!worlds.contains(player.getWorld().getName()))) {
            return false;
        }
        if (DeathResurrection.getInstance().isPlayerBaned(player.getUniqueId())) {
            event.setCancelled(true);
            return false;
        }
        return true;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        checkPermissions(event.getPlayer(), event);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerArmorStandManipulateEvent(PlayerArmorStandManipulateEvent event) {
        checkPermissions(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerArmorStandManipulateEvent event) {
        checkPermissions(event.getPlayer(), event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void  onPlayerInteract(PlayerInteractEvent event) {
        if (checkPermissions(event.getPlayer(), event)) {
            return;
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof Player) {
            checkPermissions((Player)damager, event);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            checkPermissions((Player) entity, event);
        }
    }
}
