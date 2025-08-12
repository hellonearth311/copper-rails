package net.hellonearth311.copperrails;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.hellonearth311.copperrails.registries.ModBlocks;
import net.minecraft.client.render.RenderLayer;

public class CopperRailsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COPPER_RAIL, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.POWERED_COPPER_RAIL, RenderLayer.getCutout());
	}
}