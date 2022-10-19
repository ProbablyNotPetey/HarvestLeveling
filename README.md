# HarvestLeveling
Simple Minecraft mod for pack developers, allowing them to override a tool's harvest level.

What this mod does do:

- Manual override of a tool's harvest level to any tier registered in Forge's TierSortingRegistry
- Allow for registering custom harvest levels via config
- Provides a command to dump tiers registered in TierSortingRegistry

What this mod *doesn't* do

- Override of block's harvest level requirements (use vanilla/forge tags for this)
- Harvest levels for non tools (item must extend DiggerItem)
- Modify mining speeds of tools (use [Property Modifier](https://www.curseforge.com/minecraft/mc-mods/property-modifier) or a similar mod)

## How to Use

1. Open `harvest_leveling-common.toml`
2. In the first list add any tools you want to override the harvest level for. Syntax should be `"modid:item;modid:tier"`
3. In the second list add any custom harvest levels you want. Syntax should be `"name;modid:tier"` with the tier being the tier that this custom harvest level comes immediately after. Restart required.


## Issues/Contributing

This mod *probably* won't be incompatible with anything, but if a mod modifies mining level logic then it may be incompatible. Please report incompatibilities in the [Issue Tracker](https://github.com/ProbablyNotPetey/HarvestLeveling/issues).

If you wish to contribute just make a PR and I'll take a look.
