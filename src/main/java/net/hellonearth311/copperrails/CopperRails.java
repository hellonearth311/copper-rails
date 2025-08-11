package net.hellonearth311.copperrails;

import net.fabricmc.api.ModInitializer;

import net.hellonearth311.copperrails.registries.ModBlocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CopperRails implements ModInitializer {
	public static final String MOD_ID = "copper-rails";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing CopperRails...");

		ModBlocks.initializeBlocks();

		LOGGER.info("Initialized CopperRails successfully!");
	}
}