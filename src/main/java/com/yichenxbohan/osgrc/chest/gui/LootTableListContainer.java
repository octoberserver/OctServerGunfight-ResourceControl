package com.yichenxbohan.osgrc.chest.gui;

import com.yichenxbohan.osgrc.chest.CustomLootTable;
import com.yichenxbohan.osgrc.chest.CustomLootTableManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Loot Table 列表容器
 */
public class LootTableListContainer extends AbstractContainerMenu {
    private final CustomLootTableManager manager;
    private final ServerPlayer player;
    private final int page;
    private final SimpleContainer container;
    private static final int ITEMS_PER_PAGE = 45;

    public LootTableListContainer(int containerId, Inventory playerInventory,
                                  CustomLootTableManager manager, ServerPlayer player, int page) {
        super(MenuType.GENERIC_9x6, containerId);
        this.manager = manager;
        this.player = player;
        this.page = page;
        this.container = new SimpleContainer(54);

        // 填充容器
        fillContainer();

        // 添加槽位（禁止交互）
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                int index = row * 9 + col;
                this.addSlot(new Slot(container, index, 8 + col * 18, 18 + row * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return false;
                    }

                    @Override
                    public boolean mayPickup(@NotNull Player player) {
                        return false;
                    }
                });
            }
        }
    }

    private void fillContainer() {
        container.clearContent();

        List<String> lootTableNames = new ArrayList<>(manager.getAllLootTableNames());
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, lootTableNames.size());

        // 顯示 Loot Tables
        for (int i = startIndex; i < endIndex; i++) {
            String name = lootTableNames.get(i);
            CustomLootTable lootTable = manager.getLootTable(name);
            if (lootTable == null) continue;

            ItemStack displayItem = new ItemStack(Items.CHEST);
            displayItem.setHoverName(Component.literal("§e" + name));

            // 添加 Lore
            net.minecraft.nbt.CompoundTag displayTag = displayItem.getOrCreateTagElement("display");
            net.minecraft.nbt.ListTag loreTag = new net.minecraft.nbt.ListTag();

            loreTag.add(net.minecraft.nbt.StringTag.valueOf(
                Component.Serializer.toJson(Component.literal("§7物品數量: §f" + lootTable.getItems().size()))
            ));
            loreTag.add(net.minecraft.nbt.StringTag.valueOf(
                Component.Serializer.toJson(Component.literal("§7總權重: §f" + lootTable.getTotalWeight()))
            ));
            loreTag.add(net.minecraft.nbt.StringTag.valueOf(
                Component.Serializer.toJson(Component.literal("§7填充範圍: §f" + lootTable.getMinSlots() + "-" + lootTable.getMaxSlots() + " 格"))
            ));
            loreTag.add(net.minecraft.nbt.StringTag.valueOf(
                Component.Serializer.toJson(Component.literal(""))
            ));
            loreTag.add(net.minecraft.nbt.StringTag.valueOf(
                Component.Serializer.toJson(Component.literal("§e左鍵 §7編輯"))
            ));
            loreTag.add(net.minecraft.nbt.StringTag.valueOf(
                Component.Serializer.toJson(Component.literal("§eShift+左鍵 §7刪除"))
            ));

            displayTag.put("Lore", loreTag);

            int slot = i - startIndex;
            container.setItem(slot, displayItem);
        }

        // 控制按鈕
        if (page > 0) {
            ItemStack prevButton = new ItemStack(Items.ARROW);
            prevButton.setHoverName(Component.literal("§a上一頁"));
            container.setItem(45, prevButton);
        }

        ItemStack newButton = new ItemStack(Items.EMERALD);
        newButton.setHoverName(Component.literal("§a創建新的 Loot Table"));
        container.setItem(49, newButton);

        int totalPages = (int) Math.ceil((double) lootTableNames.size() / ITEMS_PER_PAGE);
        if (page < totalPages - 1) {
            ItemStack nextButton = new ItemStack(Items.ARROW);
            nextButton.setHoverName(Component.literal("§a下一頁"));
            container.setItem(53, nextButton);
        }
    }

    @Override
    public void clicked(int slotId, int button, @NotNull ClickType clickType, @NotNull Player player) {
        if (slotId < 0 || slotId >= 54) {
            return;
        }

        handleClick(slotId, clickType);
    }

    private void handleClick(int slot, ClickType clickType) {
        List<String> lootTableNames = new ArrayList<>(manager.getAllLootTableNames());
        int startIndex = page * ITEMS_PER_PAGE;

        // 上一頁
        if (slot == 45 && page > 0) {
            player.closeContainer();
            player.openMenu(new LootTableListMenu(manager, player, page - 1));
            return;
        }

        // 創建新 Loot Table
        if (slot == 49) {
            player.closeContainer();
            player.sendSystemMessage(Component.literal("§e請使用指令創建新的 Loot Table："));
            player.sendSystemMessage(Component.literal("§7/customloot new <名稱>"));
            return;
        }

        // 下一頁
        if (slot == 53) {
            int totalPages = (int) Math.ceil((double) lootTableNames.size() / ITEMS_PER_PAGE);
            if (page < totalPages - 1) {
                player.closeContainer();
                player.openMenu(new LootTableListMenu(manager, player, page + 1));
            }
            return;
        }

        // 點擊 Loot Table
        if (slot < 45) {
            int index = startIndex + slot;
            if (index < lootTableNames.size()) {
                String tableName = lootTableNames.get(index);

                if (clickType == ClickType.QUICK_MOVE) {
                    // Shift+左鍵：刪除
                    manager.deleteCustomLootTable(tableName);
                    player.sendSystemMessage(Component.literal("§c已刪除 Loot Table: §f" + tableName));
                    player.closeContainer();
                    player.openMenu(new LootTableListMenu(manager, player, page));
                } else {
                    // 左鍵：編輯
                    player.closeContainer();
                    LootTableEditorHandler.openEditor(player, tableName, manager);
                }
            }
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}

