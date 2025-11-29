package com.yichenxbohan.osgrc.chest;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;

import java.util.concurrent.CompletableFuture;

/**
 * 組名稱自動補全提供器
 */
public class GroupNameSuggestionProvider implements SuggestionProvider<CommandSourceStack> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        ServerLevel level = context.getSource().getLevel();
        ChestManager manager = ChestManagerHolder.getManager(level);

        // 獲取所有組名稱並添加到建議列表
        for (String groupName : manager.getAllGroups()) {
            builder.suggest(groupName);
        }

        return builder.buildFuture();
    }
}

