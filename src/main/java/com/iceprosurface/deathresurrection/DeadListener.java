package com.iceprosurface.deathresurrection;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DeadListener implements Listener {
    @EventHandler
    public void onPlayerRespawn (PlayerRespawnEvent playerRespawnEvent) {
        Player player = playerRespawnEvent.getPlayer();
        player.getServer().broadcastMessage(player.getDisplayName() + "已经在极限模式中死亡，现在将其切换为观察模式");
        DeathResurrection.getInstance().banPlayer(player);
    }

}
