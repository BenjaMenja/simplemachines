package com.benjamenja.simplemachines.item;

import com.benjamenja.simplemachines.SimpleMachines;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModItems {

    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, SimpleMachines.MOD_ID, item);
    }

    public static void initialize() {

    }
}
