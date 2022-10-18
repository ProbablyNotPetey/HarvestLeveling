package com.petey.harvest_leveling.config;

import com.petey.harvest_leveling.HarvestLeveling;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
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

    public static Set<Element> ITEM_LEVEL_SET;

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

        BUILDER.comment("Mod configuration. Format should be item;value. So for example: 'minecraft:wooden_pickaxe;minecraft:iron'").push("Configuration");

        itemLevelOverride = BUILDER.comment("A list of items and what tier they should use for their mining level. Only works for items that extend DiggerItem. To find all valid tiers, run /harvestleveling dump_tiers")
                .defineList("itemLevelOverride", Arrays.asList(new String[0]), rLoc2);

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
