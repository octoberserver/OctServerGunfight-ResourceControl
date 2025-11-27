package com.yichenxbohan.osgrc.chest;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 儲存單個箱子的數據
 */
public class ChestData {
    private final String name;
    private final BlockPos position;
    private final String lootTable;
    private final List<String> groups; // 新增：箱子所屬的組列表

    public ChestData(String name, BlockPos position, String lootTable) {
        this(name, position, lootTable, new ArrayList<>());
    }

    public ChestData(String name, BlockPos position, String lootTable, List<String> groups) {
        this.name = name;
        this.position = position;
        this.lootTable = lootTable;
        this.groups = new ArrayList<>(groups);
    }

    public String getName() {
        return name;
    }

    public BlockPos getPosition() {
        return position;
    }

    public String getLootTable() {
        return lootTable;
    }

    /**
     * 取得不可變的組列表
     */
    public List<String> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    /**
     * 檢查是否屬於某組
     */
    public boolean hasGroup(String group) {
        return groups.contains(group);
    }

    /**
     * 嘗試新增組（若已存在則不重複）
     */
    public boolean addGroup(String group) {
        if (group == null || group.isEmpty()) return false;
        if (groups.contains(group)) return false;
        groups.add(group);
        return true;
    }

    /**
     * 移除組
     */
    public boolean removeGroup(String group) {
        return groups.remove(group);
    }

    /**
     * 清空所有組
     */
    public void clearGroup() {
        groups.clear();
    }

    /**
     * 將數據序列化到 NBT
     */
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.putInt("x", position.getX());
        tag.putInt("y", position.getY());
        tag.putInt("z", position.getZ());
        tag.putString("lootTable", lootTable == null ? "" : lootTable);

        ListTag groupList = new ListTag();
        for (String g : groups) {
            groupList.add(StringTag.valueOf(g));
        }
        tag.put("groups", groupList);

        return tag;
    }

    /**
     * 從 NBT 反序列化數據
     */
    public static ChestData deserializeNBT(CompoundTag tag) {
        String name = tag.getString("name");
        BlockPos pos = new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"));
        String lootTable = tag.getString("lootTable");

        List<String> groups = new ArrayList<>();
        if (tag.contains("groups", Tag.TAG_LIST)) {
            ListTag list = tag.getList("groups", Tag.TAG_STRING);
            for (int i = 0; i < list.size(); i++) {
                groups.add(list.getString(i));
            }
        }

        return new ChestData(name, pos, lootTable, groups);
    }
}