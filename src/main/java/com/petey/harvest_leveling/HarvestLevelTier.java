package com.petey.harvest_leveling;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.TierSortingRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Unlike normal tiers, a HarvestLevelTier is solely used to override another tool's harvest levels. In addition, all harvest level tiers are automatically registered into ${@link TierSortingRegistry}.
 */
public class HarvestLevelTier implements Tier {

    private final int harvestLevel;
    private final TagKey<Block> tag;

    //You really should be assigning a tag to a tier, but whatever.
    public HarvestLevelTier(String name, Tier after) {
        this(name, after, null);
    }

    public HarvestLevelTier(String name, Tier after, TagKey<Block> tag) {
        this.tag = tag;
        harvestLevel = after.getLevel() + 1; //May remove
        List<Tier> afterList = new ArrayList<>(TierSortingRegistry.getTiersLowerThan(after));
        afterList.add(after);
        List<Tier> beforeList = getTiersHigherThan(after);


        TierSortingRegistry.registerTier(this, new ResourceLocation(HarvestLeveling.MOD_ID, name), new ArrayList<Object>(afterList), new ArrayList<Object>(beforeList));
    }

    private List<Tier> getTiersHigherThan(Tier tier) {
        List<Tier> tiers = TierSortingRegistry.getSortedTiers();
        Collections.reverse(tiers);
        if (!TierSortingRegistry.isTierSorted(tier)) return List.of();
        return tiers.stream().takeWhile(t -> t != tier).toList();
    }

    @Override
    public int getUses() {
        return 0;
    }

    @Override
    public float getSpeed() {
        return 0.0f;
    }

    @Override
    public float getAttackDamageBonus() {
        return 0.0f;
    }

    @Override
    public int getLevel() {
        return this.harvestLevel;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }

    @Nullable
    @Override
    public TagKey<Block> getTag() {
        return tag;
    }
}
