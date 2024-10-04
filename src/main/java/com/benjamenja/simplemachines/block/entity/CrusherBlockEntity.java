package com.benjamenja.simplemachines.block.entity;

import com.benjamenja.simplemachines.SimpleMachines;
import com.benjamenja.simplemachines.block.ModBlockEntities;
import com.benjamenja.simplemachines.network.BlockPosPayload;
import com.benjamenja.simplemachines.screenhandler.CrusherScreenHandler;
import com.benjamenja.simplemachines.util.TickableBlockEntity;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.HashMap;
import java.util.Map;

public class CrusherBlockEntity extends BlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory<BlockPosPayload> {

    public static final Text TITLE = Text.translatable("container." + SimpleMachines.MOD_ID + ".crusher");

    private final Map<Item, ItemStack> recipes = new HashMap<>();

    private final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public void markDirty() {
            super.markDirty();
            update();
        }
    };

    private final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(10000, 100, 0) {
        @Override
        protected void onFinalCommit() {
            super.onFinalCommit();
            markDirty();
            update();
        }
    };

    private final InventoryStorage inventoryStorage = InventoryStorage.of(inventory, null);

    public CrusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRUSHER_BLOCK_ENTITY, pos, state);
        recipes.put(Items.COBBLESTONE, Items.GRAVEL.getDefaultStack());
        recipes.put(Items.GRAVEL, Items.SAND.getDefaultStack());
        recipes.put(Items.STONE, Items.COBBLESTONE.getDefaultStack());
        recipes.put(Items.DEEPSLATE, Items.COBBLED_DEEPSLATE.getDefaultStack());
        recipes.put(Items.IRON_ORE, new ItemStack(Items.RAW_IRON, 2));
        recipes.put(Items.DEEPSLATE_IRON_ORE, new ItemStack(Items.RAW_IRON, 2));
        recipes.put(Items.COAL_ORE, new ItemStack(Items.COAL, 3));
        recipes.put(Items.DEEPSLATE_COAL_ORE, new ItemStack(Items.COAL, 3));
    }

    private void update() {
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    public SimpleInventory getInventory() {
        return this.inventory;
    }

    public InventoryStorage getInventoryProvider(Direction direction) {
        return inventoryStorage;
    }

    public SimpleEnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    public SimpleEnergyStorage getEnergyProvider(Direction direction) {
        return this.energyStorage;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);

        var inventoryNbt = new NbtCompound();
        Inventories.writeNbt(inventoryNbt, this.inventory.getHeldStacks(), registryLookup);
        nbt.put("Inventory", inventoryNbt);

        nbt.putLong("Energy", this.energyStorage.amount);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("Inventory", NbtElement.COMPOUND_TYPE)) {
            Inventories.readNbt(nbt.getCompound("Inventory"), this.inventory.getHeldStacks(), registryLookup);
        }

        if (nbt.contains("Energy", NbtElement.LONG_TYPE)) {
            energyStorage.amount = nbt.getLong("Energy");
        }
    }

    @Override
    public void tick() {
        if (this.world == null || this.world.isClient) {
            return;
        }
        if (this.inventory.isEmpty()) {
            return;
        }

        Item ingredient = this.inventory.getStack(0).getItem();
        if (this.energyStorage.amount >= 200 && this.recipes.get(ingredient) != null) {
            this.inventory.removeStack(0, 1);
            this.energyStorage.amount -= 200;
            world.spawnEntity(new ItemEntity(world, getPos().getX(), getPos().getY() + 1, getPos().getZ(), this.recipes.get(ingredient)));
            markDirty();
            update();
        }
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        var nbt = super.toInitialChunkDataNbt(registryLookup);
        writeNbt(nbt, registryLookup);
        return nbt;
    }

    @Override
    public BlockPosPayload getScreenOpeningData(ServerPlayerEntity player) {
        return new BlockPosPayload(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CrusherScreenHandler(syncId, playerInventory, this);
    }
}
