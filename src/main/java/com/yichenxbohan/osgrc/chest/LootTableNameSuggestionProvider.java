package com.yichenxbohan.osgrc.chest;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;

import java.util.concurrent.CompletableFuture;

/**
 * 自定義 Loot Table 名稱自動補全提供器
 */
public class LootTableNameSuggestionProvider implements SuggestionProvider<CommandSourceStack> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        ServerLevel level = context.getSource().getLevel();
        CustomLootTableManager manager = ChestManagerHolder.getCustomLootManager(level);

        // 獲取所有自定義 Loot Table 名稱並添加到建議列表
        for (String tableName : manager.getAllLootTableNames()) {
            builder.suggest(tableName);
        }

        return builder.buildFuture();
    }
}

