package com.benjamenja.simplemachines.datagen;

import com.benjamenja.simplemachines.SimpleMachines;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public static final TagKey<Item> STONES = TagKey.of(RegistryKeys.ITEM, SimpleMachines.id("stones"));

    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(STONES)
                .add(Items.STONE)
                .add(Items.GRANITE)
                .add(Items.ANDESITE)
                .add(Items.DIORITE)
                .add(Items.DEEPSLATE)
                .add(Items.TUFF);
    }
}
