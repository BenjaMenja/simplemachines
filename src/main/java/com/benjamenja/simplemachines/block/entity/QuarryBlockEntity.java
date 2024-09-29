package com.benjamenja.simplemachines.block.entity;

import com.benjamenja.simplemachines.SimpleMachines;
import com.benjamenja.simplemachines.block.ModBlockEntities;
import com.benjamenja.simplemachines.util.TickableBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.ArrayList;
import java.util.List;

public class QuarryBlockEntity extends BlockEntity implements TickableBlockEntity {

    private int ticks = 0;
    private BlockPos miningPos = this.pos.down();

    public QuarryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.QUARRY_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        var modidData = nbt.getCompound(SimpleMachines.MOD_ID);
        this.ticks = modidData.contains("ticks", NbtElement.INT_TYPE) ? modidData.getInt("ticks") : 0;
        this.miningPos = modidData.contains("miningPos", NbtElement.LONG_TYPE) ? BlockPos.fromLong(modidData.getLong("miningPos")) : new BlockPos(0,0,0);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        var modidData = new NbtCompound();
        modidData.putInt("ticks", this.ticks);
        modidData.putLong("miningPos", this.miningPos.asLong());
    }

    public BlockPos getMiningPos() {
        return this.miningPos;
    }

    @Override
    public void tick() {
        if (this.world == null || this.world.isClient) return;
        if (this.ticks++ % 20 == 0) {
            if (this.miningPos.getY() <= this.world.getBottomY()) {
                this.miningPos = this.pos.down();
            }

            BlockState state = this.world.getBlockState(this.miningPos);
            if (state.isAir() || state.getHardness(this.world, this.miningPos) < 0) {
                this.miningPos = this.miningPos.down();
                return;
            }

            List<ItemStack> drops = new ArrayList<> (state.getDroppedStacks(new LootContextParameterSet.Builder((ServerWorld) this.world)
                    .add(LootContextParameters.TOOL, Items.DIAMOND_PICKAXE.getDefaultStack())
                    .add(LootContextParameters.ORIGIN, this.miningPos.toCenterPos())
                    .addOptional(LootContextParameters.BLOCK_ENTITY, this)));

            this.world.breakBlock(this.miningPos, false);

            Storage<ItemVariant> storage = findItemStorage((ServerWorld) this.world, this.pos.up());
            if (storage != null && storage.supportsInsertion()) {
                insertDrops(drops, storage);
            }

            if (!drops.isEmpty()) {
                spawnDrops(drops, (ServerWorld) this.world, this.pos);
            }

            this.miningPos = this.miningPos.down();
        }
    }

    private static Storage<ItemVariant> findItemStorage(ServerWorld world, BlockPos pos) {
        return ItemStorage.SIDED.find(world, pos, Direction.DOWN);
    }

    private static void spawnDrops(List<ItemStack> drops, ServerWorld world, BlockPos pos) {
        for (ItemStack drop : drops) {
            ItemScatterer.spawn(world, pos.getX() + 0.5D, pos.getY() + 1.00, pos.getZ() + 0.5D, drop);
        }
    }

    private static void insertDrops(List<ItemStack> drops, Storage<ItemVariant> storage) {
        for (ItemStack drop : drops) {
            try(Transaction transaction = Transaction.openOuter()) {
                long inserted = storage.insert(ItemVariant.of(drop), drop.getCount(), transaction);
                if (inserted > 0) {
                    drop.decrement((int) inserted);
                    transaction.commit();
                }
            }
        }

        drops.removeIf(ItemStack::isEmpty);
    }
}
