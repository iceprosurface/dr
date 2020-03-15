package com.iceprosurface.deathresurrection;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Config {
    public static String CONFIG = "config.yml";
    public static String BAN_LIST = "ban-list.yml";
    private HashMap<String, File> configFileList = new HashMap<>();
    private HashMap<String, FileConfiguration> configList = new HashMap<>();

    public Config() {
        createAndLoadConfig(CONFIG);
        createAndLoadConfig(BAN_LIST);
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
