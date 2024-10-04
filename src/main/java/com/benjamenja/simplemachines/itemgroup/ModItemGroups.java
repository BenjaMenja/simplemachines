package com.benjamenja.simplemachines.itemgroup;

import com.benjamenja.simplemachines.SimpleMachines;
import com.benjamenja.simplemachines.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

import java.util.Optional;

public class ModItemGroups {
    public static final Text TITLE = Text.translatable("itemGroup." + SimpleMachines.MOD_ID + ".simple_machines");
    public static final ItemGroup SIMPLE_MACHINES_GROUP = register("simple_machines", FabricItemGroup.builder()
            .displayName(TITLE)
            .icon(ModBlocks.MAGMA_GENERATOR.asItem()::getDefaultStack)
            .entries((displayContext, entries) -> Registries.ITEM.getIds()
                    .stream()
                    .filter(key -> key.getNamespace().equals(SimpleMachines.MOD_ID))
                    .map(Registries.ITEM::getOrEmpty)
                    .map(Optional::orElseThrow)
                    .forEach(entries::add))
                    .build());

    public static <T extends ItemGroup> T register(String name, T itemGroup) {
        return Registry.register(Registries.ITEM_GROUP, SimpleMachines.id(name), itemGroup);
    }

    public static void initialize() {

    }
}
