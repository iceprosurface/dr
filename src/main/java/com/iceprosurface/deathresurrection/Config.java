package com.iceprosurface.deathresurrection;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static String CONFIG = "config.yml";
    public static String BAN_LIST = "ban-list.yml";
    private HashMap<String, File> configFileList = new HashMap<>();
    private HashMap<String, FileConfiguration> configList = new HashMap<>();
    private HashMap<Material, Integer> resourceList = new HashMap<>();
    public Config() {
        loadConfig();
    }
    private void loadConfig () {
        createAndLoadConfig(CONFIG);
        createAndLoadConfig(BAN_LIST);
        FileConfiguration cfg = getConfig();
        ConfigurationSection sec = cfg.getConfigurationSection("ResurrectionCosts.ResourceList");
        Map<String, Object> obj = sec.getValues(false);
        HashMap<String, Integer> res = new HashMap<>();
        obj.forEach((s, i) -> res.put(s, Integer.parseInt(i.toString())));
        resourceList = convertResourceList(res);
    }
    public HashMap<Material, Integer> getNeedResource() {
        return this. resourceList;
    }
    public static HashMap<Material, Integer> convertResourceList (HashMap<String, Integer> map) {
        HashMap<Material, Integer> total = new HashMap<>();
        map.forEach((s, i) -> {
            Material m = Material.getMaterial(s);
            if (m != null) {
                total.put(m, i);
            }
        });
        return total;
    }

    public void reload() {
        loadConfig();
    }

    public FileConfiguration getBanListConfig() {
        return configList.get(BAN_LIST);
    }


    public void setBanListConfig() {
        try {
            configList.get(BAN_LIST).save(configFileList.get(BAN_LIST));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return configList.get(CONFIG);
    }
    public void setConfig() {
        try {
            configList.get(CONFIG).save(configFileList.get(CONFIG));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createAndLoadConfig(String path) {
        DeathResurrection instance = DeathResurrection.getInstance();
        File dataFolder = instance.getDataFolder();
        File configFile = new File(dataFolder, path);
        configFileList.put(path, configFile);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            instance.saveResource(path, false);
        }
        YamlConfiguration config = new YamlConfiguration();

        try {
            config.load(configFile);
            configList.put(path, config);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
