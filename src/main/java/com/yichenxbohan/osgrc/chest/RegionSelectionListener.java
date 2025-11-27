package com.yichenxbohan.osgrc.chest;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * 監聽金斧頭的選取事件
 */
@Mod.EventBusSubscriber(modid = "osgrc", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegionSelectionListener {

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // 檢查是否持有金斧頭
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() != Items.GOLDEN_AXE) return;

        // 檢查是否在創造模式
        if (!player.isCreative()) return;

        BlockPos pos = event.getPos();
        RegionSelector.setFirstPosition(player, pos);

        player.sendSystemMessage(Component.literal("§a第一點已設置: §e[" +
            pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]"));

        // 如果已有第二點，顯示選取資訊
        if (RegionSelector.getSecondPosition(player) != null) {
            showSelectionInfo(player);
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // 檢查是否持有金斧頭
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() != Items.GOLDEN_AXE) return;

        // 檢查是否在創造模式
        if (!player.isCreative()) return;

        BlockPos pos = event.getPos();
        RegionSelector.setSecondPosition(player, pos);

        player.sendSystemMessage(Component.literal("§a第二點已設置: §e[" +
            pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]"));

        // 如果已有第一點，顯示選取資訊
        if (RegionSelector.getFirstPosition(player) != null) {
            showSelectionInfo(player);
        }

        event.setCanceled(true);
    }

    private static void showSelectionInfo(ServerPlayer player) {
        BlockPos min = RegionSelector.getMinPos(player);
        BlockPos max = RegionSelector.getMaxPos(player);
        int volume = RegionSelector.getVolume(player);

        player.sendSystemMessage(Component.literal("§6=== 選取區域資訊 ==="));
        player.sendSystemMessage(Component.literal("§e最小座標: [" +
            min.getX() + ", " + min.getY() + ", " + min.getZ() + "]"));
        player.sendSystemMessage(Component.literal("§e最大座標: [" +
            max.getX() + ", " + max.getY() + ", " + max.getZ() + "]"));
        player.sendSystemMessage(Component.literal("§e區域大小: " +
            (max.getX() - min.getX() + 1) + " x " +
            (max.getY() - min.getY() + 1) + " x " +
            (max.getZ() - min.getZ() + 1)));
        player.sendSystemMessage(Component.literal("§e總方塊數: " + volume));
        player.sendSystemMessage(Component.literal("§a使用 §6/chest region §a指令來批量註冊區域內的箱子"));
    }
}

