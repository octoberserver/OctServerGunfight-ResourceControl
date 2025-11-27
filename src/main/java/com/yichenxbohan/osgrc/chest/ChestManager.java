package com.yichenxbohan.osgrc.chest;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 管理所有標記的箱子
 */
public class ChestManager {
    private static final String CHEST_FILE_NAME = "marked_chests.nbt";
    private final Map<String, ChestData> chests = new LinkedHashMap<>();
    private ServerLevel level;

    public ChestManager(ServerLevel level) {
        this.level = level;
    }

    /**
     * 新增箱子
     */
    public boolean addChest(String name, BlockPos pos, String lootTable) {
        if (chests.containsKey(name)) {
            return false; // 名稱已存在
        }

        // 驗證位置是否是箱子
        if (level.getBlockState(pos).getBlock() != Blocks.CHEST) {
            return false; // 該位置不是箱子
        }

        chests.put(name, new ChestData(name, pos, lootTable));
        saveToFile();
        return true;
    }

    /**
     * 刪除箱子
     */
    public boolean removeChest(String name) {
        boolean removed = chests.remove(name) != null;
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    /**
     * 獲取箱子數據
     */
    public ChestData getChest(String name) {
        return chests.get(name);
    }

    /**
     * 獲取所有箱子
     */
    public Collection<ChestData> getAllChests() {
        return Collections.unmodifiableCollection(chests.values());
    }

    /**
     * 檢查箱子是否存在
     */
    public boolean hasChest(String name) {
        return chests.containsKey(name);
    }

    /**
     * 清空箱子內容並套用 loot table
     */
    public boolean clearAndApplyLootTable(String name, long seed) {
        ChestData chestData = chests.get(name);
        if (chestData == null) {
            return false;
        }

        BlockPos pos = chestData.getPosition();

        // 驗證位置仍是箱子
        if (level.getBlockState(pos).getBlock() != Blocks.CHEST) {
            return false;
        }

        try {
            net.minecraft.world.level.block.entity.BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ChestBlockEntity chest) {
                // 清空箱子
                chest.clearContent();

                String lootTableStr = chestData.getLootTable();

                // 檢查是否是自定義 Loot Table
                if (lootTableStr != null && lootTableStr.startsWith("CUSTOM_LOOT:")) {
                    // 自定義 Loot Table
                    String customTableName = lootTableStr.substring("CUSTOM_LOOT:".length());
                    CustomLootTableManager customManager = ChestManagerHolder.getCustomLootManager(level);
                    CustomLootTable customTable = customManager.getCustomLootTable(customTableName);

                    if (customTable != null) {
                        // 從自定義 Loot Table 中隨機選擇物品填充箱子
                        java.util.Random random = new java.util.Random(seed);
                        for (int i = 0; i < chest.getContainerSize(); i++) {
                            net.minecraft.world.item.ItemStack item = customTable.getRandomItem(random);
                            if (!item.isEmpty()) {
                                chest.setItem(i, item);
                            }
                        }
                        chest.setChanged();
                        return true;
                    }
                } else if (lootTableStr != null && !lootTableStr.isEmpty()) {
                    // 套用原生 loot table
                    try {
                        net.minecraft.resources.ResourceLocation lootTableId =
                            net.minecraft.resources.ResourceLocation.parse(lootTableStr);
                        chest.setLootTable(lootTableId, seed);
                    } catch (Exception e) {
                        return false;
                    }
                }

                chest.setChanged();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 將數據保存到文件
     */
    public void saveToFile() {
        try {
            Path worldDir = level.getServer().getWorldPath(LevelResource.ROOT);
            Path chestFile = worldDir.resolve(CHEST_FILE_NAME);

            CompoundTag rootTag = new CompoundTag();
            ListTag chestList = new ListTag();

            for (ChestData chest : chests.values()) {
                chestList.add(chest.serializeNBT());
            }

            rootTag.put("chests", chestList);

            // 保存顯式組列表
            ListTag groupList = new ListTag();
            for (String group : explicitGroups) {
                groupList.add(net.minecraft.nbt.StringTag.valueOf(group));
            }
            rootTag.put("explicitGroups", groupList);

            // 創建臨時文件並寫入
            Path tempFile = chestFile.resolveSibling(CHEST_FILE_NAME + ".tmp");
            net.minecraft.nbt.NbtIo.writeCompressed(rootTag, tempFile.toFile());

            // 原子替換
            Files.move(tempFile, chestFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 從文件加載數據
     */
    public void loadFromFile() {
        try {
            Path worldDir = level.getServer().getWorldPath(LevelResource.ROOT);
            Path chestFile = worldDir.resolve(CHEST_FILE_NAME);

            if (!Files.exists(chestFile)) {
                return;
            }

            CompoundTag rootTag = net.minecraft.nbt.NbtIo.readCompressed(chestFile.toFile());
            ListTag chestList = rootTag.getList("chests", Tag.TAG_COMPOUND);

            chests.clear();
            for (int i = 0; i < chestList.size(); i++) {
                CompoundTag tag = chestList.getCompound(i);
                ChestData chest = ChestData.deserializeNBT(tag);
                chests.put(chest.getName(), chest);
            }

            // 加載顯式組列表
            explicitGroups.clear();
            if (rootTag.contains("explicitGroups", Tag.TAG_LIST)) {
                ListTag groupList = rootTag.getList("explicitGroups", Tag.TAG_STRING);
                for (int i = 0; i < groupList.size(); i++) {
                    explicitGroups.add(groupList.getString(i));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新 level 引用（服務器重啟時使用）
     */
    public void setLevel(ServerLevel level) {
        this.level = level;
    }

    public ServerLevel getLevel() {
        return level;
    }

    // ---------- 新增：組管理 APIs ----------

    /**
     * 獲取所有存在的組名稱
     */
    public List<String> getAllGroups() {
        Set<String> allGroups = new HashSet<>(explicitGroups);
        for (ChestData chest : chests.values()) {
            allGroups.addAll(chest.getGroups());
        }
        return new ArrayList<>(allGroups);
    }

    /**
     * 創建新組（通過標記記錄組的存在）
     * 注意：組是通過箱子關聯自動創建的，此方法用於顯式創建空組
     */
    private final Set<String> explicitGroups = new HashSet<>();

    public boolean createGroup(String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) {
            return false;
        }
        boolean added = explicitGroups.add(groupName.trim());
        if (added) {
            saveToFile();
        }
        return added;
    }

    /**
     * 刪除整個組（包括從所有箱子中移除該組的關聯）
     */
    public boolean deleteGroup(String groupName) {
        boolean found = false;

        // 從所有箱子中移除該組
        for (ChestData chest : chests.values()) {
            if (chest.removeGroup(groupName)) {
                found = true;
            }
        }

        // 從顯式組集合中移除
        if (explicitGroups.remove(groupName)) {
            found = true;
        }

        if (found) {
            saveToFile();
        }
        return found;
    }

    /**
     * 清空組內所有箱子並重新套用 Loot Table
     * @param groupName 組名稱
     * @param seed 隨機種子
     * @return 成功清空的箱子數量
     */
    public int clearGroupChests(String groupName, long seed) {
        List<ChestData> groupChests = getChestsByGroup(groupName);
        if (groupChests.isEmpty()) {
            return 0;
        }

        int successCount = 0;
        for (ChestData chestData : groupChests) {
            if (clearAndApplyLootTable(chestData.getName(), seed)) {
                successCount++;
            }
        }

        return successCount;
    }

    /**
     * 檢查組是否存在（包括隱式和顯式創建的組）
     */
    public boolean groupExists(String groupName) {
        if (explicitGroups.contains(groupName)) {
            return true;
        }
        // 檢查是否有箱子屬於此組
        for (ChestData chest : chests.values()) {
            if (chest.hasGroup(groupName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 將指定箱子加入到組
     */
    public boolean addChestToGroup(String chestName, String group) {
        ChestData chest = chests.get(chestName);
        if (chest == null) return false;
        boolean added = chest.addGroup(group);
        if (added) {
            // 自動將組加入顯式組集合
            explicitGroups.add(group);
            saveToFile();
        }
        return added;
    }

    /**
     * 從組中移除指定箱子
     */
    public boolean removeChestFromGroup(String chestName, String group) {
        ChestData chest = chests.get(chestName);
        if (chest == null) return false;
        boolean removed = chest.removeGroup(group);
        if (removed) saveToFile();
        return removed;
    }

    /**
     * 清空組
     */
    public boolean clearGroup(String group) {
        boolean result = false;
        for (ChestData chest : chests.values()) {
            if (chest.hasGroup(group)) {
                chest.clearGroup();
                result = true;
            }
        }
        if (result) saveToFile();
        return result;
    }

    /**
     * 獲取指定組的所有箱子
     */
    public List<ChestData> getChestsByGroup(String group) {
        List<ChestData> result = new ArrayList<>();
        for (ChestData chest : chests.values()) {
            if (chest.hasGroup(group)) {
                result.add(chest);
            }
        }
        return result;
    }
}
