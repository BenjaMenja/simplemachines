package com.benjamenja.simplemachines.block.entity;

import com.benjamenja.simplemachines.SimpleMachines;
import com.benjamenja.simplemachines.block.ModBlockEntities;
import com.benjamenja.simplemachines.network.BlockPosPayload;
import com.benjamenja.simplemachines.screenhandler.MagmaGeneratorScreenHandler;
import com.benjamenja.simplemachines.util.TickableBlockEntity;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
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
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

import java.util.HashMap;
import java.util.Map;

public class MagmaGeneratorBlockEntity extends BlockEntity implements TickableBlockEntity, ExtendedScreenHandlerFactory<BlockPosPayload> {

    public static final Text TITLE = Text.translatable("container." + SimpleMachines.MOD_ID + ".magma_generator");
    private int burnTicks = 0;
    private int fuelRate = 10;

    private final Map<Item, Integer> fuelMap = new HashMap<>(); // Item, Energy/Tick Value
    private final SimpleInventory inventory = new SimpleInventory(1) {
        @Override
        public void markDirty() {
            super.markDirty();
            update();
        }
    };
    private final SimpleEnergyStorage energyStorage = new SimpleEnergyStorage(100000, 0, 500) {
        @Override
        protected void onFinalCommit() {
            super.onFinalCommit();
            markDirty();
            update();
        }
    };

    private final InventoryStorage inventoryStorage = InventoryStorage.of(this.inventory, null);

    public MagmaGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAGMA_GENERATOR_BLOCK_ENTITY, pos, state);
        fuelMap.put(Items.LAVA_BUCKET, 40);
        fuelMap.put(Items.MAGMA_BLOCK, 20);
        fuelMap.put(Items.MAGMA_CREAM, 5);
    }

    @Override
    public void tick() {
        if (this.world == null || this.world.isClient) {
            return;
        }

        ItemStack itemStack = getInventory().getStack(0);

        if (this.burnTicks == 0 && fuelMap.get(itemStack.getItem()) != null) {
            this.burnTicks = 1000;
            this.fuelRate = fuelMap.get(itemStack.getItem());
            if (itemStack.isOf(Items.LAVA_BUCKET)) {
                this.inventory.setStack(0, new ItemStack(Items.BUCKET, 1));
            }
            else {
                this.inventory.removeStack(0, 1);
            }
        }

        if (this.burnTicks > 0 && energyStorage.amount < energyStorage.getCapacity()) {
            energyStorage.amount = MathHelper.clamp(energyStorage.amount + fuelRate, 0, energyStorage.getCapacity());
            this.burnTicks--;
            markDirty();
            update();
        }

        for (Direction direction : Direction.values()) {
            EnergyStorage storage = EnergyStorage.SIDED.find(this.world, this.pos.offset(direction), direction.getOpposite());
            if (storage != null && storage.supportsInsertion()) {
                try(Transaction transaction = Transaction.openOuter()) {
                    long insertable;
                    try (Transaction simulateTransaction = transaction.openNested()) {
                        insertable = storage.insert(Long.MAX_VALUE, simulateTransaction);
                    }
                    long extracted = this.energyStorage.extract(insertable, transaction);
                    long inserted = storage.insert(extracted, transaction);
                    if (extracted == inserted) {
                        transaction.commit();
                    }
                }
            }
        }
    }

    private void update() {
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, this.inventory.getHeldStacks(), registryLookup);
        if (nbt.contains("Energy", NbtElement.LONG_TYPE)) {
            energyStorage.amount = nbt.getLong("Energy");
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, this.inventory.getHeldStacks(), registryLookup);
        nbt.putLong("Energy", this.energyStorage.amount);
    }

    public SimpleEnergyStorage getEnergyStorage() {
        return this.energyStorage;
    }

    public SimpleEnergyStorage getEnergyProvider(Direction direction) {
        return this.energyStorage;
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
        return new MagmaGeneratorScreenHandler(syncId, playerInventory, this);
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

    public InventoryStorage getInventoryProvider(Direction direction) {
        return this.inventoryStorage;
    }

    public SimpleInventory getInventory() {
        return this.inventory;
    }
}
