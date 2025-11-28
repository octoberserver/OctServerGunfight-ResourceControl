package com.yichenxbohan.osgrc.chest;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 註冊箱子相關指令
 */
public class ChestCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // /chest add <name> <lootTable> [groups...]
        dispatcher.register(Commands.literal("chest")
            .then(Commands.literal("add")
                .then(Commands.argument("name", StringArgumentType.word())
                    .then(Commands.argument("lootTable", StringArgumentType.string())
                        .executes(ctx -> addChest(ctx.getSource(),
                            StringArgumentType.getString(ctx, "name"),
                            StringArgumentType.getString(ctx, "lootTable"),
                            null))
                        .then(Commands.argument("groups", StringArgumentType.greedyString())
                            .executes(ctx -> addChest(ctx.getSource(),
                                StringArgumentType.getString(ctx, "name"),
                                StringArgumentType.getString(ctx, "lootTable"),
                                StringArgumentType.getString(ctx, "groups"))))))));
        // /chest remove <name>
        dispatcher.register(Commands.literal("chest")
            .then(Commands.literal("remove")
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(ctx -> removeChest(ctx.getSource(), StringArgumentType.getString(ctx, "name"))))));
        // /chest list
        dispatcher.register(Commands.literal("chest")
            .then(Commands.literal("list")
                .executes(ctx -> listChests(ctx.getSource()))));
        // /chest clear <name> [seed]
        dispatcher.register(Commands.literal("chest")
            .then(Commands.literal("clear")
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(ctx -> clearChest(ctx.getSource(), StringArgumentType.getString(ctx, "name"), 0L))
                    .then(Commands.argument("seed", com.mojang.brigadier.arguments.LongArgumentType.longArg())
                        .executes(ctx -> clearChest(ctx.getSource(), StringArgumentType.getString(ctx, "name"),
                                                     com.mojang.brigadier.arguments.LongArgumentType.getLong(ctx, "seed")))))));
        // /chest info <name>
        dispatcher.register(Commands.literal("chest")
            .then(Commands.literal("info")
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(ctx -> infoChest(ctx.getSource(), StringArgumentType.getString(ctx, "name"))))));

        // /chest region <lootTable> <group> - 批量註冊選取區域內的所有箱子
        dispatcher.register(Commands.literal("chest")
            .then(Commands.literal("region")
                .then(Commands.argument("lootTable", StringArgumentType.string())
                    .then(Commands.argument("group", StringArgumentType.word())
                        .executes(ctx -> registerRegionChests(ctx.getSource(),
                            StringArgumentType.getString(ctx, "lootTable"),
                            StringArgumentType.getString(ctx, "group")))))));

        // ---------- 新增：group 子命令 ----------
        dispatcher.register(Commands.literal("chest")
            .then(Commands.literal("group")
                // /chest group add <chestName> <group>
                .then(Commands.literal("add")
                    .then(Commands.argument("chestName", StringArgumentType.word())
                        .then(Commands.argument("group", StringArgumentType.word())
                            .executes(ctx -> groupAddToChest(ctx.getSource(),
                                StringArgumentType.getString(ctx, "chestName"),
                                StringArgumentType.getString(ctx, "group"))))))
                // /chest group remove <group> - 刪除整個組
                .then(Commands.literal("remove")
                    .then(Commands.argument("group", StringArgumentType.word())
                        .executes(ctx -> groupDelete(ctx.getSource(),
                            StringArgumentType.getString(ctx, "group")))))
                // /chest group list - 列出所有組
                .then(Commands.literal("list")
                    .executes(ctx -> groupListAll(ctx.getSource())))
                // /chest group members <group>
                .then(Commands.literal("members")
                    .then(Commands.argument("group", StringArgumentType.word())
                        .executes(ctx -> groupMembers(ctx.getSource(),
                            StringArgumentType.getString(ctx, "group")))))
                // /chest group create <name> - 創建新組
                .then(Commands.literal("create")
                    .then(Commands.argument("name", StringArgumentType.word())
                        .executes(ctx -> groupCreate(ctx.getSource(),
                            StringArgumentType.getString(ctx, "name")))))
                // /chest group clear <group> [seed] - 清空組內所有箱子並重新套用 Loot Table
                .then(Commands.literal("clear")
                    .then(Commands.argument("group", StringArgumentType.word())
                        .executes(ctx -> groupClear(ctx.getSource(),
                            StringArgumentType.getString(ctx, "group"), null))
                        .then(Commands.argument("seed", com.mojang.brigadier.arguments.LongArgumentType.longArg())
                            .executes(ctx -> groupClear(ctx.getSource(),
                                StringArgumentType.getString(ctx, "group"),
                                com.mojang.brigadier.arguments.LongArgumentType.getLong(ctx, "seed"))))))
                // /chest group join <group> (玩家站在箱子上加入組)
                .then(Commands.literal("join")
                    .then(Commands.argument("group", StringArgumentType.word())
                        .executes(ctx -> groupJoinAtPlayer(ctx.getSource(),
                            StringArgumentType.getString(ctx, "group")))))
                // /chest group leave <group> (玩家站在箱子上離開組)
                .then(Commands.literal("leave")
                    .then(Commands.argument("group", StringArgumentType.word())
                        .executes(ctx -> groupLeaveAtPlayer(ctx.getSource(),
                            StringArgumentType.getString(ctx, "group")))))));

        // ---------- 新增：自定義 Loot Table 指令 ----------
        dispatcher.register(Commands.literal("customloot")
            .then(Commands.literal("create")
                .then(Commands.argument("name", StringArgumentType.word())
                    .then(Commands.argument("weights", StringArgumentType.greedyString())
                        .executes(ctx -> createCustomLootTableFromInventory(ctx.getSource(),
                            StringArgumentType.getString(ctx, "name"),
                            StringArgumentType.getString(ctx, "weights")))))));

        // /customloot list - 列出所有自定義 Loot Table
        dispatcher.register(Commands.literal("customloot")
            .then(Commands.literal("list")
                .executes(ctx -> listCustomLootTables(ctx.getSource()))));

        // /customloot info <name> - 查看自定義 Loot Table 詳情
        dispatcher.register(Commands.literal("customloot")
            .then(Commands.literal("info")
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(ctx -> infoCustomLootTable(ctx.getSource(),
                        StringArgumentType.getString(ctx, "name"))))));

        // /customloot delete <name> - 刪除自定義 Loot Table
        dispatcher.register(Commands.literal("customloot")
            .then(Commands.literal("delete")
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(ctx -> deleteCustomLootTable(ctx.getSource(),
                        StringArgumentType.getString(ctx, "name"))))));

        // /customloot apply <chestName> <tableName> - 將自定義 Loot Table 應用到箱子
        dispatcher.register(Commands.literal("customloot")
            .then(Commands.literal("apply")
                .then(Commands.argument("chestName", StringArgumentType.word())
                    .then(Commands.argument("tableName", StringArgumentType.word())
                        .executes(ctx -> applyCustomLootTable(ctx.getSource(),
                            StringArgumentType.getString(ctx, "chestName"),
                            StringArgumentType.getString(ctx, "tableName")))))));
        // /customloot slots <name> <min> <max> - 設置 Loot Table 的最小和最大填充格數
        dispatcher.register(Commands.literal("customloot")
            .then(Commands.literal("slots")
                .then(Commands.argument("name", StringArgumentType.word())
                    .then(Commands.argument("min", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1, 27))
                        .then(Commands.argument("max", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1, 27))
                            .executes(ctx -> setCustomLootTableSlots(ctx.getSource(),
                                StringArgumentType.getString(ctx, "name"),
                                com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(ctx, "min"),
                                com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(ctx, "max"))))))));

        // ---------- 新增：Loot Table 編輯指令（支援無限物品）----------

        // /customloot new <name> - 創建空的 Loot Table
        dispatcher.register(Commands.literal("customloot")
            .then(Commands.literal("new")
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(ctx -> createEmptyLootTable(ctx.getSource(),
                        StringArgumentType.getString(ctx, "name"))))));

        // /customloot additem <tableName> <weight> - 添加手持物品到 Loot Table
        dispatcher.register(Commands.literal("customloot")
            .then(Commands.literal("additem")
                .then(Commands.argument("tableName", StringArgumentType.word())
                    .then(Commands.argument("weight", com.mojang.brigadier.arguments.LongArgumentType.longArg(1))
                        .executes(ctx -> addItemToLootTable(ctx.getSource(),
                            StringArgumentType.getString(ctx, "tableName"),
                            com.mojang.brigadier.arguments.LongArgumentType.getLong(ctx, "weight")))))));

        // /customloot import <tableName> [weights] - 批量導入背包物品
        dispatcher.register(Commands.literal("customloot")
            .then(Commands.literal("import")
                .then(Commands.argument("tableName", StringArgumentType.word())
                    .executes(ctx -> importInventoryToLootTable(ctx.getSource(),
                        StringArgumentType.getString(ctx, "tableName"), null))
                    .then(Commands.argument("weights", StringArgumentType.greedyString())
                        .executes(ctx -> importInventoryToLootTable(ctx.getSource(),
                            StringArgumentType.getString(ctx, "tableName"),
                            StringArgumentType.getString(ctx, "weights")))))));

        // /customloot removeitem <tableName> <index> - 移除物品
        dispatcher.register(Commands.literal("customloot")
            .then(Commands.literal("removeitem")
                .then(Commands.argument("tableName", StringArgumentType.word())
                    .then(Commands.argument("index", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1))
                        .executes(ctx -> removeItemFromLootTable(ctx.getSource(),
                            StringArgumentType.getString(ctx, "tableName"),
                            com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(ctx, "index")))))));

        // /customloot clear <tableName> - 清空 Loot Table 的所有物品
        dispatcher.register(Commands.literal("customloot")
            .then(Commands.literal("clear")
                .then(Commands.argument("tableName", StringArgumentType.word())
                    .executes(ctx -> clearLootTableItems(ctx.getSource(),
                        StringArgumentType.getString(ctx, "tableName"))))));

        // /customloot setweight <tableName> <index> <newWeight> - 修改物品權重
        dispatcher.register(Commands.literal("customloot")
            .then(Commands.literal("setweight")
                .then(Commands.argument("tableName", StringArgumentType.word())
                    .then(Commands.argument("index", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1))
                        .then(Commands.argument("newWeight", com.mojang.brigadier.arguments.LongArgumentType.longArg(1))
                            .executes(ctx -> updateItemWeight(ctx.getSource(),
                                StringArgumentType.getString(ctx, "tableName"),
                                com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(ctx, "index"),
                                com.mojang.brigadier.arguments.LongArgumentType.getLong(ctx, "newWeight"))))))));
    }

    /**
     * 在玩家位置新增箱子
     */
    private static int addChest(CommandSourceStack source, String name, String lootTable, String groupsStr) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c只有玩家可以執行此命令"));
            return 0;
        }

        ServerLevel level = source.getLevel();
        BlockPos playerPos = player.blockPosition();

        // 檢查玩家下方是否是箱子
        BlockPos chestPos = playerPos.below();
        if (level.getBlockState(chestPos).getBlock() != Blocks.CHEST) {
            source.sendFailure(Component.literal("§c你下方沒有箱子！請站在箱子上方執行此命令"));
            return 0;
        }

        ChestManager manager = ChestManagerHolder.getManager(level);
        if (manager.addChest(name, chestPos, lootTable)) {
            // 如果有指定組，則將箱子加入組
            if (groupsStr != null && !groupsStr.isEmpty()) {
                String[] groups = groupsStr.split(",");
                int addedCount = 0;
                for (String group : groups) {
                    group = group.trim();
                    if (!group.isEmpty() && manager.addChestToGroup(name, group)) {
                        addedCount++;
                    }
                }

                if (addedCount > 0) {
                    final int finalCount = addedCount;
                    source.sendSuccess(() -> Component.literal("§a已新增箱子 \"" + name + "\" 於 " +
                            chestPos.getX() + ", " + chestPos.getY() + ", " + chestPos.getZ() +
                            "，Loot Table: " + lootTable +
                            "，並加入 " + finalCount + " 個組"), true);
                } else {
                    source.sendSuccess(() -> Component.literal("§a已新增箱子 \"" + name + "\" 於 " +
                            chestPos.getX() + ", " + chestPos.getY() + ", " + chestPos.getZ() +
                            "，Loot Table: " + lootTable + " §e(組加入失敗)"), true);
                }
            } else {
                source.sendSuccess(() -> Component.literal("§a已新增箱子 \"" + name + "\" 於 " +
                        chestPos.getX() + ", " + chestPos.getY() + ", " + chestPos.getZ() +
                        "，Loot Table: " + lootTable), true);
            }
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c箱子 \"" + name + "\" 已存在！"));
            return 0;
        }
    }

    /**
     * 刪除指定的箱子記錄
     */
    private static int removeChest(CommandSourceStack source, String name) {
        ServerLevel level = source.getLevel();
        ChestManager manager = ChestManagerHolder.getManager(level);

        if (manager.removeChest(name)) {
            source.sendSuccess(() -> Component.literal("§a已刪除箱子 \"" + name + "\""), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c找不到箱子 \"" + name + "\""));
            return 0;
        }
    }

    /**
     * 列出所有已標記的箱子
     */
    private static int listChests(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        ChestManager manager = ChestManagerHolder.getManager(level);

        var chests = manager.getAllChests();
        if (chests.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§6沒有標記的箱子"), false);
        } else {
            source.sendSuccess(() -> Component.literal("§6=== 標記的箱子列表 ==="), false);
            for (ChestData chest : chests) {
                BlockPos pos = chest.getPosition();
                source.sendSuccess(() -> Component.literal("§e• " + chest.getName() +
                        " - 位置: [" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "] " +
                        "Loot Table: " + chest.getLootTable()), false);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 清空箱子內容並套用 Loot Table
     */
    private static int clearChest(CommandSourceStack source, String name, long seed) {
        ServerLevel level = source.getLevel();
        ChestManager manager = ChestManagerHolder.getManager(level);

        ChestData chestData = manager.getChest(name);
        if (chestData == null) {
            source.sendFailure(Component.literal("§c找不到箱子 \"" + name + "\""));
            return 0;
        }

        BlockPos pos = chestData.getPosition();
        if (level.getBlockState(pos).getBlock() != Blocks.CHEST) {
            source.sendFailure(Component.literal("§c箱子位置 [" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "] 不存在或已被破壞"));
            return 0;
        }

        if (manager.clearAndApplyLootTable(name, seed)) {
            source.sendSuccess(() -> Component.literal("§a已清空並重新套用 Loot Table \"" +
                    chestData.getLootTable() + "\" 到箱子 \"" + name + "\""), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c無法套用 Loot Table，請檢查 Loot Table 是否存在"));
            return 0;
        }
    }

    /**
     * 顯示箱子的詳細信息
     */
    private static int infoChest(CommandSourceStack source, String name) {
        ServerLevel level = source.getLevel();
        ChestManager manager = ChestManagerHolder.getManager(level);

        ChestData chest = manager.getChest(name);
        if (chest == null) {
            source.sendFailure(Component.literal("§c找不到箱子 \"" + name + "\""));
            return 0;
        }

        BlockPos pos = chest.getPosition();
        source.sendSuccess(() -> Component.literal("§6=== 箱子信息 ==="), false);
        source.sendSuccess(() -> Component.literal("§e名稱: " + chest.getName()), false);
        source.sendSuccess(() -> Component.literal("§e位置: [" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]"), false);
        source.sendSuccess(() -> Component.literal("§eLoot Table: " + chest.getLootTable()), false);

        if (level.getBlockState(pos).getBlock() == Blocks.CHEST) {
            source.sendSuccess(() -> Component.literal("§a箱子狀態: 存在"), false);
        } else {
            source.sendSuccess(() -> Component.literal("§c箱子狀態: 已破壞或不是箱子"), false);
        }

        // 顯示組信息
        List<String> groups = chest.getGroups();
        if (groups.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§e組: (無)"), false);
        } else {
            source.sendSuccess(() -> Component.literal("§e組: " + String.join(", ", groups)), false);
        }

        return Command.SINGLE_SUCCESS;
    }

    /**
     * 批量註冊選取區域內的所有箱子
     */
    private static int registerRegionChests(CommandSourceStack source, String lootTable, String group) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c只有玩家可以執行此命令"));
            return 0;
        }

        // 檢查是否已選取區域
        if (!RegionSelector.hasSelection(player)) {
            source.sendFailure(Component.literal("§c請先用金斧頭選取區域！左鍵選第一點，右鍵選第二點"));
            return 0;
        }

        ServerLevel level = source.getLevel();
        BlockPos minPos = RegionSelector.getMinPos(player);
        BlockPos maxPos = RegionSelector.getMaxPos(player);

        ChestManager manager = ChestManagerHolder.getManager(level);
        int successCount = 0;
        int chestIndex = 1;

        // 遍歷區域內的所有方塊
        for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
            for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);

                    // 檢查該位置是否為箱子
                    if (level.getBlockState(pos).getBlock() == Blocks.CHEST) {
                        // 生成唯一名稱
                        String chestName = "region_chest_" + chestIndex;

                        // 嘗試新增箱子
                        if (manager.addChest(chestName, pos, lootTable)) {
                            // 將箱子加入組
                            manager.addChestToGroup(chestName, group);
                            successCount++;
                            chestIndex++;
                        }
                    }
                }
            }
        }

        if (successCount > 0) {
            final int count = successCount;
            source.sendSuccess(() -> Component.literal("§a成功註冊 " + count + " 個箱子到組 \"" + group + "\""), true);
            source.sendSuccess(() -> Component.literal("§eLoot Table: " + lootTable), false);

            // 清除選取
            RegionSelector.clearSelection(player);
            source.sendSuccess(() -> Component.literal("§6已清除區域選取"), false);

            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c選取區域內沒有找到任何箱子"));
            return 0;
        }
    }

    // ---------- 新增：group 命令處理函數 ----------

    /**
     * 將箱子加入組
     */
    private static int groupAddToChest(CommandSourceStack source, String chestName, String group) {
        ServerLevel level = source.getLevel();
        ChestManager manager = ChestManagerHolder.getManager(level);

        if (!manager.hasChest(chestName)) {
            source.sendFailure(Component.literal("§c找不到箱子 \"" + chestName + "\""));
            return 0;
        }

        boolean added = manager.addChestToGroup(chestName, group);
        if (added) {
            source.sendSuccess(() -> Component.literal("§a已將箱子 \"" + chestName + "\" 加入組 \"" + group + "\""), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c箱子已在該組或無效的組名稱"));
            return 0;
        }
    }

    /**
     * 將箱子從組中移除
     */
    private static int groupRemoveFromChest(CommandSourceStack source, String chestName, String group) {
        ServerLevel level = source.getLevel();
        ChestManager manager = ChestManagerHolder.getManager(level);

        if (!manager.hasChest(chestName)) {
            source.sendFailure(Component.literal("§c找不到箱子 \"" + chestName + "\""));
            return 0;
        }

        boolean removed = manager.removeChestFromGroup(chestName, group);
        if (removed) {
            source.sendSuccess(() -> Component.literal("§a已將箱子 \"" + chestName + "\" 從組 \"" + group + "\" 移除"), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c箱子不在該組"));
            return 0;
        }
    }


    /**
     * 列出所有組
     */
    private static int groupListAll(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        ChestManager manager = ChestManagerHolder.getManager(level);

        List<String> groups = manager.getAllGroups();
        if (groups.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§6目前沒有任何組"), false);
        } else {
            source.sendSuccess(() -> Component.literal("§6=== 所有組列表 ==="), false);
            for (String group : groups) {
                source.sendSuccess(() -> Component.literal("§e• " + group), false);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 創建新組
     */
    private static int groupCreate(CommandSourceStack source, String name) {
        ServerLevel level = source.getLevel();
        ChestManager manager = ChestManagerHolder.getManager(level);

        if (manager.createGroup(name)) {
            source.sendSuccess(() -> Component.literal("§a已創建新組 \"" + name + "\""), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c無法創建組，可能是因為組已存在"));
            return 0;
        }
    }

    /**
     * 刪除整個組
     */
    private static int groupDelete(CommandSourceStack source, String groupName) {
        ServerLevel level = source.getLevel();
        ChestManager manager = ChestManagerHolder.getManager(level);

        List<ChestData> affectedChests = manager.getChestsByGroup(groupName);
        int chestCount = affectedChests.size();

        if (manager.deleteGroup(groupName)) {
            if (chestCount > 0) {
                final int count = chestCount;
                source.sendSuccess(() -> Component.literal("§a已刪除組 \"" + groupName + "\"，並從 " + count + " 個箱子中移除此組"), true);
            } else {
                source.sendSuccess(() -> Component.literal("§a已刪除組 \"" + groupName + "\""), true);
            }
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c找不到組 \"" + groupName + "\""));
            return 0;
        }
    }

    /**
     * 清空組內所有箱子並重新套用 Loot Table
     */
    private static int groupClear(CommandSourceStack source, String groupName, Long seed) {
        ServerLevel level = source.getLevel();
        ChestManager manager = ChestManagerHolder.getManager(level);

        List<ChestData> groupChests = manager.getChestsByGroup(groupName);
        if (groupChests.isEmpty()) {
            source.sendFailure(Component.literal("§c組 \"" + groupName + "\" 不存在或沒有成員"));
            return 0;
        }

        int totalChests = groupChests.size();
        int successCount = manager.clearGroupChests(groupName, seed);

        if (successCount > 0) {
            final int success = successCount;
            final int total = totalChests;
            if (success == total) {
                source.sendSuccess(() -> Component.literal("§a已成功重置組 \"" + groupName + "\" 內的所有 " + total + " 個箱子"), true);
            } else {
                source.sendSuccess(() -> Component.literal("§e已重置組 \"" + groupName + "\" 內的 " + success + "/" + total + " 個箱子 §c(部分箱子可能已被破壞)"), true);
            }
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c無法重置組內的箱子，請檢查箱子是否存在"));
            return 0;
        }
    }

    /**
     * 列出組內的所有箱子
     */
    private static int groupMembers(CommandSourceStack source, String group) {
        ServerLevel level = source.getLevel();
        ChestManager manager = ChestManagerHolder.getManager(level);

        List<ChestData> chests = manager.getChestsByGroup(group);
        if (chests.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§6組 \"" + group + "\" 沒有成員"), false);
        } else {
            source.sendSuccess(() -> Component.literal("§6=== 組 \"" + group + "\" 的成員 ==="), false);
            for (ChestData chest : chests) {
                BlockPos pos = chest.getPosition();
                source.sendSuccess(() -> Component.literal("§e• " + chest.getName() +
                    " - 位置: [" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]"), false);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 將玩家腳下的箱子加入組
     */
    private static int groupJoinAtPlayer(CommandSourceStack source, String group) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c只有玩家可以執行此命令"));
            return 0;
        }

        ServerLevel level = source.getLevel();
        BlockPos chestPos = player.blockPosition().below();

        if (level.getBlockState(chestPos).getBlock() != Blocks.CHEST) {
            source.sendFailure(Component.literal("§c你下方沒有箱子！請站在箱子上方執行此命令"));
            return 0;
        }

        // 找到該位置已標記的箱子
        ChestManager manager = ChestManagerHolder.getManager(level);
        ChestData found = null;
        for (ChestData c : manager.getAllChests()) {
            if (c.getPosition().equals(chestPos)) {
                found = c;
                break;
            }
        }

        if (found == null) {
            source.sendFailure(Component.literal("§c該箱子未被標記，請先使用 /chest add 註冊"));
            return 0;
        }

        final String chestName = found.getName();
        boolean added = manager.addChestToGroup(chestName, group);
        if (added) {
            source.sendSuccess(() -> Component.literal("§a已將箱子 \"" + chestName + "\" 加入組 \"" + group + "\""), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c箱子已在該組或無效的組名稱"));
            return 0;
        }
    }

    /**
     * 玩家腳下的箱子離開組
     */
    private static int groupLeaveAtPlayer(CommandSourceStack source, String group) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c只有玩家可以執行此命令"));
            return 0;
        }

        ServerLevel level = source.getLevel();
        BlockPos chestPos = player.blockPosition().below();

        if (level.getBlockState(chestPos).getBlock() != Blocks.CHEST) {
            source.sendFailure(Component.literal("§c你下方沒有箱子！請站在箱子上方執行此命令"));
            return 0;
        }

        // 找到該位置已標記的箱子
        ChestManager manager = ChestManagerHolder.getManager(level);
        ChestData found = null;
        for (ChestData c : manager.getAllChests()) {
            if (c.getPosition().equals(chestPos)) {
                found = c;
                break;
            }
        }

        if (found == null) {
            source.sendFailure(Component.literal("§c該箱子未被標記，請先使用 /chest add 註冊"));
            return 0;
        }

        final String chestName = found.getName();
        boolean removed = manager.removeChestFromGroup(chestName, group);
        if (removed) {
            source.sendSuccess(() -> Component.literal("§a已將箱子 \"" + chestName + "\" 從組 \"" + group + "\" 移除"), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c箱子不在該組"));
            return 0;
        }
    }

    // ---------- 自定義 Loot Table 命令處理函數 ----------

    /**
     * 從玩家背包創建自定義 Loot Table
     * 格式: /customloot create <name> <slot0_weight>,<slot1_weight>,...
     * 例: /customloot create my_table 10,5,20,1,1,1,1,1,1...
     * 或簡單: /customloot create my_table (默認所有物品權重為1)
     */
    private static int createCustomLootTableFromInventory(CommandSourceStack source, String name, String weightStr) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c只有玩家可以執行此命令"));
            return 0;
        }

        ServerLevel level = source.getLevel();
        CustomLootTableManager manager = ChestManagerHolder.getCustomLootManager(level);

        // 檢查是否已存在
        if (manager.hasLootTable(name)) {
            source.sendFailure(Component.literal("§c自定義 Loot Table \"" + name + "\" 已存在"));
            return 0;
        }

        // 解析權重
        Map<Integer, Long> weightMap = new HashMap<>();
        if (weightStr != null && !weightStr.isEmpty() && !weightStr.equals("1")) {
            try {
                String[] weights = weightStr.split(",");
                for (int i = 0; i < weights.length && i < 27; i++) {
                    long weight = Long.parseLong(weights[i].trim());
                    if (weight > 0) {
                        weightMap.put(i, weight);
                    }
                }
            } catch (NumberFormatException e) {
                source.sendFailure(Component.literal("§c權重格式錯誤！請使用逗號分隔的數字"));
                return 0;
            }
        }

        // 從玩家背包創建
        CustomLootTable table = CustomLootTable.fromPlayerInventory(name, player, weightMap);

        // 檢查是否有物品
        if (table.getItems().isEmpty()) {
            source.sendFailure(Component.literal("§c您的背包前27格沒有任何物品！"));
            return 0;
        }

        // 保存
        if (manager.createCustomLootTable(name, table)) {
            source.sendSuccess(() -> Component.literal("§a已創建自定義 Loot Table \"" + name + "\"，包含 " +
                    table.getItems().size() + " 種物品，總權重: " + table.getTotalWeight()), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c無法創建 Loot Table"));
            return 0;
        }
    }

    /**
     * 列出所有自定義 Loot Table
     */
    private static int listCustomLootTables(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        CustomLootTableManager manager = ChestManagerHolder.getCustomLootManager(level);

        List<String> tableNames = manager.listLootTableNames();
        if (tableNames.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§6沒有自定義 Loot Table"), false);
        } else {
            source.sendSuccess(() -> Component.literal("§6=== 自定義 Loot Table 列表 ==="), false);
            for (String tableName : tableNames) {
                CustomLootTable table = manager.getCustomLootTable(tableName);
                source.sendSuccess(() -> Component.literal("§e• " + tableName +
                        " §8(物品數: " + table.getItems().size() + ", 總權重: " + table.getTotalWeight() + ")"), false);
            }
        }
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 查看自定義 Loot Table 詳情
     */
    private static int infoCustomLootTable(CommandSourceStack source, String name) {
        ServerLevel level = source.getLevel();
        CustomLootTableManager manager = ChestManagerHolder.getCustomLootManager(level);

        CustomLootTable table = manager.getCustomLootTable(name);
        if (table == null) {
            source.sendFailure(Component.literal("§c找不到自定義 Loot Table \"" + name + "\""));
            return 0;
        }

        source.sendSuccess(() -> Component.literal("§6" + table.toString()), false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 刪除自定義 Loot Table
     */
    private static int deleteCustomLootTable(CommandSourceStack source, String name) {
        ServerLevel level = source.getLevel();
        CustomLootTableManager manager = ChestManagerHolder.getCustomLootManager(level);

        if (manager.deleteCustomLootTable(name)) {
            source.sendSuccess(() -> Component.literal("§a已刪除自定義 Loot Table \"" + name + "\""), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c找不到自定義 Loot Table \"" + name + "\""));
            return 0;
        }
    }

    /**
     * 將自定義 Loot Table 應用到箱子
     * 這會將箱子的 Loot Table 指向自定義表，使用特殊前綴標記
     */
    private static int applyCustomLootTable(CommandSourceStack source, String chestName, String tableName) {
        ServerLevel level = source.getLevel();
        ChestManager chestManager = ChestManagerHolder.getManager(level);
        CustomLootTableManager lootManager = ChestManagerHolder.getCustomLootManager(level);

        // 檢查箱子是否存在
        ChestData chestData = chestManager.getChest(chestName);
        if (chestData == null) {
            source.sendFailure(Component.literal("§c找不到箱子 \"" + chestName + "\""));
            return 0;
        }

        // 檢查 Loot Table 是否存在
        if (!lootManager.hasLootTable(tableName)) {
            source.sendFailure(Component.literal("§c找不到自定義 Loot Table \"" + tableName + "\""));
            return 0;
        }

        // 使用特殊前綴標記為自定義 Loot Table
        String customLootId = "CUSTOM_LOOT:" + tableName;

        // 注意：這裡需要修改 ChestData 或 ChestManager 來支持更新 Loot Table
        // 目前我們通過刪除並重新添加來實現
        BlockPos pos = chestData.getPosition();
        chestManager.removeChest(chestName);
        chestManager.addChest(chestName, pos, customLootId);

        // 復原所有組
        CustomLootTable originalTable = lootManager.getCustomLootTable(tableName);
        for (String group : chestData.getGroups()) {
            chestManager.addChestToGroup(chestName, group);
        }

        source.sendSuccess(() -> Component.literal("§a已將自定義 Loot Table \"" + tableName +
                "\" 應用到箱子 \"" + chestName + "\""), true);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 設置自定義 Loot Table 的最小和最大填充格數
     * /customloot slots <name> <min> <max>
     */
    private static int setCustomLootTableSlots(CommandSourceStack source, String name, int minSlots, int maxSlots) {
        ServerLevel level = source.getLevel();
        CustomLootTableManager manager = ChestManagerHolder.getCustomLootManager(level);

        // 檢查 Loot Table 是否存在
        if (!manager.hasLootTable(name)) {
            source.sendFailure(Component.literal("§c找不到自定義 Loot Table \"" + name + "\""));
            return 0;
        }

        // 設置格數
        CustomLootTable table = manager.getCustomLootTable(name);
        table.setMinSlots(minSlots);
        table.setMaxSlots(maxSlots);

        source.sendSuccess(() -> Component.literal("§a已設置 Loot Table \"" + name + "\" 的最小填充格數為 " + minSlots +
                "，最大填充格數為 " + maxSlots), true);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 創建空的自定義 Loot Table
     * /customloot new <name>
     */
    private static int createEmptyLootTable(CommandSourceStack source, String name) {
        ServerLevel level = source.getLevel();
        CustomLootTableManager manager = ChestManagerHolder.getCustomLootManager(level);

        // 檢查是否已存在
        if (manager.hasLootTable(name)) {
            source.sendFailure(Component.literal("§c自定義 Loot Table \"" + name + "\" 已存在"));
            return 0;
        }

        // 創建空的 Loot Table
        CustomLootTable table = CustomLootTable.emptyTable(name);

        // 保存
        if (manager.createCustomLootTable(name, table)) {
            source.sendSuccess(() -> Component.literal("§a已創建空的自定義 Loot Table \"" + name + "\""), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c無法創建 Loot Table"));
            return 0;
        }
    }

    /**
     * 添加手持物品到指定的 Loot Table
     * /customloot additem <tableName> <weight>
     */
    private static int addItemToLootTable(CommandSourceStack source, String tableName, long weight) {
        ServerLevel level = source.getLevel();
        CustomLootTableManager manager = ChestManagerHolder.getCustomLootManager(level);

        // 檢查 Loot Table 是否存在
        if (!manager.hasLootTable(tableName)) {
            source.sendFailure(Component.literal("§c找不到自定義 Loot Table \"" + tableName + "\""));
            return 0;
        }

        // 獲取手持物品
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c只有玩家可以執行此命令"));
            return 0;
        }

        // 添加物品
        CustomLootTable table = manager.getCustomLootTable(tableName);
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendFailure(Component.literal("§c請手持一個物品來添加到 Loot Table"));
            return 0;
        }

        // 添加物品到 Loot Table
        table.addItem(heldItem, weight);

        source.sendSuccess(() -> Component.literal("§a已將手持物品添加到 Loot Table \"" + tableName + "\"，權重: " + weight), true);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 批量導入背包物品到指定的 Loot Table
     * /customloot import <tableName> [weights]
     */
    private static int importInventoryToLootTable(CommandSourceStack source, String tableName, String weightsStr) {
        ServerLevel level = source.getLevel();
        CustomLootTableManager manager = ChestManagerHolder.getCustomLootManager(level);

        // 檢查 Loot Table 是否存在
        if (!manager.hasLootTable(tableName)) {
            source.sendFailure(Component.literal("§c找不到自定義 Loot Table \"" + tableName + "\""));
            return 0;
        }

        // 獲取背包物品
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("§c只有玩家可以執行此命令"));
            return 0;
        }

        // 解析權重
        Map<Integer, Long> weightMap = new HashMap<>();
        if (weightsStr != null && !weightsStr.isEmpty() && !weightsStr.equals("1")) {
            try {
                String[] weights = weightsStr.split(",");
                for (int i = 0; i < weights.length && i < 27; i++) {
                    long weight = Long.parseLong(weights[i].trim());
                    if (weight > 0) {
                        weightMap.put(i, weight);
                    }
                }
            } catch (NumberFormatException e) {
                source.sendFailure(Component.literal("§c權重格式錯誤！請使用逗號分隔的數字"));
                return 0;
            }
        }

        // 將背包物品添加到 Loot Table
        CustomLootTable table = manager.getCustomLootTable(tableName);
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                long weight = weightMap.getOrDefault(i, 1L); // 默認權重為 1
                table.addItem(stack, weight);
            }
        }

        source.sendSuccess(() -> Component.literal("§a已將背包物品導入到 Loot Table \"" + tableName + "\""), true);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 移除指定 Loot Table 中的物品
     * /customloot removeitem <tableName> <index>
     */
    private static int removeItemFromLootTable(CommandSourceStack source, String tableName, int index) {
        ServerLevel level = source.getLevel();
        CustomLootTableManager manager = ChestManagerHolder.getCustomLootManager(level);

        // 檢查 Loot Table 是否存在
        if (!manager.hasLootTable(tableName)) {
            source.sendFailure(Component.literal("§c找不到自定義 Loot Table \"" + tableName + "\""));
            return 0;
        }

        // 移除物品
        CustomLootTable table = manager.getCustomLootTable(tableName);
        if (table.removeItem(index)) {
            source.sendSuccess(() -> Component.literal("§a已從 Loot Table \"" + tableName + "\" 移除物品，索引: " + index), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c無法移除物品，請檢查索引是否有效"));
            return 0;
        }
    }

    /**
     * 清空指定 Loot Table 的所有物品
     * /customloot clear <tableName>
     */
    private static int clearLootTableItems(CommandSourceStack source, String tableName) {
        ServerLevel level = source.getLevel();
        CustomLootTableManager manager = ChestManagerHolder.getCustomLootManager(level);

        // 檢查 Loot Table 是否存在
        if (!manager.hasLootTable(tableName)) {
            source.sendFailure(Component.literal("§c找不到自定義 Loot Table \"" + tableName + "\""));
            return 0;
        }

        // 清空物品
        CustomLootTable table = manager.getCustomLootTable(tableName);
        table.clearItems();

        source.sendSuccess(() -> Component.literal("§a已清空 Loot Table \"" + tableName + "\" 的所有物品"), true);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 修改指定 Loot Table 中物品的權重
     * /customloot setweight <tableName> <index> <newWeight>
     */
    private static int updateItemWeight(CommandSourceStack source, String tableName, int index, long newWeight) {
        ServerLevel level = source.getLevel();
        CustomLootTableManager manager = ChestManagerHolder.getCustomLootManager(level);

        // 檢查 Loot Table 是否存在
        if (!manager.hasLootTable(tableName)) {
            source.sendFailure(Component.literal("§c找不到自定義 Loot Table \"" + tableName + "\""));
            return 0;
        }

        // 修改權重
        CustomLootTable table = manager.getCustomLootTable(tableName);
        if (table.updateItemWeight(index, newWeight)) {
            source.sendSuccess(() -> Component.literal("§a已修改 Loot Table \"" + tableName + "\" 中物品的權重，索引: " + index + "，新權重: " + newWeight), true);
            return Command.SINGLE_SUCCESS;
        } else {
            source.sendFailure(Component.literal("§c無法修改物品權重，請檢查索引是否有效"));
            return 0;
        }
    }
}
