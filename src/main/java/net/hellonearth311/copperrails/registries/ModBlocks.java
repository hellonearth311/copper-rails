package net.hellonearth311.copperrails.registries;

import net.hellonearth311.copperrails.CopperRails;
import net.hellonearth311.copperrails.registries.custom.block.CopperRail;
import net.hellonearth311.copperrails.registries.custom.block.OxidationLevel;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.hellonearth311.copperrails.registries.custom.block.PoweredCopperRail;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModBlocks {
    private static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory, AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        RegistryKey<Block> blockKey = keyOfBlock(name);
        Block block = blockFactory.apply(settings.registryKey(blockKey));

        if (shouldRegisterItem) {
            RegistryKey<Item> itemKey = keyOfItem(name);

            BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
            Registry.register(Registries.ITEM, itemKey, blockItem);
        }

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    private static RegistryKey<Block> keyOfBlock(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(CopperRails.MOD_ID, name));
    }

    private static RegistryKey<Item> keyOfItem(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(CopperRails.MOD_ID, name));
    }

    public static final Block COPPER_RAIL = register("copper_rail",
            settings -> new CopperRail(OxidationLevel.UNAFFECTED, settings),
            AbstractBlock.Settings.create()
                    .strength(0.7f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()
                    .noCollision(),
            true);

    public static final Block EXPOSED_COPPER_RAIL = register("exposed_copper_rail",
            settings -> new CopperRail(OxidationLevel.EXPOSED, settings),
            AbstractBlock.Settings.create()
                    .strength(0.7f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()
                    .noCollision(),
            true);

    public static final Block WEATHERED_COPPER_RAIL = register("weathered_copper_rail",
            settings -> new CopperRail(OxidationLevel.WEATHERED, settings),
            AbstractBlock.Settings.create()
                    .strength(0.7f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()
                    .noCollision(),
            true);

    public static final Block OXIDIZED_COPPER_RAIL = register("oxidized_copper_rail",
            settings -> new CopperRail(OxidationLevel.OXIDIZED, settings),
            AbstractBlock.Settings.create()
                    .strength(0.7f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()
                    .noCollision(),
            true);

    public static final Block POWERED_COPPER_RAIL = register("powered_copper_rail",
            settings -> new PoweredCopperRail(OxidationLevel.UNAFFECTED, settings),
            AbstractBlock.Settings.create()
                    .strength(0.7f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()
                    .noCollision(),
            true);

    public static final Block EXPOSED_POWERED_COPPER_RAIL = register("exposed_powered_copper_rail",
            settings -> new PoweredCopperRail(OxidationLevel.EXPOSED, settings),
            AbstractBlock.Settings.create()
                    .strength(0.7f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()
                    .noCollision(),
            true);

    public static final Block WEATHERED_POWERED_COPPER_RAIL = register("weathered_powered_copper_rail",
            settings -> new PoweredCopperRail(OxidationLevel.WEATHERED, settings),
            AbstractBlock.Settings.create()
                    .strength(0.7f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()
                    .noCollision(),
            true);

    public static final Block OXIDIZED_POWERED_COPPER_RAIL = register("oxidized_powered_copper_rail",
            settings -> new PoweredCopperRail(OxidationLevel.OXIDIZED, settings),
            AbstractBlock.Settings.create()
                    .strength(0.7f)
                    .sounds(BlockSoundGroup.METAL)
                    .nonOpaque()
                    .noCollision(),
            true);

    public static void initializeBlocks() {
    }
}
