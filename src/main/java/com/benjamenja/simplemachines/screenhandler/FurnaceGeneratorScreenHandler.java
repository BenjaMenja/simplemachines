package com.benjamenja.simplemachines.screenhandler;

import com.benjamenja.simplemachines.block.ModBlocks;
import com.benjamenja.simplemachines.block.entity.FurnaceGeneratorBlockEntity;
import com.benjamenja.simplemachines.network.BlockPosPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.MathHelper;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class FurnaceGeneratorScreenHandler extends ScreenHandler {

    private final FurnaceGeneratorBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    // Client Constructor
    public FurnaceGeneratorScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (FurnaceGeneratorBlockEntity) playerInventory.player.getWorld().getBlockEntity(payload.pos()));
    }

    // Server (Main) Constructor
    public FurnaceGeneratorScreenHandler(int syncId, PlayerInventory playerInventory, FurnaceGeneratorBlockEntity furnaceGeneratorBlockEntity) {
        super(ModScreenHandlerTypes.FURNACE_GENERATOR, syncId);
        this.blockEntity = furnaceGeneratorBlockEntity;
        this.context = ScreenHandlerContext.create(this.blockEntity.getWorld(), this.blockEntity.getPos());

        SimpleInventory inventory = this.blockEntity.getInventory();
        checkSize(inventory, 1);
        inventory.onOpen(playerInventory.player);

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        addBlockInventory(inventory);
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
        addSlot(new Slot(inventory, 0, 80, 49));
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.blockEntity.getInventory().onClose(player);
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
        return canUse(this.context, player, ModBlocks.FURNACE_GENERATOR);
    }

    public FurnaceGeneratorBlockEntity getBlockEntity() {
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

}
