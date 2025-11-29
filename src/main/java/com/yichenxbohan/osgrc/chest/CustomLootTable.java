package com.yichenxbohan.osgrc.chest;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
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
     * @param index 用戶輸入的索引（從 1 開始）
     */
    public boolean removeItem(int index) {
        // 將用戶輸入的索引（1-based）轉換為實際的列表索引（0-based）
        int actualIndex = index - 1;
        if (actualIndex < 0 || actualIndex >= items.size()) {
            return false;
        }
        LootItem removed = items.remove(actualIndex);
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
     * @param index 用戶輸入的索引（從 1 開始）
     */
    public boolean updateItemWeight(int index, long newWeight) {
        // 將用戶輸入的索引（1-based）轉換為實際的列表索引（0-based）
        int actualIndex = index - 1;
        if (actualIndex < 0 || actualIndex >= items.size() || newWeight <= 0) {
            return false;
        }
        LootItem item = items.get(actualIndex);
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
        sb.append("§6物品数量: ").append(items.size()).append("\n");
        sb.append("§7§m                                        §r\n");

        for (int i = 0; i < items.size(); i++) {
            LootItem item = items.get(i);
            double probability = totalWeight > 0 ? (item.weight * 100.0 / totalWeight) : 0;

            sb.append("§6  [").append(i + 1).append("] ");
            sb.append("§f").append(item.itemStack.getHoverName().getString());
            sb.append(" §7x").append(item.itemStack.getCount());
            sb.append("\n");
            sb.append("§8      权重: ").append(item.weight);
            sb.append(" §8| 概率: §a").append(String.format("%.2f", probability)).append("%\n");
        }

        return sb.toString();
    }

    /**
     * 獲取格式化的聊天組件列表（支援更豐富的顯示）
     */
    public java.util.List<net.minecraft.network.chat.Component> toFormattedComponents() {
        java.util.List<net.minecraft.network.chat.Component> components = new java.util.ArrayList<>();

        // 標題
        components.add(Component.literal("§e§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        components.add(Component.literal("§e§l  自定義 Loot Table: §f" + name));
        components.add(Component.literal("§e§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        // 基本信息
        components.add(Component.literal(""));
        components.add(Component.literal("§6► 基本信息"));
        components.add(Component.literal("  §7物品數量: §f" + items.size() + " 種"));
        components.add(Component.literal("  §7總權重: §f" + totalWeight));
        components.add(Component.literal("  §7填充格數: §f" + minSlots + " §7至 §f" + maxSlots + " §7格"));

        // 物品列表
        components.add(Component.literal(""));
        components.add(Component.literal("§6► 物品列表 §7(索引 | 物品 | 權重 | 概率)"));
        components.add(Component.literal(""));

        for (int i = 0; i < items.size(); i++) {
            LootItem item = items.get(i);
            double probability = totalWeight > 0 ? (item.weight * 100.0 / totalWeight) : 0;

            // 使用不同顏色標示稀有度
            String rarityColor = getRarityColor(probability);
            String indexStr = String.format("§e[%3d]", i + 1);

            // 使用 final 變量
            final int finalIndex = i;

            net.minecraft.network.chat.MutableComponent line = Component.literal(indexStr + " ")
                .append(Component.literal("§f" + item.itemStack.getHoverName().getString())
                    .withStyle(style -> style
                        .withHoverEvent(
                            new net.minecraft.network.chat.HoverEvent(
                                net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                Component.literal("§7索引: §e" + (finalIndex + 1) + "\n" +
                                                "§7物品: §f" + item.itemStack.getHoverName().getString() + "\n" +
                                                "§7數量: §fx" + item.itemStack.getCount() + "\n" +
                                                "§7權重: §f" + item.weight + "\n" +
                                                "§7概率: " + rarityColor + String.format("%.2f%%", probability) + "\n" +
                                                "§8\n" +
                                                "§8點擊複製索引號")
                            ))
                        .withClickEvent(
                            new net.minecraft.network.chat.ClickEvent(
                                net.minecraft.network.chat.ClickEvent.Action.SUGGEST_COMMAND,
                                String.valueOf(finalIndex + 1)
                            ))))
                .append(Component.literal(" §7x" + item.itemStack.getCount()))
                .append(Component.literal("\n    §8└ 權重: §f" + item.weight + " §8| 概率: " + rarityColor + String.format("%.2f%%", probability)));

            components.add(line);
        }

        components.add(Component.literal(""));
        components.add(Component.literal("§e§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        components.add(Component.literal("§7提示: 將鼠標懸停在物品上查看詳情，點擊可複製索引號"));

        return components;
    }

    /**
     * 根據概率返回對應的顏色代碼
     */
    private String getRarityColor(double probability) {
        if (probability >= 30.0) return "§a";      // 綠色 - 常見
        if (probability >= 15.0) return "§e";      // 黃色 - 普通
        if (probability >= 5.0) return "§6";       // 金色 - 稀有
        if (probability >= 1.0) return "§c";       // 紅色 - 非常稀有
        return "§5";                               // 紫色 - 超稀有
    }

    /**
     * 獲取分頁顯示的組件列表
     */
    public java.util.List<net.minecraft.network.chat.Component> toPagedComponents(int page, int itemsPerPage) {
        java.util.List<net.minecraft.network.chat.Component> components = new java.util.ArrayList<>();

        int totalPages = (int) Math.ceil((double) items.size() / itemsPerPage);
        page = Math.max(1, Math.min(page, totalPages));

        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, items.size());

        // 使用 final 變量
        final int finalPage = page;
        final String finalName = this.name;

        // 標題
        components.add(Component.literal("§e§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));
        components.add(Component.literal("§e§l  " + name + " §7(頁 " + page + "/" + totalPages + ")"));
        components.add(Component.literal("§e§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        // 基本信息
        components.add(Component.literal("§7總共 §f" + items.size() + " §7種物品 | 總權重: §f" + totalWeight +
                                       " §7| 填充: §f" + minSlots + "-" + maxSlots + " §7格"));
        components.add(Component.literal(""));

        // 當前頁的物品
        for (int i = startIndex; i < endIndex; i++) {
            LootItem item = items.get(i);
            double probability = totalWeight > 0 ? (item.weight * 100.0 / totalWeight) : 0;
            String rarityColor = getRarityColor(probability);

            components.add(Component.literal(String.format("§e[%3d] §f%s §7x%d",
                i + 1,
                item.itemStack.getHoverName().getString(),
                item.itemStack.getCount()))
                .append(Component.literal("\n    §8└ 權重: §f" + item.weight + " §8| 概率: " +
                                        rarityColor + String.format("%.2f%%", probability))));
        }

        // 分頁控制
        if (totalPages > 1) {
            components.add(Component.literal(""));
            net.minecraft.network.chat.MutableComponent pageControl = Component.literal("");

            if (page > 1) {
                pageControl.append(Component.literal("§a[上一頁]")
                    .withStyle(style -> style
                        .withClickEvent(
                            new net.minecraft.network.chat.ClickEvent(
                                net.minecraft.network.chat.ClickEvent.Action.RUN_COMMAND,
                                "/customloot info " + finalName + " " + (finalPage - 1)
                            ))
                        .withHoverEvent(
                            new net.minecraft.network.chat.HoverEvent(
                                net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                Component.literal("§7點擊查看上一頁")
                            ))));
            } else {
                pageControl.append(Component.literal("§8[上一頁]"));
            }

            pageControl.append(Component.literal("  §7" + page + "/" + totalPages + "  "));

            if (page < totalPages) {
                pageControl.append(Component.literal("§a[下一頁]")
                    .withStyle(style -> style
                        .withClickEvent(
                            new net.minecraft.network.chat.ClickEvent(
                                net.minecraft.network.chat.ClickEvent.Action.RUN_COMMAND,
                                "/customloot info " + finalName + " " + (finalPage + 1)
                            ))
                        .withHoverEvent(
                            new net.minecraft.network.chat.HoverEvent(
                                net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                Component.literal("§7點擊查看下一頁")
                            ))));
            } else {
                pageControl.append(Component.literal("§8[下一頁]"));
            }

            components.add(pageControl);
        }

        components.add(Component.literal("§e§l━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"));

        return components;
    }
}
