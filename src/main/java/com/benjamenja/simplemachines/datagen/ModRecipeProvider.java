package com.benjamenja.simplemachines.datagen;

import com.benjamenja.simplemachines.block.ModBlocks;
import com.benjamenja.simplemachines.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        // Machine Casing
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModBlocks.MACHINE_CASING)
                .pattern("iii")
                .pattern("isi")
                .pattern("iii")
                .input('i', Items.IRON_INGOT)
                .input('s', ModItemTagProvider.STONES)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion("has_stones", conditionsFromTag(ModItemTagProvider.STONES))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, ModItems.IRON_ROD)
                .pattern("i")
                .pattern("i")
                .pattern("i")
                .input('i', Items.IRON_INGOT)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter);
    }
}
