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
     * A config element that has a resource location and a value. The two should be split by a ;
     * Example: "minecraft:iron_pickaxe;value"
     */
    public static class Element {

        private String value;
        private ResourceLocation loc;

        public Element(String input) {
            this(input, ';');
        }
        private Element(String input, char separator) {
            int separatorIndex = input.indexOf(separator);
            if(separatorIndex == -1) {
                throw new IllegalArgumentException("String " + input + " is not formatted properly!");
            }
            String rLocString = input.substring(0, separatorIndex);
            loc = ResourceLocation.tryParse(rLocString);
            if (loc == null) {
                throw new IllegalArgumentException(rLocString + " is not a valid ResourceLocation!");
            }
            value = input.substring(separatorIndex + 1);
        }

        public String getValue() {
            return value;
        }

        public ResourceLocation getLoc() {
            return loc;
        }
    }

    static {

        //Returns false if the value of the element is either not formatted right or the value is not a resource location.
        Predicate<Object> rLocValue = object -> {
            if(!(object instanceof String)) return false;
            String str = (String) object;
            try {
                return ResourceLocation.isValidResourceLocation(new Element(str).getValue());
            } catch(IllegalArgumentException e) {
                HarvestLeveling.LOGGER.error("Error reading config: String " + str + " is not correct format, skipping...");
                return false;
            }
        };


        BUILDER.comment("Mod configuration. Format should be item;value. So for example: 'minecraft:wooden_pickaxe;minecraft:iron'").push("Configuration");

        itemLevelOverride = BUILDER.comment("A list of items and what tier they should use for their mining level. Only works for items that extend DiggerItem. To find all valid tiers, run /harvestleveling dump_tiers")
                .defineList("itemLevelOverride", Arrays.asList(new String[0]), rLocValue);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static void cacheValues() {

        ITEM_LEVEL_SET = new HashSet<>();
        for(String s : itemLevelOverride.get()) {
            Element e = new Element(s);
            if(!ResourceLocation.isValidResourceLocation(e.getValue())) throw new IllegalArgumentException("Error reading config: " + e.getValue() + " is not a valid ResourceLocation!");
            ITEM_LEVEL_SET.add(e);
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
