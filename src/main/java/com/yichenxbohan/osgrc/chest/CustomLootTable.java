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
     * 添加物品和权重
     */
    public void addItem(ItemStack itemStack, long weight) {
        if (weight <= 0) return;
        if (itemStack.isEmpty()) return;

        ItemStack copy = itemStack.copy();
        copy.setCount(1); // 确保只保存单个物品的信息
        items.add(new LootItem(copy, weight));
        totalWeight += weight;
    }

    /**
     * 从玩家背包读取物品（27格）
     * 根据物品所在的背包位置索引作为权重
     */
    public static CustomLootTable fromPlayerInventory(String name, net.minecraft.world.entity.player.Player player, Map<Integer, Long> weightMap) {
        CustomLootTable table = new CustomLootTable(name);

        // 读取背包的前27格（索引 0-26）
        net.minecraft.world.inventory.Inventory inv = player.getInventory();

        for (int i = 0; i < Math.min(27, inv.getContainerSize()); i++) {
            ItemStack itemStack = inv.getItem(i);
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

