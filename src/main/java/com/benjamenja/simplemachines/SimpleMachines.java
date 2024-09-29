package com.benjamenja.simplemachines;

import com.benjamenja.simplemachines.block.ModBlockEntities;
import com.benjamenja.simplemachines.block.ModBlocks;
import com.benjamenja.simplemachines.block.entity.BatteryBlockEntity;
import com.benjamenja.simplemachines.block.entity.FurnaceGeneratorBlockEntity;
import com.benjamenja.simplemachines.block.entity.WasherBlockEntity;
import com.benjamenja.simplemachines.item.ModItems;
import com.benjamenja.simplemachines.screenhandler.ModScreenHandlerTypes;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.reborn.energy.api.EnergyStorage;

public class SimpleMachines implements ModInitializer {
	public static final String MOD_ID = "simplemachines";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModItems.initialize();
		ModBlocks.initialize();
		ModBlockEntities.initialize();
		ModScreenHandlerTypes.initialize();
		EnergyStorage.SIDED.registerForBlockEntity(FurnaceGeneratorBlockEntity::getEnergyProvider, ModBlockEntities.FURNACE_GENERATOR_BLOCK_ENTITY);
		EnergyStorage.SIDED.registerForBlockEntity(BatteryBlockEntity::getEnergyProvider, ModBlockEntities.BATTERY_BLOCK_ENTITY);
		EnergyStorage.SIDED.registerForBlockEntity(WasherBlockEntity::getEnergyProvider, ModBlockEntities.WASHER_BLOCK_ENTITY);
		ItemStorage.SIDED.registerForBlockEntity(FurnaceGeneratorBlockEntity::getInventoryProvider, ModBlockEntities.FURNACE_GENERATOR_BLOCK_ENTITY);
		ItemStorage.SIDED.registerForBlockEntity(WasherBlockEntity::getInventoryProvider, ModBlockEntities.WASHER_BLOCK_ENTITY);
		FluidStorage.SIDED.registerForBlockEntity(WasherBlockEntity::getFluidTankProvider, ModBlockEntities.WASHER_BLOCK_ENTITY);
		LOGGER.info("Hello Fabric world!");
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}