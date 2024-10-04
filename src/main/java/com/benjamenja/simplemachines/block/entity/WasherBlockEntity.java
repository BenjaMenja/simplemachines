package com.benjamenja.simplemachines.block.entity;

import com.benjamenja.simplemachines.SimpleMachines;
import com.benjamenja.simplemachines.block.ModBlockEntities;
import com.benjamenja.simplemachines.network.BlockPosPayload;
import com.benjamenja.simplemachines.screenhandler.WasherScreenHandler;
import com.benjamenja.simplemachines.util.TickableBlockEntity;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
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

public class WasherBlockEntity extends BlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory<BlockPosPayload> {

    public static final Text TITLE = Text.translatable("container." + SimpleMachines.MOD_ID + ".washer");

    private final Map<Item, ItemStack> recipes = new HashMap<>();

    private final SimpleInventory inventory = new SimpleInventory(2) {
        @Override
        public void markDirty() {
            super.markDirty();
            update();
        }

        @Override
        public boolean isValid(int slot, ItemStack stack) {
            return WasherBlockEntity.this.isValid(stack, slot);
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

    private final ContainerItemContext fluidItemContext = ContainerItemContext.ofSingleSlot(this.inventoryStorage.getSlot(0));

    private final SingleFluidStorage fluidStorage = SingleFluidStorage.withFixedCapacity(FluidConstants.BUCKET * 10, this::update);

    public WasherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.WASHER_BLOCK_ENTITY, pos, state);
        recipes.put(Items.WHITE_CONCRETE_POWDER, Items.WHITE_CONCRETE.getDefaultStack());
        recipes.put(Items.BLACK_CONCRETE_POWDER, Items.BLACK_CONCRETE.getDefaultStack());
        recipes.put(Items.GRAY_CONCRETE_POWDER, Items.GRAY_CONCRETE.getDefaultStack());
        recipes.put(Items.LIGHT_GRAY_CONCRETE_POWDER, Items.LIGHT_GRAY_CONCRETE.getDefaultStack());
        recipes.put(Items.RED_CONCRETE_POWDER, Items.RED_CONCRETE.getDefaultStack());
        recipes.put(Items.BLUE_CONCRETE_POWDER, Items.RED_CONCRETE.getDefaultStack());
        recipes.put(Items.BROWN_CONCRETE_POWDER, Items.BROWN_CONCRETE.getDefaultStack());
        recipes.put(Items.ORANGE_CONCRETE_POWDER, Items.ORANGE_CONCRETE.getDefaultStack());
        recipes.put(Items.YELLOW_CONCRETE_POWDER, Items.YELLOW_CONCRETE.getDefaultStack());
        recipes.put(Items.LIME_CONCRETE_POWDER, Items.LIME_CONCRETE.getDefaultStack());
        recipes.put(Items.GREEN_CONCRETE_POWDER, Items.GREEN_CONCRETE.getDefaultStack());
        recipes.put(Items.CYAN_CONCRETE_POWDER, Items.CYAN_CONCRETE.getDefaultStack());
        recipes.put(Items.LIGHT_BLUE_CONCRETE_POWDER, Items.LIGHT_BLUE_CONCRETE.getDefaultStack());
        recipes.put(Items.PURPLE_CONCRETE_POWDER, Items.PURPLE_CONCRETE.getDefaultStack());
        recipes.put(Items.MAGENTA_CONCRETE_POWDER, Items.MAGENTA_CONCRETE.getDefaultStack());
        recipes.put(Items.PINK_CONCRETE_POWDER, Items.PINK_CONCRETE.getDefaultStack());
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

    public SingleFluidStorage getFluidTankProvider(Direction direction) {
        return this.fluidStorage;
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

        var fluidNbt = new NbtCompound();
        this.fluidStorage.writeNbt(fluidNbt, registryLookup);
        nbt.put("FluidTank", fluidNbt);

        nbt.putLong("Energy", this.energyStorage.amount);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains("Inventory", NbtElement.COMPOUND_TYPE)) {
            Inventories.readNbt(nbt.getCompound("Inventory"), this.inventory.getHeldStacks(), registryLookup);
        }

        if (nbt.contains("FluidTank", NbtElement.COMPOUND_TYPE)) {
            this.fluidStorage.readNbt(nbt.getCompound("FluidTank"), registryLookup);
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
        if (this.inventory.isEmpty() || !isValid(this.inventory.getStack(0), 0)) {
            return;
        }

        Item ingredient = this.inventory.getStack(1).getItem();
        if (this.energyStorage.amount >= 200 && this.fluidStorage.getAmount() >= FluidConstants.BUCKET / 10 && this.recipes.get(ingredient) != null) {
            this.inventory.removeStack(1, 1);
            this.fluidStorage.amount -= FluidConstants.BUCKET / 10;
            this.energyStorage.amount -= 200;
            world.spawnEntity(new ItemEntity(world, getPos().getX(), getPos().getY() + 1, getPos().getZ(), this.recipes.get(ingredient)));
            markDirty();
            update();
        }

        Storage<FluidVariant> itemFluidStorage = this.fluidItemContext.find(FluidStorage.ITEM);
        if (itemFluidStorage == null) {
            return;
        }

        FluidVariant match = null;
        for (StorageView<FluidVariant> storageView : itemFluidStorage.nonEmptyViews()) {
            if (storageView.isResourceBlank()) {
                continue;
            }

            try (Transaction transaction = Transaction.openOuter()) {
                if (this.fluidStorage.insert(storageView.getResource(), FluidConstants.BUCKET, transaction) > 0) {
                    match = storageView.getResource();
                    break;
                }
            }
        }

        if (match == null || match.isBlank()) {
            return;
        }

        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = this.fluidStorage.insert(match, FluidConstants.BUCKET, transaction);
            long extracted = itemFluidStorage.extract(match, inserted, transaction);
            if (extracted < FluidConstants.BUCKET) {
                long extra = FluidConstants.BUCKET - extracted;
                this.fluidStorage.extract(match, extra, transaction);
            }

            transaction.commit();
        }

        SimpleMachines.LOGGER.info("Fluid: {}, Amount: {}", this.fluidStorage.getResource().getRegistryEntry().getIdAsString(), this.fluidStorage.getAmount());
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
        return new WasherScreenHandler(syncId, playerInventory, this);
    }

    public boolean isValid(ItemStack stack, int slot) {
        if (stack.isEmpty()) {
            return true;
        }
        if (slot != 0) {
            return false;
        }
        Storage<FluidVariant> storage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
        return storage != null;
    }
}
