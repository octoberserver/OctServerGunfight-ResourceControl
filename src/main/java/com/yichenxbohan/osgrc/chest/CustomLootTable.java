package com.yichenxbohan.osgrc.chest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * 自定义 Loot Table - 支持从玩家背包创建并设置物品权重
 */
public class CustomLootTable {
    private final String name;
    private final List<LootItem> items = new ArrayList<>();
    private long totalWeight = 0;
    private int minSlots = 1;      // 最小填充格數，預設為1
    private int maxSlots = 27;     // 最大填充格數，預設為27（滿格）

    public static class LootItem {
        public ItemStack itemStack;
        public long weight;

        public LootItem(ItemStack itemStack, long weight) {
            this.itemStack = itemStack;
            this.weight = weight;
        }

        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.put("item", itemStack.serializeNBT());
            tag.putLong("weight", weight);
            return tag;
        }

        public static LootItem deserializeNBT(CompoundTag tag) {
            ItemStack item = ItemStack.of(tag.getCompound("item"));
            long weight = tag.getLong("weight");
            return new LootItem(item, weight);
        }
    }

    public CustomLootTable(String name) {
        this.name = name;
    }

    /**
     * 設置最小填充格數
     */
    public void setMinSlots(int minSlots) {
        this.minSlots = Math.max(1, minSlots);
    }

    /**
     * 設置最大填充格數
     */
    public void setMaxSlots(int maxSlots) {
        this.maxSlots = Math.min(27, Math.max(this.minSlots, maxSlots));
    }

    /**
     * 獲取最小填充格數
     */
    public int getMinSlots() {
        return minSlots;
    }

    /**
     * 獲取最大填充格數
     */
    public int getMaxSlots() {
        return maxSlots;
    }

    /**
     * 添加物品和权重
     */
    public void addItem(ItemStack itemStack, long weight) {
        if (weight <= 0) return;
        if (itemStack.isEmpty()) return;

        ItemStack copy = itemStack.copy();
        // 保留物品的數量信息
        items.add(new LootItem(copy, weight));
        totalWeight += weight;
    }

    /**
     * 移除指定索引的物品
     */
    public boolean removeItem(int index) {
        if (index < 0 || index >= items.size()) {
            return false;
        }
        LootItem removed = items.remove(index);
        totalWeight -= removed.weight;
        return true;
    }

    /**
     * 清空所有物品
     */
    public void clearItems() {
        items.clear();
        totalWeight = 0;
    }

    /**
     * 更新物品權重
     */
    public boolean updateItemWeight(int index, long newWeight) {
        if (index < 0 || index >= items.size() || newWeight <= 0) {
            return false;
        }
        LootItem item = items.get(index);
        totalWeight -= item.weight;
        item.weight = newWeight;
        totalWeight += newWeight;
        return true;
    }

    /**
     * 獲取物品數量
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * 創建空的 Loot Table
     */
    public static CustomLootTable emptyTable(String name) {
        return new CustomLootTable(name);
    }

    /**
     * 从玩家背包读取物品（27格）
     * 根据物品所在的背包位置索引作为权重
     */
    public static CustomLootTable fromPlayerInventory(String name, net.minecraft.world.entity.player.Player player, Map<Integer, Long> weightMap) {
        CustomLootTable table = new CustomLootTable(name);

        // 读取背包的前27格（索引 0-26）
        // 玩家背包的前27格是主背包（inventory），不包括快捷栏
        for (int i = 0; i < 27; i++) {
            ItemStack itemStack = player.containerMenu.getSlot(i).getItem();
            if (!itemStack.isEmpty()) {
                // 使用指定的权重，如果没有则使用默认权重1
                long weight = weightMap.getOrDefault(i, 1L);
                table.addItem(itemStack, weight);
            }
        }

        return table;
    }

    /**
     * 随机选择一个物品
     */
    public ItemStack getRandomItem(Random random) {
        if (items.isEmpty() || totalWeight <= 0) {
            return ItemStack.EMPTY;
        }

        long roll = Math.abs(random.nextLong()) % totalWeight;
        long current = 0;

        for (LootItem item : items) {
            current += item.weight;
            if (roll < current) {
                return item.itemStack.copy();
            }
        }

        // 后备方案
        return items.get(items.size() - 1).itemStack.copy();
    }

    /**
     * 获取所有物品
     */
    public List<LootItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * 获取总权重
     */
    public long getTotalWeight() {
        return totalWeight;
    }

    /**
     * 获取表名
     */
    public String getName() {
        return name;
    }

    /**
     * 序列化到 NBT
     */
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name);
        tag.putLong("totalWeight", totalWeight);
        tag.putInt("minSlots", minSlots);
        tag.putInt("maxSlots", maxSlots);

        ListTag itemList = new ListTag();
        for (LootItem item : items) {
            itemList.add(item.serializeNBT());
        }
        tag.put("items", itemList);

        return tag;
    }

    /**
     * 从 NBT 反序列化
     */
    public static CustomLootTable deserializeNBT(CompoundTag tag) {
        CustomLootTable table = new CustomLootTable(tag.getString("name"));
        table.totalWeight = tag.getLong("totalWeight");
        table.minSlots = tag.getInt("minSlots");
        table.maxSlots = tag.getInt("maxSlots");

        ListTag itemList = tag.getList("items", Tag.TAG_COMPOUND);
        for (int i = 0; i < itemList.size(); i++) {
            LootItem item = LootItem.deserializeNBT(itemList.getCompound(i));
            table.items.add(item);
        }

        return table;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("§e[自定义 Loot Table] ").append(name).append("\n");
        sb.append("§6总权重: ").append(totalWeight).append("\n");
        sb.append("§6填充格數: ").append(minSlots).append(" - ").append(maxSlots).append("\n");
        for (int i = 0; i < items.size(); i++) {
            LootItem item = items.get(i);
            sb.append("§6  ").append(i + 1).append(". ");
            sb.append(item.itemStack.getHoverName().getString());
            sb.append(" §7x").append(item.itemStack.getCount());
            sb.append(" §8(权重: ").append(item.weight).append(")\n");
        }
        return sb.toString();
    }
}
