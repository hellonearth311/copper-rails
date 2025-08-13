package net.hellonearth311.copperrails.registries;

import net.hellonearth311.copperrails.CopperRails;
import net.hellonearth311.copperrails.registries.custom.item.WaxerItem;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import java.util.function.Function;

public class ModItems {
    public static <T extends Item> T register(String name, Function<Item.Settings, T> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(CopperRails.MOD_ID, name));
        Item.Settings itemSettings = settings.registryKey(itemKey);
        T item = itemFactory.apply(itemSettings);
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    public static final Item WAXER = register(
            "waxer",
            WaxerItem::new,
            new Item.Settings().maxDamage(40).rarity(Rarity.COMMON)
    );

    public static void initializeModItems() {
        CopperRails.LOGGER.info("Registering items and item groups for CopperRails");
    }
}