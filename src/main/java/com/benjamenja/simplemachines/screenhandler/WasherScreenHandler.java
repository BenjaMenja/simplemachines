package com.benjamenja.simplemachines.screenhandler;

import com.benjamenja.simplemachines.block.ModBlocks;
import com.benjamenja.simplemachines.block.entity.WasherBlockEntity;
import com.benjamenja.simplemachines.network.BlockPosPayload;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.SingleFluidStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.MathHelper;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class WasherScreenHandler extends ScreenHandler {

    private final WasherBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    public WasherScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (WasherBlockEntity) playerInventory.player.getWorld().getBlockEntity(payload.pos()));
    }

    public WasherScreenHandler(int syncId, PlayerInventory playerInventory, WasherBlockEntity washerBlockEntity) {
        super(ModScreenHandlerTypes.WASHER, syncId);
        this.blockEntity = washerBlockEntity;
        this.context = ScreenHandlerContext.create(this.blockEntity.getWorld(), this.blockEntity.getPos());

        SimpleInventory inventory = this.blockEntity.getInventory();
        checkSize(inventory, 2);
        inventory.onOpen(playerInventory.player);

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);
        addBlockInventory(inventory);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = getSlot(slotIndex);
        if (slot != null && slot.hasStack()) {
            ItemStack inSlot = slot.getStack();
            newStack = inSlot.copy();
            if (slotIndex < this.blockEntity.getInventory().size()) {
                if (!insertItem(inSlot, this.blockEntity.getInventory().size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                else if (!insertItem(inSlot, 0, this.blockEntity.getInventory().size(), false)) {
                    return ItemStack.EMPTY;
                }

                if (inSlot.isEmpty()) {
                    slot.setStack(ItemStack.EMPTY);
                }
                else {
                    slot.markDirty();
                }
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, ModBlocks.WASHER);
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.blockEntity.getInventory().onClose(player);
    }

    public WasherBlockEntity getBlockEntity() {
        return this.blockEntity;
    }

    public long getEnergy() {
        return this.blockEntity.getEnergyStorage().getAmount();
    }

    public long getMaxEnergy() {
        return this.blockEntity.getEnergyStorage().getCapacity();
    }

    public float getEnergyPercent() {
        SimpleEnergyStorage energyStorage = this.blockEntity.getEnergyStorage();
        long energy = energyStorage.getAmount();
        long maxEnergy = energyStorage.getCapacity();
        if (maxEnergy == 0 || energy == 0) {
            return 0.0f;
        }
        return MathHelper.clamp((float) energy / (float) maxEnergy, 0.0f, 1.0f);
    }

    public long getFluidAmount() {
        return this.blockEntity.getFluidTankProvider(null).getAmount();
    }

    public long getMaxCapacity() {
        return this.blockEntity.getFluidTankProvider(null).getCapacity();
    }

    public float getFluidPercent() {
        SingleFluidStorage fluidStorage = this.blockEntity.getFluidTankProvider(null);
        long amount = fluidStorage.getAmount();
        long capacity = fluidStorage.getCapacity();
        if (capacity == 0 || amount == 0) {
            return 0.0f;
        }
        return MathHelper.clamp((float) amount / (float) capacity, 0.0f, 1.0f);
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 8 + (column * 18), 142));
        }
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, 9 + (col + (row * 9)), 8 + (col * 18), 84 + (row * 18)));
            }
        }
    }

    private void addBlockInventory(SimpleInventory inventory) {
        addSlot(new Slot(inventory, 0, 80, 53) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return inventory.isValid(0, stack);
            }
        });
        addSlot(new Slot(inventory, 1, 80, 17));
    }
}
