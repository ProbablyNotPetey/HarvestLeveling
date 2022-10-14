package com.petey.harvest_leveling.mixin;

import com.petey.harvest_leveling.HarvestLeveling;
import com.petey.harvest_leveling.config.ModConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DiggerItem.class)
public abstract class DiggerItemMixin extends TieredItem {

    @Shadow @Final
    private TagKey<Block> blocks;

    public DiggerItemMixin(Tier pTier, Properties pProperties) {
        super(pTier, pProperties);
    }

    @Inject(method = "isCorrectToolForDrops(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At("HEAD"), cancellable = true, remap = false)
    public void mixinIsCorrectToolForDrops(ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if(state.is(blocks)) {
            ResourceLocation loc = stack.getItem().getRegistryName();
            ModConfig.ITEM_LEVEL_SET.forEach(element -> {
                if(element.getLoc().equals(loc)) {
                    Tier tier = TierSortingRegistry.byName(new ResourceLocation(element.getValue()));
                    //If the provided value read from config isn't valid/registered in TierSortingRegistry, it is skipped
                    if(tier == null) {
                        HarvestLeveling.LOGGER.error("Tier " + element.getValue() + " is either not valid or not registered in TierSortingRegistry!");
                    }
                    else {
                        //If stack is in config, check if it's correct tool as if its tier was the given tier, rather than its real tier.
                        cir.setReturnValue(TierSortingRegistry.isCorrectTierForDrops(tier, state));
                    }
                }
            });

        }
    }
}
