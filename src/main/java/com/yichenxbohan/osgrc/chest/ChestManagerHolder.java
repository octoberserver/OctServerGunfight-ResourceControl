package com.yichenxbohan.osgrc.chest;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局保存所有 ChestManager 實例
 */
public class ChestManagerHolder {
    private static final Map<String, ChestManager> managers = new HashMap<>();
    private static final Map<String, CustomLootTableManager> customLootManagers = new HashMap<>();

    /**
     * 獲取或創建指定 Level 的 ChestManager
     */
    public static ChestManager getManager(ServerLevel level) {
        String levelName = level.getServer().getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).toString();

        return managers.computeIfAbsent(levelName, k -> {
            ChestManager manager = new ChestManager(level);
            manager.loadFromFile();
            return manager;
        });
    }

    /**
     * 獲取或創建指定 Level 的 CustomLootTableManager
     */
    public static CustomLootTableManager getCustomLootManager(ServerLevel level) {
        String levelName = level.getServer().getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).toString();

        return customLootManagers.computeIfAbsent(levelName, k -> {
            CustomLootTableManager manager = new CustomLootTableManager(level);
            manager.loadFromFile();
            return manager;
        });
    }

    /**
     * 清空所有 manager 實例
     */
    public static void clear() {
        managers.clear();
        customLootManagers.clear();
    }

    /**
     * 保存所有 manager 的數據
     */
    public static void saveAll() {
        for (ChestManager manager : managers.values()) {
            manager.saveToFile();
        }
    }
}
