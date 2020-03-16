package com.iceprosurface.deathresurrection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public final class DeathResurrection extends JavaPlugin {
    private static DeathResurrection instance;
    public Config config;

    @Override
    public void onEnable() {
        instance = this;
        config = new Config();
        // Plugin startup logic
        System.out.println("DeathResurrection was run");
        getServer().getPluginManager().registerEvents(new DeadListener(), this);
        getServer().getPluginManager().registerEvents(new PreventOperation(), this);
        this.getCommand("dr").setTabCompleter(new Commanders());
        this.getCommand("dr").setExecutor(new Commanders());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("DeathResurrection was off");
    }

    public static DeathResurrection getInstance(){
        return instance;
    }
    public boolean resurrectPlayer(Player target) {
        unbanPlayer(target);
        List<Integer> respawnPoint = instance.config.getConfig().getIntegerList("RespawnPoint");
        String respawnWorld = instance.config.getConfig().getString("RespawnWorld");
        if (respawnWorld == null) {
            return false;
        }
        World world = instance.getServer().getWorld(respawnWorld);
        Location respawn = new Location(
                world,
                respawnPoint.get(0).doubleValue(),
                respawnPoint.get(1).doubleValue(),
                respawnPoint.get(2).doubleValue()
        );
        if (world == null) {
            return false;
        }
        target.teleport(respawn);
        world.strikeLightningEffect(respawn);
        return true;
    }
    public boolean payCostOfResurrection (Player from) {
        FileConfiguration cfg = config.getConfig();
        int hpCost = cfg.getInt("ResurrectionCosts.Hp");
        boolean enchantItemInHand = cfg.getBoolean("ResurrectionCosts.EnchantItemsInHand");
        double health = from.getHealth();
        if (health < hpCost) {
            String NotEnoughHp = config.getConfig().getString("NotEnoughHp");

            //"你至少需要" + hpCost + "来复活队友"
            from.sendMessage(ChatColor.RED + NotEnoughHp.replace("%hp%", hpCost + ""));
            return false;
        }
        PlayerInventory inv = from.getInventory();
        ItemStack handItem = inv.getItemInMainHand();
        if (enchantItemInHand) {
            if (handItem.getMaxStackSize() != 1) {
                 //"你必须使用正确的附魔装备来复活队友"
                from.sendMessage(ChatColor.RED + config.getConfig().getString("NoEnchantItem"));
                return false;
            }

            Map<Enchantment, Integer> enchants = handItem.getEnchantments();
            AtomicInteger totalLevel = new AtomicInteger();
            enchants.forEach((enchant, level) -> totalLevel.addAndGet(level));
            if (totalLevel.get() == 0) {
                from.sendMessage(ChatColor.RED + config.getConfig().getString("NoEnchantItem"));
                return false;
            }
        }
        HashMap<Material, Integer> invMap = mapInventory(inv);
        HashMap<Material, Integer>  needResources = config.getNeedResource();
        if (!isOblationEnough(invMap, needResources, from)) {
            from.sendMessage(ChatColor.RED + config.getConfig().getString("NotEnough"));
            return false;
        }
        removeItems(inv, needResources);
        from.setHealth(health - hpCost);
        if (enchantItemInHand) {
            inv.setItemInMainHand(null);
        }
        return true;
    }
    public static HashMap<Material, Integer> mapInventory (Inventory inventory) {
        HashMap<Material, Integer> total = new HashMap<>();
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) {
                continue;
            }
            Material type = is.getType();
            if (type == null) {
                continue;
            }
            AtomicInteger number = new AtomicInteger();
            if (total.get(type) != null) {
                number.set(total.get(type));
            }
            total.put(type, number.get() + is.getAmount());
        }
        return total;
    }
    public boolean isOblationEnough(HashMap<Material, Integer> invMap, HashMap<Material, Integer> needResource, Player from) {
        AtomicBoolean flag = new AtomicBoolean(true);
        needResource.forEach((m, i) -> {
            if (invMap.get(m) == null) {
                return;
            }
            int amount = invMap.get(m) - i;
            if (amount < 0) {
                flag.set(false);
                // "你缺少" + (-amount) + "个" + m.name()
                String NotEnoughDetail = config.getConfig().getString("NotEnoughDetail");

                from.sendMessage(ChatColor.YELLOW +
                        NotEnoughDetail.replace("%count%", (-amount) + "")
                            .replace("%name%", m.name())
                );

            }
        });
        return flag.get();
    }


    public static void removeItems(Inventory inventory, HashMap<Material, Integer> needResource) {
        int size = inventory.getSize();
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) {
                continue;
            }
            Material m = is.getType();
            if (m == null) {
                continue;
            }
            if (needResource.get(m) == null) {
                continue;
            }
            int amount = needResource.get(m);
            int newAmount = is.getAmount() - amount;
            if (newAmount > 0) {
                is.setAmount(newAmount);
            } else {
                inventory.clear(slot);
                needResource.put(m, -newAmount);
            }
        }
    }
    public void unbanPlayer (UUID playerUUid) {
        config.getBanListConfig().set("list." + playerUUid , null);
    }
    public void unbanPlayer (Player player) {
        unbanPlayer(player.getUniqueId());
    }
    public void banPlayer (UUID playerUUid, String playerName) {
        FileConfiguration banListConfig = config.getBanListConfig();
        banListConfig.set("list." + playerUUid + ".status", true);
        banListConfig.set("list." + playerUUid + ".name", playerName);
        config.setBanListConfig();
    }
    public void banPlayer(Player player) {
        banPlayer(player.getUniqueId(), player.getDisplayName());
    }
    public boolean isPlayerBaned (UUID playerUUid) {
        return config.getBanListConfig().getBoolean("list." + playerUUid + ".status");
    }
    public boolean isPlayerBaned (Player player) {
        return this.isPlayerBaned(player.getUniqueId());
    }
}
