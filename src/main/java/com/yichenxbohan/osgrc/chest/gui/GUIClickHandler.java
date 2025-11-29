package com.yichenxbohan.osgrc.chest.gui;

import com.yichenxbohan.osgrc.chest.CustomLootTableManager;
import com.yichenxbohan.osgrc.chest.ChestManagerHolder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 處理 GUI 容器點擊事件
 */
@Mod.EventBusSubscriber(modid = "osgrc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GUIClickHandler {

    /**
     * 當玩家點擊容器時觸發
     */
    @SubscribeEvent
    public static void onContainerClick(net.minecraftforge.event.entity.player.PlayerEvent.ItemPickupEvent event) {
        // 這個事件處理物品拾取，我們需要用另一個方式
    }

    /**
     * 當容器打開時記錄
     */
    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // 記錄打開的容器類型
            if (player.containerMenu instanceof ChestMenu) {
                // 這是箱子菜單
                String title = player.containerMenu.getClass().getSimpleName();
                // 可以在這裡添加日誌
            }
        }
    }
}

