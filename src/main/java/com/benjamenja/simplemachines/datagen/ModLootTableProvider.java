package com.benjamenja.simplemachines.datagen;

import com.benjamenja.simplemachines.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModLootTableProvider extends FabricBlockLootTableProvider {

    public ModLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ModBlocks.WASHER);
        addDrop(ModBlocks.QUARRY);
        addDrop(ModBlocks.BATTERY);
        addDrop(ModBlocks.FURNACE_GENERATOR);
    }
}
