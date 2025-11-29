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
 * Loot Table 列表選擇 GUI
 */
public class LootTableListMenu implements MenuProvider {
    private final CustomLootTableManager manager;
    private final ServerPlayer player;
    private final int page;

    public LootTableListMenu(CustomLootTableManager manager, ServerPlayer player, int page) {
        this.manager = manager;
        this.player = player;
        this.page = page;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal("§6§lLoot Table 管理器 §7(頁 " + (page + 1) + ")");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new LootTableListContainer(containerId, playerInventory, manager, this.player, page);
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
