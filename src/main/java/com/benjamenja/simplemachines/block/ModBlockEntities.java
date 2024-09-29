package com.benjamenja.simplemachines.block;

import com.benjamenja.simplemachines.SimpleMachines;
import com.benjamenja.simplemachines.block.entity.BatteryBlockEntity;
import com.benjamenja.simplemachines.block.entity.FurnaceGeneratorBlockEntity;
import com.benjamenja.simplemachines.block.entity.QuarryBlockEntity;
import com.benjamenja.simplemachines.block.entity.WasherBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModBlockEntities {
    public static final BlockEntityType<WasherBlockEntity> WASHER_BLOCK_ENTITY = register("washer_block_entity", BlockEntityType.Builder.create(WasherBlockEntity::new, ModBlocks.WASHER).build());
    public static final BlockEntityType<QuarryBlockEntity> QUARRY_BLOCK_ENTITY = register("quarry_block_entity", BlockEntityType.Builder.create(QuarryBlockEntity::new, ModBlocks.QUARRY).build());
    public static final BlockEntityType<BatteryBlockEntity> BATTERY_BLOCK_ENTITY = register("battery_block_entity", BlockEntityType.Builder.create(BatteryBlockEntity::new, ModBlocks.BATTERY).build());
    public static final BlockEntityType<FurnaceGeneratorBlockEntity> FURNACE_GENERATOR_BLOCK_ENTITY = register("furnace_generator_block_entity", BlockEntityType.Builder.create(FurnaceGeneratorBlockEntity::new, ModBlocks.FURNACE_GENERATOR).build());

    public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> type) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, SimpleMachines.id(name), type);
    }

    public static void initialize() {

    }
}
