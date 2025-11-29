package com.yichenxbohan.osgrc.chest.gui;

import com.yichenxbohan.osgrc.chest.CustomLootTable;
import com.yichenxbohan.osgrc.chest.CustomLootTableManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 自定義的 Loot Table 編輯器容器
 */
public class LootTableEditorContainer extends AbstractContainerMenu {
    private final CustomLootTableManager manager;
    private final String lootTableName;
    private final ServerPlayer player;
    private final int page;
    private final SimpleContainer container;
    private static final int ITEMS_PER_PAGE = 21;

    public LootTableEditorContainer(int containerId, Inventory playerInventory,
                                    CustomLootTableManager manager, String lootTableName,
                                    ServerPlayer player, int page) {
        super(MenuType.GENERIC_9x6, containerId);
        this.manager = manager;
        this.lootTableName = lootTableName;
        this.player = player;
        this.page = page;
        this.container = new SimpleContainer(54);

        // 填充容器內容
        fillContainer();

        // 添加槽位（但是設置為不可交互）
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                int index = row * 9 + col;
                this.addSlot(new Slot(container, index, 8 + col * 18, 18 + row * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return false; // 禁止放置物品
                    }

                    @Override
                    public boolean mayPickup(@NotNull Player player) {
                        return false; // 禁止拿取物品
                    }
                });
            }
        }
    }

    private void fillContainer() {
        CustomLootTable lootTable = manager.getLootTable(lootTableName);
        if (lootTable == null) return;

        // 清空容器
        container.clearContent();

        List<CustomLootTable.LootItem> items = lootTable.getItems();
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, items.size());

        // 填充物品（前3行）
        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            if (slot >= ITEMS_PER_PAGE) break;

            CustomLootTable.LootItem lootItem = items.get(i);
            ItemStack displayItem = lootItem.itemStack.copy();

            // 添加 Lore
            net.minecraft.nbt.CompoundTag displayTag = displayItem.getOrCreateTagElement("display");
            net.minecraft.nbt.ListTag loreTag = new net.minecraft.nbt.ListTag();

            double probability = lootTable.getTotalWeight() > 0
                ? (lootItem.weight * 100.0 / lootTable.getTotalWeight())
                : 0;

            loreTag.add(net.minecraft.nbt.StringTag.valueOf(
                Component.Serializer.toJson(Component.literal("§7索引: §e" + (i + 1)))
            ));
            loreTag.add(net.minecraft.nbt.StringTag.valueOf(
                Component.Serializer.toJson(Component.literal("§7權重: §f" + lootItem.weight))
            ));
            loreTag.add(net.minecraft.nbt.StringTag.valueOf(
                Component.Serializer.toJson(Component.literal("§7概率: §a" + String.format("%.2f%%", probability)))
            ));
            loreTag.add(net.minecraft.nbt.StringTag.valueOf(
                Component.Serializer.toJson(Component.literal(""))
            ));
            loreTag.add(net.minecraft.nbt.StringTag.valueOf(
                Component.Serializer.toJson(Component.literal("§eShift+左鍵 §7刪除"))
            ));
            loreTag.add(net.minecraft.nbt.StringTag.valueOf(
                Component.Serializer.toJson(Component.literal("§e右鍵 §7調整權重"))
            ));

            displayTag.put("Lore", loreTag);

            int row = slot / 7;
            int col = slot % 7;
            int actualSlot = row * 9 + col;

            container.setItem(actualSlot, displayItem);
            slot++;
        }

        // 添加控制按鈕（第4行）
        if (page > 0) {
            ItemStack prevButton = new ItemStack(Items.ARROW);
            prevButton.setHoverName(Component.literal("§a上一頁"));
            container.setItem(36, prevButton);
        }

        // 信息按鈕
        ItemStack infoItem = new ItemStack(Items.BOOK);
        infoItem.setHoverName(Component.literal("§6Loot Table 信息"));
        container.setItem(40, infoItem);

        // 下一頁
        int totalPages = (int) Math.ceil((double) items.size() / ITEMS_PER_PAGE);
        if (page < totalPages - 1) {
            ItemStack nextButton = new ItemStack(Items.ARROW);
            nextButton.setHoverName(Component.literal("§a下一頁"));
            container.setItem(44, nextButton);
        }

        // 功能按鈕（第5行）
        ItemStack addButton = new ItemStack(Items.EMERALD);
        addButton.setHoverName(Component.literal("§a添加手持物品"));
        container.setItem(45, addButton);

        ItemStack slotsButton = new ItemStack(Items.CHEST);
        slotsButton.setHoverName(Component.literal("§e設置填充格數"));
        container.setItem(49, slotsButton);

        ItemStack closeButton = new ItemStack(Items.BARRIER);
        closeButton.setHoverName(Component.literal("§c關閉"));
        container.setItem(53, closeButton);
    }

    @Override
    public void clicked(int slotId, int button, @NotNull ClickType clickType, @NotNull Player player) {
        // 阻止默認行為
        if (slotId < 0 || slotId >= 54) {
            return;
        }

        // 處理點擊
        handleClick(slotId, button, clickType);
    }

    private void handleClick(int slot, int button, ClickType clickType) {
        CustomLootTable lootTable = manager.getLootTable(lootTableName);
        if (lootTable == null) return;

        // 上一頁
        if (slot == 36 && page > 0) {
            player.closeContainer();
            LootTableEditorHandler.openEditor(player, lootTableName, manager, page - 1);
            return;
        }

        // 信息按鈕 (slot 40)
        if (slot == 40) {
            player.sendSystemMessage(Component.literal("§6=== Loot Table 信息 ==="));
            player.sendSystemMessage(Component.literal("§e名稱: §f" + lootTableName));
            player.sendSystemMessage(Component.literal("§e物品總數: §f" + lootTable.getItems().size()));
            player.sendSystemMessage(Component.literal("§e總權重: §f" + lootTable.getTotalWeight()));
            player.sendSystemMessage(Component.literal("§e填充範圍: §f" + lootTable.getMinSlots() + "-" + lootTable.getMaxSlots() + " 格"));
            return;
        }

        // 下一頁
        if (slot == 44) {
            int totalPages = (int) Math.ceil((double) lootTable.getItems().size() / 21.0);
            if (page < totalPages - 1) {
                player.closeContainer();
                LootTableEditorHandler.openEditor(player, lootTableName, manager, page + 1);
            }
            return;
        }

        // 添加物品
        if (slot == 45) {
            ItemStack heldItem = player.getMainHandItem();
            if (!heldItem.isEmpty()) {
                lootTable.addItem(heldItem, 100);
                manager.saveLootTables();
                player.sendSystemMessage(Component.literal("§a已添加物品: §f" + heldItem.getHoverName().getString()));
                player.closeContainer();
                LootTableEditorHandler.openEditor(player, lootTableName, manager, page);
            } else {
                player.sendSystemMessage(Component.literal("§c請先手持要添加的物品"));
            }
            return;
        }

        // 設置格數
        if (slot == 49) {
            player.closeContainer();
            player.sendSystemMessage(Component.literal("§e請使用指令設置格數："));
            player.sendSystemMessage(Component.literal("§7/customloot slots " + lootTableName + " <最小格數> <最大格數>"));
            return;
        }

        // 關閉
        if (slot == 53) {
            player.closeContainer();
            player.sendSystemMessage(Component.literal("§aLoot Table 編輯完成"));
            return;
        }

        // 處理物品槽位（前3行）
        int row = slot / 9;
        int col = slot % 9;
        if (row < 3 && col < 7) {
            int itemIndex = (page * 21) + (row * 7 + col);
            if (itemIndex < lootTable.getItems().size()) {
                if (clickType == ClickType.QUICK_MOVE) {
                    // Shift+左鍵：刪除
                    CustomLootTable.LootItem item = lootTable.getItems().get(itemIndex);
                    lootTable.removeItem(itemIndex);
                    manager.saveLootTables();
                    player.sendSystemMessage(Component.literal("§c已刪除物品: §f" + item.itemStack.getHoverName().getString()));
                    player.closeContainer();
                    LootTableEditorHandler.openEditor(player, lootTableName, manager, page);
                } else if (button == 1) {
                    // 右鍵點擊 (button == 1 表示右鍵)
                    player.closeContainer();
                    player.sendSystemMessage(Component.literal("§e請使用指令調整權重："));
                    player.sendSystemMessage(Component.literal("§7/customloot setweight " + lootTableName + " " + (itemIndex + 1) + " <新權重>"));
                } else if (button == 0) {
                    // 左鍵點擊：顯示詳細信息
                    CustomLootTable.LootItem item = lootTable.getItems().get(itemIndex);
                    double probability = lootTable.getTotalWeight() > 0
                        ? (item.weight * 100.0 / lootTable.getTotalWeight())
                        : 0;

                    player.sendSystemMessage(Component.literal("§6=== 物品信息 ==="));
                    player.sendSystemMessage(Component.literal("§e索引: §f" + (itemIndex + 1)));
                    player.sendSystemMessage(Component.literal("§e物品: §f" + item.itemStack.getHoverName().getString()));
                    player.sendSystemMessage(Component.literal("§e數量: §fx" + item.itemStack.getCount()));
                    player.sendSystemMessage(Component.literal("§e權重: §f" + item.weight));
                    player.sendSystemMessage(Component.literal("§e概率: §a" + String.format("%.2f%%", probability)));
                }
            }
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY; // 禁止快速移動
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
