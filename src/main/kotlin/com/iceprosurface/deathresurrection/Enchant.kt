package com.iceprosurface.deathresurrection

import org.bukkit.enchantments.Enchantment
class Enchant {
    private var enchantNeed = HashMap<Enchantment, Int>()

    constructor() {
        val cfg = DeathResurrection.instance.config.config ?: return
        val enchantConfigSection = cfg.getConfigurationSection("ResurrectionCosts.EnchantList")
        val enchantObj =  enchantConfigSection!!.getValues(false)
        enchantObj.forEach{
            val key = it.key ?: return@forEach
            val value= it.value.toString()
            val level = Integer.parseInt(value)
            val enchant = enchantMap[key] ?: return@forEach
            enchantNeed[enchant] = level
        }
    }
    fun checkEnchant (map: Map<Enchantment, Int>): Boolean {
        enchantNeed.forEach{
            val enchantItem = it.key
            val targetEnchantLevel = map[enchantItem] ?: return@forEach
            if (targetEnchantLevel < it.value) {
                return false
            }
        }
        return true
    }
    companion object {
        val enchantMap = hashMapOf<String, Enchantment>(
                "ARROW_DAMAGE" to Enchantment.ARROW_DAMAGE,
                "ARROW_FIRE" to Enchantment.ARROW_FIRE,
                "ARROW_INFINITE" to Enchantment.ARROW_INFINITE,
                "ARROW_KNOCKBACK" to Enchantment.ARROW_KNOCKBACK,
                "BINDING_CURSE" to Enchantment.BINDING_CURSE,
                "CHANNELING" to Enchantment.CHANNELING,
                "DAMAGE_ALL" to Enchantment.DAMAGE_ALL,
                "DAMAGE_ARTHROPODS" to Enchantment.DAMAGE_ARTHROPODS,
                "DAMAGE_UNDEAD" to Enchantment.DAMAGE_UNDEAD,
                "DEPTH_STRIDER" to Enchantment.DEPTH_STRIDER,
                "DIG_SPEED" to Enchantment.DIG_SPEED,
                "DURABILITY" to Enchantment.DURABILITY,
                "FIRE_ASPECT" to Enchantment.FIRE_ASPECT,
                "FROST_WALKER" to Enchantment.FROST_WALKER,
                "IMPALING" to Enchantment.IMPALING,
                "KNOCKBACK" to Enchantment.KNOCKBACK,
                "LOOT_BONUS_BLOCKS" to Enchantment.LOOT_BONUS_BLOCKS,
                "LOOT_BONUS_MOBS" to Enchantment.LOOT_BONUS_MOBS,
                "LOYALTY" to Enchantment.LOYALTY,
                "LUCK" to Enchantment.LUCK,
                "LURE" to Enchantment.LURE,
                "MENDING" to Enchantment.MENDING,
                "MULTISHOT" to Enchantment.MULTISHOT,
                "OXYGEN" to Enchantment.OXYGEN,
                "PIERCING" to Enchantment.PIERCING,
                "PROTECTION_ENVIRONMENTAL" to Enchantment.PROTECTION_ENVIRONMENTAL,
                "PROTECTION_EXPLOSIONS" to Enchantment.PROTECTION_EXPLOSIONS,
                "PROTECTION_FALL" to Enchantment.PROTECTION_FALL,
                "PROTECTION_FIRE" to Enchantment.PROTECTION_FIRE,
                "PROTECTION_PROJECTILE" to Enchantment.PROTECTION_PROJECTILE,
                "QUICK_CHARGE" to Enchantment.QUICK_CHARGE,
                "RIPTIDE" to Enchantment.RIPTIDE,
                "SILK_TOUCH" to Enchantment.SILK_TOUCH,
                "SWEEPING_EDGE" to Enchantment.SWEEPING_EDGE,
                "THORNS" to Enchantment.THORNS,
                "VANISHING_CURSE" to Enchantment.VANISHING_CURSE,
                "WATER_WORKER" to Enchantment.WATER_WORKER
        )
    }
}