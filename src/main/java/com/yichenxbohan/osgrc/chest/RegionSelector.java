package com.yichenxbohan.osgrc.chest;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 管理玩家的區域選取
 */
public class RegionSelector {
    private static final Map<UUID, BlockPos> firstPositions = new HashMap<>();
    private static final Map<UUID, BlockPos> secondPositions = new HashMap<>();

    /**
     * 設置第一個位置
     */
    public static void setFirstPosition(ServerPlayer player, BlockPos pos) {
        firstPositions.put(player.getUUID(), pos);
    }

    /**
     * 設置第二個位置
     */
    public static void setSecondPosition(ServerPlayer player, BlockPos pos) {
        secondPositions.put(player.getUUID(), pos);
    }

    /**
     * 獲取第一個位置
     */
    public static BlockPos getFirstPosition(ServerPlayer player) {
        return firstPositions.get(player.getUUID());
    }

    /**
     * 獲取第二個位置
     */
    public static BlockPos getSecondPosition(ServerPlayer player) {
        return secondPositions.get(player.getUUID());
    }

    /**
     * 檢查玩家是否已選取兩個位置
     */
    public static boolean hasSelection(ServerPlayer player) {
        return firstPositions.containsKey(player.getUUID()) &&
               secondPositions.containsKey(player.getUUID());
    }

    /**
     * 清除玩家的選取
     */
    public static void clearSelection(ServerPlayer player) {
        firstPositions.remove(player.getUUID());
        secondPositions.remove(player.getUUID());
    }

    /**
     * 獲取選取的最小座標
     */
    public static BlockPos getMinPos(ServerPlayer player) {
        BlockPos pos1 = firstPositions.get(player.getUUID());
        BlockPos pos2 = secondPositions.get(player.getUUID());

        if (pos1 == null || pos2 == null) return null;

        return new BlockPos(
            Math.min(pos1.getX(), pos2.getX()),
            Math.min(pos1.getY(), pos2.getY()),
            Math.min(pos1.getZ(), pos2.getZ())
        );
    }

    /**
     * 獲取選取的最大座標
     */
    public static BlockPos getMaxPos(ServerPlayer player) {
        BlockPos pos1 = firstPositions.get(player.getUUID());
        BlockPos pos2 = secondPositions.get(player.getUUID());

        if (pos1 == null || pos2 == null) return null;

        return new BlockPos(
            Math.max(pos1.getX(), pos2.getX()),
            Math.max(pos1.getY(), pos2.getY()),
            Math.max(pos1.getZ(), pos2.getZ())
        );
    }

    /**
     * 計算選取區域的體積
     */
    public static int getVolume(ServerPlayer player) {
        BlockPos min = getMinPos(player);
        BlockPos max = getMaxPos(player);

        if (min == null || max == null) return 0;

        return (max.getX() - min.getX() + 1) *
               (max.getY() - min.getY() + 1) *
               (max.getZ() - min.getZ() + 1);
    }
}

