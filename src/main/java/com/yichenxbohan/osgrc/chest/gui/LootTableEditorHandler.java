package com.yichenxbohan.osgrc.chest.gui;

import com.yichenxbohan.osgrc.chest.CustomLootTable;
import com.yichenxbohan.osgrc.chest.CustomLootTableManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 處理 Loot Table 編輯器 GUI 的交互事件
 */
@Mod.EventBusSubscriber(modid = "osgrc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LootTableEditorHandler {

    @SubscribeEvent
    public static void onContainerClick(PlayerContainerEvent.Close event) {
        // 當玩家關閉容器時的處理
        if (event.getEntity() instanceof ServerPlayer player) {
            // 只有關閉 LootTableEditorContainer 時才顯示提示
            if (player.containerMenu instanceof LootTableEditorContainer) {
                player.sendSystemMessage(Component.literal("§aLoot Table 已保存"));
            }
        }
    }

    /**
     * 打開 Loot Table 編輯器
     */
    public static void openEditor(ServerPlayer player, String lootTableName, CustomLootTableManager manager) {
        openEditor(player, lootTableName, manager, 0);
    }

    /**
     * 打開 Loot Table 編輯器（指定頁碼）
     */
    public static void openEditor(ServerPlayer player, String lootTableName, CustomLootTableManager manager, int page) {
        CustomLootTable lootTable = manager.getLootTable(lootTableName);
        if (lootTable == null) {
            player.sendSystemMessage(Component.literal("§c錯誤：Loot Table '" + lootTableName + "' 不存在"));
            return;
        }

        LootTableEditorMenu menu = new LootTableEditorMenu(lootTableName, manager, player, page);
        player.openMenu(menu);
    }

    /**
     * 處理 GUI 中的點擊事件
     */
    public static void handleClick(ServerPlayer player, int slot, ClickType clickType,
                                   String lootTableName, CustomLootTableManager manager, int page) {
        CustomLootTable lootTable = manager.getLootTable(lootTableName);
        if (lootTable == null) return;

        // 控制按鈕槽位
        if (slot == 36 && page > 0) {
            // 上一頁
            player.closeContainer();
            openEditor(player, lootTableName, manager, page - 1);
            return;
        }

        if (slot == 44) {
            // 下一頁
            int totalPages = (int) Math.ceil((double) lootTable.getItems().size() / 21.0);
            if (page < totalPages - 1) {
                player.closeContainer();
                openEditor(player, lootTableName, manager, page + 1);
            }
            return;
        }

        if (slot == 45) {
            // 添加手持物品
            ItemStack heldItem = player.getMainHandItem();
            if (!heldItem.isEmpty()) {
                lootTable.addItem(heldItem, 100);
                manager.saveLootTables();
                player.sendSystemMessage(Component.literal("§a已添加物品: §f" + heldItem.getHoverName().getString()));
                player.closeContainer();
                openEditor(player, lootTableName, manager, page);
            } else {
                player.sendSystemMessage(Component.literal("§c請先手持要添加的物品"));
            }
            return;
        }

        if (slot == 49) {
            // 設置格數
            player.closeContainer();
            player.sendSystemMessage(Component.literal("§e請使用指令設置格數："));
            player.sendSystemMessage(Component.literal("§7/customloot slots " + lootTableName + " <最小格數> <最大格數>"));
            return;
        }

        if (slot == 53) {
            // 關閉
            player.closeContainer();
            player.sendSystemMessage(Component.literal("§aLoot Table 編輯完成"));
            return;
        }

        // 處理物品槽位點擊（前3行，每行7個）
        int row = slot / 9;
        int col = slot % 9;
        if (row < 3 && col < 7) {
            int itemIndex = (page * 21) + (row * 7 + col);
            if (itemIndex < lootTable.getItems().size()) {
                if (clickType == ClickType.QUICK_MOVE) {
                    // Shift+左鍵：刪除物品
                    CustomLootTable.LootItem item = lootTable.getItems().get(itemIndex);
                    lootTable.removeItem(itemIndex);
                    manager.saveLootTables();
                    player.sendSystemMessage(Component.literal("§c已刪除物品: §f" +
                        item.itemStack.getHoverName().getString()));
                    player.closeContainer();
                    openEditor(player, lootTableName, manager, page);
                } else if (clickType == ClickType.PICKUP_ALL) {
                    // 右鍵：調整權重
                    player.closeContainer();
                    player.sendSystemMessage(Component.literal("§e請使用指令調整權重："));
                    player.sendSystemMessage(Component.literal("§7/customloot setweight " +
                        lootTableName + " " + (itemIndex + 1) + " <新權重>"));
                }
            }
        }
    }
}
