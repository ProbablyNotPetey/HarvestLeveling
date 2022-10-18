package com.petey.harvest_leveling.config;

import com.petey.harvest_leveling.HarvestLevelTier;
import com.petey.harvest_leveling.HarvestLeveling;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> itemLevelOverride;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> customTiers;

    public static Set<Element> ITEM_LEVEL_SET;
    public static Set<Element> TIER_AFTER_SET;
    public static Set<HarvestLevelTier> CUSTOM_TIER_SET;

    /**
     * A config element that has a string name and a value. The two should be split by a ;
     * Example: "minecraft:iron_pickaxe;value"
     */
    public static class Element {

        private final String value;
        private final String name;

        public Element(String input) {
            this(input, ';');
        }

        private Element(String input, char separator) {
            int separatorIndex = input.indexOf(separator);
            if(separatorIndex == -1) {
                throw new IllegalArgumentException("String " + input + " is not formatted properly!");
            }
            name = input.substring(0, separatorIndex);
            value = input.substring(separatorIndex + 1);
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return name;
        }
    }

    //Returns false if either the name or value are not resource locations
    private static final Predicate<Object> rLoc2 = object -> {
        if(!(object instanceof String)) return false;
        String str = (String) object;
        try {
            Element e = new Element(str);
            return ResourceLocation.isValidResourceLocation(e.getName()) && ResourceLocation.isValidResourceLocation(e.getValue());
        } catch(IllegalArgumentException e) {
            HarvestLeveling.LOGGER.error("Error reading config: String " + str + " is not correct format, skipping...");
            return false;
        }
    };
    //Returns false if the value isn't a resource location
    private static final Predicate<Object> stringRLoc = object -> {
        if(!(object instanceof String)) return false;
        String str = (String) object;
        try {
            return ResourceLocation.isValidResourceLocation(new Element(str).getValue());
        } catch(IllegalArgumentException e) {
            HarvestLeveling.LOGGER.error("Error reading config: String " + str + " is not correct format, skipping...");
            return false;
        }
    };

    static {

        BUILDER.comment("Mod configuration. Format should be thing;value. So for example: 'minecraft:wooden_pickaxe;minecraft:iron'").push("Configuration");

        itemLevelOverride = BUILDER.comment("A list of items and what tier they should use for their mining level. Format should be modid:item;modid:tier. Only works for items that extend DiggerItem. To find all valid tiers, run /harvestleveling dump_tiers")
                .defineList("itemLevelOverride", Arrays.asList(new String[0]), rLoc2);
        customTiers = BUILDER.comment("A list of custom tiers and what tier it should be placed after. Format should be name;modid:tier. Restart required")
                .defineList("customTiers", Arrays.asList(new String[0]), stringRLoc);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void cacheValues() {

        ITEM_LEVEL_SET = new HashSet<>();
        for(String s : itemLevelOverride.get()) {
            //Only add to set if both name and value are resource locations. Might be redundant to check this like twice but idk weird edge case somewhere probably
            if(rLoc2.test(s)) ITEM_LEVEL_SET.add(new Element(s));
        }
    }

    public static void registerTiers() {
        HarvestLeveling.LOGGER.info("Registering tiers!");
        TIER_AFTER_SET = new HashSet<>();
        for(String s : customTiers.get()) {
            if(stringRLoc.test(s)) {
                TIER_AFTER_SET.add(new Element(s));
            }
        }
        CUSTOM_TIER_SET = new HashSet<>();
        TIER_AFTER_SET.forEach(element -> {
            Tier after = TierSortingRegistry.byName(new ResourceLocation(element.getValue()));
            CUSTOM_TIER_SET.add(new HarvestLevelTier(element.getName(), after, BlockTags.create(new ResourceLocation(HarvestLeveling.MOD_ID, "needs_" + element.getName() + "_tool"))));
        });
    }

    @SubscribeEvent
    public void onModConfigEvent(final ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == ModConfig.SPEC) {
            ModConfig.cacheValues();
        }
    }

    @SubscribeEvent
    public void onLoad(final ModConfigEvent.Loading event) {
        HarvestLeveling.LOGGER.info("Config loaded!");
        ModConfig.cacheValues();
    }

    @SubscribeEvent
    public void onReload(final ModConfigEvent.Reloading event) {
        HarvestLeveling.LOGGER.info("Config reloaded!");
        ModConfig.cacheValues();
    }
}
