package com.benjamenja.simplemachines.datagen;

import com.benjamenja.simplemachines.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;


public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.WASHER);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.QUARRY);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.BATTERY);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.FURNACE_GENERATOR);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {

    }
}
