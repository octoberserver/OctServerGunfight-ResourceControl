package com.yichenxbohan.osgrc.chest;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.yichenxbohan.osgrc.Osgrc;

/**
 * 監聽服務器事件以初始化和關閉 ChestManager
 */
@Mod.EventBusSubscriber(modid = Osgrc.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ChestEventListener {

    /**
     * 服務器啟動事件
     */
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        // 指令註冊會在 RegisterCommandsEvent 中進行
    }

    /**
     * 服務器停止事件 - 保存所有數據
     */
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        ChestManagerHolder.saveAll();
        ChestManagerHolder.clear();
    }

    /**
     * 註冊指令
     */
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ChestCommands.register(event.getDispatcher());
    }
}


