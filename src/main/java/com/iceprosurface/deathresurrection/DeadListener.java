package com.iceprosurface.deathresurrection;

import jdk.vm.ci.meta.Local;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

public class DeadListener implements Listener {
    @EventHandler
    public void onDeath(PlayerDeathEvent playerDeathEvent) {
        Player player = playerDeathEvent.getEntity();
        if (player instanceof Player) {
            DeathResurrection instance = DeathResurrection.getInstance();
            if (!instance.isPlayerBaned(player)) {
                player.getServer().broadcastMessage(ChatColor.YELLOW + player.getDisplayName() + "已经在极限模式中死亡，现在将其切换为英灵模式");
                playThunder(player);
            } else {
                player.getServer().broadcastMessage("英灵" + player.getDisplayName() + "再一次阵亡了");
            }
            DeathResurrection.getInstance().banPlayer(player);
        }
    }

    private void playThunder(Player player) {
        DeathResurrection instance = DeathResurrection.getInstance();
        List<Integer> respawnPoint = instance.config.getConfig().getIntegerList("RespawnPoint");
        String respawnWorld = instance.config.getConfig().getString("RespawnWorld");
        if (respawnWorld == null) {
            return;
        }
        World world = instance.getServer().getWorld(respawnWorld);
        if (world == null) {
            return;
        }
        Location respawn = new Location(
                world,
                respawnPoint.get(0).doubleValue(),
                respawnPoint.get(1).doubleValue(),
                respawnPoint.get(2).doubleValue()
        );
        Sound sound = Sound.ITEM_TRIDENT_THUNDER;
        world.playSound(respawn, sound, 3.0F, 0.5F);
        for (Player worldPlayer : world.getPlayers()) {
            if (player == worldPlayer) {
                continue;
            }
            worldPlayer.playSound(worldPlayer.getLocation(), sound,  2f, 0.3f);
        }
    }

}
