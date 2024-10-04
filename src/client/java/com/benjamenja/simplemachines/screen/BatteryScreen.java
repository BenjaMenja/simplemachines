package com.benjamenja.simplemachines.screen;

import com.benjamenja.simplemachines.SimpleMachines;
import com.benjamenja.simplemachines.screenhandler.BatteryScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class BatteryScreen extends HandledScreen<BatteryScreenHandler> {

    private static final Identifier TEXTURE = SimpleMachines.id("textures/gui/container/empty_inventory.png");

    public BatteryScreen(BatteryScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();

        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        int energyBarSize = MathHelper.ceil(this.handler.getEnergyPercent() * 56);
        context.fill(this.x + 80, this.y + 10 + 56 - energyBarSize, this.x + 80 + 20, this.y + 10 + 56, 0xFFFF0025);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
        int energyBarSize = MathHelper.ceil(this.handler.getEnergyPercent() * 56);
        if (isPointWithinBounds(80, 10 + 56 - energyBarSize, 20, energyBarSize, mouseX, mouseY)) {
            context.drawTooltip(this.textRenderer, Text.literal(this.handler.getEnergy() + " / " + this.handler.getMaxEnergy() + " Energy"), mouseX, mouseY);
        }
    }
}
