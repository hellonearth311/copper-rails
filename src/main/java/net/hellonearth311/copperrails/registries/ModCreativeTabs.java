package net.hellonearth311.copperrails.registries;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.hellonearth311.copperrails.CopperRails;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModCreativeTabs {
    public static final RegistryKey<ItemGroup> COPPER_RAILS_TAB = RegistryKey.of(RegistryKeys.ITEM_GROUP,
            Identifier.of(CopperRails.MOD_ID, "copper_rails"));

    public static final ItemGroup COPPER_RAILS_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ModBlocks.COPPER_RAIL))
            .displayName(Text.translatable("itemgroup.copper-rails.copper_rails"))
            .entries((displayContext, entries) -> {
                entries.add(ModBlocks.COPPER_RAIL);
                entries.add(ModBlocks.POWERED_COPPER_RAIL);
            })
            .build();

    public static void registerCreativeTabs() {
        Registry.register(Registries.ITEM_GROUP, COPPER_RAILS_TAB, COPPER_RAILS_GROUP);
        CopperRails.LOGGER.info("Registered Creative Tabs for " + CopperRails.MOD_ID);
    }
}
