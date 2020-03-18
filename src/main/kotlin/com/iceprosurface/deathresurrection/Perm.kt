package com.iceprosurface.deathresurrection

import org.bukkit.Bukkit
import org.bukkit.permissions.Permission

// TODO: 权限系统
class Perm {
    constructor() {
        val pm = Bukkit.getPluginManager()
        Commanders.COMMANDS.forEach {
            val permStr = "${PREFIX}.${it}"
            val permission = Permission(permStr)
            pm.addPermission(permission)
        }
    }
    companion object {
        private const val PREFIX = "rd"
    }
}