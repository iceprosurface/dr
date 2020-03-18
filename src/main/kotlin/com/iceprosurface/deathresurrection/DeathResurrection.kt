package com.iceprosurface.deathresurrection

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.HashMap

class DeathResurrection : JavaPlugin() {
    lateinit var config: Config
    lateinit var enchant: Enchant
    override fun onEnable() {
        instance = this
        config = Config()
        enchant = Enchant()
        // Plugin startup logic
        println("DeathResurrection was run")
        server.pluginManager.registerEvents(DeadListener(), this)
        server.pluginManager.registerEvents(PreventOperation(), this)
        getCommand("dr")!!.tabCompleter = Commanders()
        getCommand("dr")!!.setExecutor(Commanders())
    }

    override fun onDisable() {
        // Plugin shutdown logic
        println("DeathResurrection was off")
    }
    fun resurrectPlayer(target: Player): Boolean {
        unbanPlayer(target)
        val cfg = config.config ?: return false
        val respawnPoint = cfg.getIntegerList("RespawnPoint")
        val respawnWorld = cfg.getString("RespawnWorld") ?: return false
        val world = instance.server.getWorld(respawnWorld)
        if (respawnPoint.size != 3) {
            return false
        }
        val respawn = Location(
                world,
                respawnPoint[0].toDouble(),
                respawnPoint[1].toDouble(),
                respawnPoint[2].toDouble()
        )
        if (world == null) {
            return false
        }
        target.teleport(respawn)
        world.strikeLightningEffect(respawn)
        return true
    }

    fun payCostOfResurrection(from: Player): Boolean {
        val cfg = config.config ?: return false
        val hpCost = cfg.getInt("ResurrectionCosts.Hp")
        val enchantItemInHand = cfg.getBoolean("ResurrectionCosts.EnchantItemsInHand")
        val health = from.health
        if (health < hpCost) {
            val notEnoughHp = cfg.getString("NotEnoughHp")!!
            from.sendMessage(ChatColor.RED.toString() + notEnoughHp.replace("%hp%", hpCost.toString() + ""))
            return false
        }
        val inv = from.inventory
        val handItem = inv.itemInMainHand
        if (enchantItemInHand) {
            if (handItem.maxStackSize != 1) {
                from.sendMessage(ChatColor.RED.toString() + (cfg.getString("NoEnchantItem")))
                return false
            }
            val enchants = handItem.enchantments
            val totalLevel = AtomicInteger()
            enchants.forEach { (_, level) -> totalLevel.addAndGet(level ?: 0) }

            if (!enchant.checkEnchant(enchants)) {
                from.sendMessage(ChatColor.RED.toString() + (cfg.getString("NoCorrectEnchantItem")))
                return false
            }
            if (totalLevel.get() == 0) {
                from.sendMessage(ChatColor.RED.toString() + (cfg.getString("NoEnchantItem")))
                return false
            }
        }
        val invMap = mapInventory(inv)
        val needResources = config.needResource
        if (!isOblationEnough(invMap, needResources, from)) {
            from.sendMessage(ChatColor.RED.toString() + (config.config?.getString("NotEnough")))
            return false
        }
        removeItems(inv, needResources)
        from.health = health - hpCost
        if (enchantItemInHand) {
            inv.setItemInMainHand(null)
        }
        return true
    }

    private fun isOblationEnough(invMap: HashMap<Material, Int?>, needResource: HashMap<Material, Int>, from: Player): Boolean {
        val flag = AtomicBoolean(true)
        needResource.forEach { (m: Material, i: Int?) ->
            if (invMap[m] == null) {
                return@forEach
            }
            val amount = invMap[m]?.minus(i) ?: return@forEach
            if (amount < 0) {
                flag.set(false)
                // "你缺少" + (-amount) + "个" + m.name()
                val notEnoughDetail = config.config?.getString("NotEnoughDetail")!!
                from.sendMessage(ChatColor.YELLOW.toString() +
                        notEnoughDetail.replace("%count%", (-amount).toString())
                                .replace("%name%", m.name))
            }
        }
        return flag.get()
    }

    private fun unbanPlayer(playerUUid: UUID) {
        config.banListConfig?.set("list.$playerUUid", null)
        config.setConfig(Config.BAN_LIST)
    }

    private fun unbanPlayer(player: Player) {
        unbanPlayer(player.uniqueId)
    }

    private fun banPlayer(playerUUid: UUID, playerName: String?) {
        val banListConfig = config.banListConfig ?: return
        banListConfig["list.$playerUUid.status"] = true
        banListConfig["list.$playerUUid.name"] = playerName
        config.setBanListConfig()
    }

    fun banPlayer(player: Player) {
        banPlayer(player.uniqueId, player.displayName)
    }

    fun isPlayerBaned(playerUUid: UUID): Boolean {
        return config.banListConfig?.getBoolean("list.$playerUUid.status") ?: false
    }

    fun isPlayerBaned(player: Player): Boolean {
        return this.isPlayerBaned(player.uniqueId)
    }

    companion object {
        lateinit var instance: DeathResurrection
            private set

        fun mapInventory(inventory: Inventory): HashMap<Material, Int?> {
            val total = HashMap<Material, Int?>()
            val size = inventory.size
            for (slot in 0 until size) {
                val `is` = inventory.getItem(slot) ?: continue
                val type = `is`.type
                val number = AtomicInteger()
                if (total[type] != null) {
                    number.set(total[type]!!)
                }
                total[type] = number.get() + `is`.amount
            }
            return total
        }

        fun removeItems(inventory: Inventory, needResource: HashMap<Material, Int>) {
            val size = inventory.size
            for (slot in 0 until size) {
                val `is` = inventory.getItem(slot) ?: continue
                val m = `is`.type
                if (needResource[m] == null) {
                    continue
                }
                val amount = needResource[m]!!
                val newAmount = `is`.amount - amount
                if (newAmount > 0) {
                    `is`.amount = newAmount
                } else {
                    inventory.clear(slot)
                    needResource[m] = -newAmount
                }
            }
        }
    }
}