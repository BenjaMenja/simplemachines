package com.benjamenja.simplemachines.datagen;

import com.benjamenja.simplemachines.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.WASHER)
                .add(ModBlocks.QUARRY)
                .add(ModBlocks.BATTERY)
                .add(ModBlocks.FURNACE_GENERATOR)
                .add(ModBlocks.MAGMA_GENERATOR)
                .add(ModBlocks.MACHINE_CASING)
                .add(ModBlocks.CRUSHER);

        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(ModBlocks.WASHER)
                .add(ModBlocks.QUARRY)
                .add(ModBlocks.BATTERY)
                .add(ModBlocks.FURNACE_GENERATOR)
                .add(ModBlocks.MAGMA_GENERATOR)
                .add(ModBlocks.MACHINE_CASING)
                .add(ModBlocks.CRUSHER);
    }
}
