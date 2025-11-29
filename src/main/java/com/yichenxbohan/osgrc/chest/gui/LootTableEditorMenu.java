package com.yichenxbohan.osgrc.chest.gui;

import com.yichenxbohan.osgrc.chest.CustomLootTableManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Loot Table 編輯器 GUI
 * 提供視覺化介面來編輯自定義 Loot Table
 */
public class LootTableEditorMenu implements MenuProvider {
    private final String lootTableName;
    private final CustomLootTableManager manager;
    private final ServerPlayer player;
    private final int page;

    public LootTableEditorMenu(String lootTableName, CustomLootTableManager manager, ServerPlayer player, int page) {
        this.lootTableName = lootTableName;
        this.manager = manager;
        this.player = player;
        this.page = page;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("§6§l編輯 Loot Table: §f" + lootTableName + " §7(頁 " + (page + 1) + ")");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new LootTableEditorContainer(containerId, playerInventory, manager, lootTableName, this.player, page);
    }

    public String getLootTableName() {
        return lootTableName;
    }

    public int getPage() {
        return page;
    }

    public CustomLootTableManager getManager() {
        return manager;
    }

    public ServerPlayer getPlayer() {
        return player;
    }
}
