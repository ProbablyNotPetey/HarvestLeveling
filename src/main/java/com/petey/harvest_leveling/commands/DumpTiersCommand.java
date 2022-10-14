package com.petey.harvest_leveling.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.petey.harvest_leveling.HarvestLeveling;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.TierSortingRegistry;

public class DumpTiersCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("harvestleveling")
                .requires((source) -> source.hasPermission(2))
                .then(Commands.literal("dump_tiers")
                        .executes(command -> {
                            TierSortingRegistry.getSortedTiers().forEach(tier -> {
                                HarvestLeveling.LOGGER.info(TierSortingRegistry.getName(tier).toString());
                            });
                            command.getSource().sendSuccess(new TextComponent("Registered tiers logged. Check latest.log"), true);
                            return 1;
                        })));
    }
}
