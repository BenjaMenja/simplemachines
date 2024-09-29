package com.benjamenja.simplemachines.block;

import com.benjamenja.simplemachines.SimpleMachines;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static Block register(Block block, String name, boolean shouldRegisterItem) {
        Identifier id = Identifier.of(SimpleMachines.MOD_ID, name);

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, id, blockItem);
        }

        return Registry.register(Registries.BLOCK, id, block);
    }

    public static final Block WASHER = register(new Washer(AbstractBlock.Settings.create().strength(2.0F, 2.0F).requiresTool()), "washer", true);
    public static final Block QUARRY = register(new Quarry(AbstractBlock.Settings.create().strength(2.0F, 2.0F).requiresTool()), "quarry", true);
    public static final Block BATTERY = register(new Battery(AbstractBlock.Settings.create().strength(2.0F, 2.0F).requiresTool()), "battery", true);
    public static final Block FURNACE_GENERATOR = register(new FurnaceGenerator(AbstractBlock.Settings.create().strength(2.0F, 2.0F).requiresTool()), "furnace_generator", true);


    public static void initialize() {

    }
}
