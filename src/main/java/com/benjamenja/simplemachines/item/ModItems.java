package com.benjamenja.simplemachines.item;

import com.benjamenja.simplemachines.SimpleMachines;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {

    public static final Item IRON_ROD = register("iron_rod", new Item(new Item.Settings()));

    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, SimpleMachines.id(name), item);
    }

    public static void initialize() {

    }
}
