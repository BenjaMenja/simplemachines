package com.benjamenja.simplemachines.datagen;

import com.benjamenja.simplemachines.SimpleMachines;
import com.benjamenja.simplemachines.block.ModBlocks;
import com.benjamenja.simplemachines.block.entity.*;
import com.benjamenja.simplemachines.item.ModItems;
import com.benjamenja.simplemachines.itemgroup.ModItemGroups;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModLangProvider extends FabricLanguageProvider {

    public ModLangProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    private static void addText(@NotNull TranslationBuilder builder, @NotNull Text text, @NotNull String value) {
        if (text.getContent() instanceof TranslatableTextContent translatableTextContent) {
            builder.add(translatableTextContent.getKey(), value);
        } else {
            SimpleMachines.LOGGER.warn("Failed to add translation for text: {}", text.getString());
        }
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(ModBlocks.WASHER, "Washer");
        translationBuilder.add(ModBlocks.QUARRY, "Quarry");
        translationBuilder.add(ModBlocks.BATTERY, "Battery");
        translationBuilder.add(ModBlocks.FURNACE_GENERATOR, "Furnace Generator");
        translationBuilder.add(ModBlocks.MAGMA_GENERATOR, "Magma Generator");
        translationBuilder.add(ModBlocks.MACHINE_CASING, "Machine Casing");
        translationBuilder.add(ModBlocks.CRUSHER, "Crusher");

        translationBuilder.add(ModItems.IRON_ROD, "Iron Rod");

        addText(translationBuilder, FurnaceGeneratorBlockEntity.TITLE, "Furnace Generator");
        addText(translationBuilder, MagmaGeneratorBlockEntity.TITLE, "Magma Generator");
        addText(translationBuilder, WasherBlockEntity.TITLE, "Washer");
        addText(translationBuilder, CrusherBlockEntity.TITLE, "Crusher");
        addText(translationBuilder, ModItemGroups.TITLE, "Simple Machines");
        addText(translationBuilder, BatteryBlockEntity.TITLE, "Battery");
    }
}
