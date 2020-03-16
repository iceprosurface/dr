package com.iceprosurface.deathresurrection

import org.bukkit.Material
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

class Config {
    private val configFileList = HashMap<String, File>()
    private val configList = HashMap<String, FileConfiguration>()
    var needResource = HashMap<Material, Int>()
        private set

    private fun loadConfig() {
        createAndLoadConfig(CONFIG)
        createAndLoadConfig(BAN_LIST)
        val cfg = config
        val sec = cfg!!.getConfigurationSection("ResurrectionCosts.ResourceList")!!
        val obj = sec.getValues(false)
        val res = HashMap<String, Int>()
        obj.forEach { (s, i) -> res[s] = i.toString().toInt() }
        needResource = convertResourceList(res)
    }

    fun reload() {
        loadConfig()
    }

    val banListConfig: FileConfiguration?
        get() = configList[BAN_LIST]

    fun setBanListConfig() {
        try {
            configList[BAN_LIST]!!.save(configFileList[BAN_LIST]!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    val config: FileConfiguration?
        get() = configList[CONFIG]

    fun setConfig() {
        try {
            configList[CONFIG]!!.save(CONFIG)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun createAndLoadConfig(path: String) {
        val instance = DeathResurrection.instance
        val dataFolder = instance.dataFolder
        val configFile = File(dataFolder, path)
        configFileList[path] = configFile
        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
            instance.saveResource(path, false)
        }
        val config = YamlConfiguration()
        try {
            config.load(configFile)
            configList[path] = config
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidConfigurationException) {
            e.printStackTrace()
        }
    }

    companion object {
        var CONFIG = "config.yml"
        var BAN_LIST = "ban-list.yml"
        fun convertResourceList(map: HashMap<String, Int>): HashMap<Material, Int> {
            val total = HashMap<Material, Int>()
            map.forEach { (s, i) ->
                val m = Material.getMaterial(s)
                if (m != null) {
                    total[m] = i
                }
            }
            return total
        }
    }

    init {
        loadConfig()
    }
}