package com.yichenxbohan.osgrc.chest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 管理自定义 Loot Table
 */
public class CustomLootTableManager {
    private static final String CUSTOM_LOOT_FILE_NAME = "custom_loot_tables.nbt";
    private final Map<String, CustomLootTable> lootTables = new LinkedHashMap<>();
    private ServerLevel level;

    public CustomLootTableManager(ServerLevel level) {
        this.level = level;
    }

    /**
     * 创建自定义 Loot Table
     */
    public boolean createCustomLootTable(String name, CustomLootTable table) {
        if (lootTables.containsKey(name)) {
            return false; // 名称已存在
        }

        lootTables.put(name, table);
        saveToFile();
        return true;
    }

    /**
     * 获取 Loot Table
     */
    public CustomLootTable getCustomLootTable(String name) {
        return lootTables.get(name);
    }

    /**
     * 删除 Loot Table
     */
    public boolean deleteCustomLootTable(String name) {
        boolean removed = lootTables.remove(name) != null;
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    /**
     * 获取所有 Loot Table
     */
    public Collection<CustomLootTable> getAllLootTables() {
        return Collections.unmodifiableCollection(lootTables.values());
    }

    /**
     * 检查 Loot Table 是否存在
     */
    public boolean hasLootTable(String name) {
        return lootTables.containsKey(name);
    }

    /**
     * 列出所有 Loot Table
     */
    public List<String> listLootTableNames() {
        return new ArrayList<>(lootTables.keySet());
    }

    /**
     * 獲取 Loot Table（別名方法，與 getCustomLootTable 相同）
     */
    public CustomLootTable getLootTable(String name) {
        return lootTables.get(name);
    }

    /**
     * 獲取所有 Loot Table 名稱（別名方法，與 listLootTableNames 相同）
     */
    public Set<String> getAllLootTableNames() {
        return lootTables.keySet();
    }

    /**
     * 保存所有 Loot Tables 到文件
     */
    public void saveLootTables() {
        saveToFile();
    }

    /**
     * 保存到文件
     */
    private void saveToFile() {
        try {
            Path worldDir = level.getServer().getWorldPath(LevelResource.ROOT);
            Path filePath = worldDir.resolve(CUSTOM_LOOT_FILE_NAME);

            ListTag allTables = new ListTag();
            for (CustomLootTable table : lootTables.values()) {
                allTables.add(table.serializeNBT());
            }

            CompoundTag rootTag = new CompoundTag();
            rootTag.put("lootTables", allTables);

            // 确保文件父目录存在
            Files.createDirectories(filePath.getParent());

            // 写入文件
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            net.minecraft.nbt.NbtIo.writeCompressed(rootTag, baos);
            Files.write(filePath, baos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件加载
     */
    public void loadFromFile() {
        try {
            Path worldDir = level.getServer().getWorldPath(LevelResource.ROOT);
            Path filePath = worldDir.resolve(CUSTOM_LOOT_FILE_NAME);

            if (!Files.exists(filePath)) {
                return; // 文件不存在，跳过
            }

            byte[] data = Files.readAllBytes(filePath);
            CompoundTag rootTag = net.minecraft.nbt.NbtIo.readCompressed(new ByteArrayInputStream(data));

            ListTag allTables = rootTag.getList("lootTables", Tag.TAG_COMPOUND);
            for (int i = 0; i < allTables.size(); i++) {
                CustomLootTable table = CustomLootTable.deserializeNBT(allTables.getCompound(i));
                lootTables.put(table.getName(), table);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空所有数据
     */
    public void clear() {
        lootTables.clear();
        saveToFile();
    }
}
