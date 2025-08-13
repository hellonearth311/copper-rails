package net.hellonearth311.copperrails;

import net.fabricmc.api.ModInitializer;

import net.hellonearth311.copperrails.registries.ModBlocks;
import net.hellonearth311.copperrails.registries.ModCreativeTabs;
import net.minecraft.item.HoneycombItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopperRails implements ModInitializer {
	public static final String MOD_ID = "copper-rails";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing CopperRails...");

		ModBlocks.initializeBlocks();
		ModCreativeTabs.registerCreativeTabs();

		// Register waxed to unwaxed block mappings for axe interactions
		registerWaxedBlocks();

		LOGGER.info("Initialized CopperRails successfully!");
	}

	private void registerWaxedBlocks() {
		HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_COPPER_RAIL, ModBlocks.COPPER_RAIL);
		HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_EXPOSED_COPPER_RAIL, ModBlocks.EXPOSED_COPPER_RAIL);
		HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_WEATHERED_COPPER_RAIL, ModBlocks.WEATHERED_COPPER_RAIL);
		HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_OXIDIZED_COPPER_RAIL, ModBlocks.OXIDIZED_COPPER_RAIL);

		HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_POWERED_COPPER_RAIL, ModBlocks.POWERED_COPPER_RAIL);
		HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_EXPOSED_POWERED_COPPER_RAIL, ModBlocks.EXPOSED_POWERED_COPPER_RAIL);
		HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_WEATHERED_POWERED_COPPER_RAIL, ModBlocks.WEATHERED_POWERED_COPPER_RAIL);
		HoneycombItem.WAXED_TO_UNWAXED_BLOCKS.get().put(ModBlocks.WAXED_OXIDIZED_POWERED_COPPER_RAIL, ModBlocks.OXIDIZED_POWERED_COPPER_RAIL);
	}
}