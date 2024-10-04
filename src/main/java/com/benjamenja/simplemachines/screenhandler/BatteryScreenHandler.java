package com.benjamenja.simplemachines.screenhandler;

import com.benjamenja.simplemachines.block.ModBlocks;
import com.benjamenja.simplemachines.block.entity.BatteryBlockEntity;
import com.benjamenja.simplemachines.network.BlockPosPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.MathHelper;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class BatteryScreenHandler extends ScreenHandler {

    private final BatteryBlockEntity blockEntity;
    private final ScreenHandlerContext context;

    // Client Constructor
    public BatteryScreenHandler(int syncId, PlayerInventory playerInventory, BlockPosPayload payload) {
        this(syncId, playerInventory, (BatteryBlockEntity) playerInventory.player.getWorld().getBlockEntity(payload.pos()));
    }

    // Server (Main) Constructor
    public BatteryScreenHandler(int syncId, PlayerInventory playerInventory, BatteryBlockEntity batteryBlockEntity) {
        super(ModScreenHandlerTypes.BATTERY, syncId);
        this.blockEntity = batteryBlockEntity;
        this.context = ScreenHandlerContext.create(this.blockEntity.getWorld(), this.blockEntity.getPos());

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public BatteryBlockEntity getBlockEntity() {
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

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, ModBlocks.BATTERY);
    }
}
