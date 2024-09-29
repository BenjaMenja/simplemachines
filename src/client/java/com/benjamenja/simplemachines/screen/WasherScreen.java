package com.benjamenja.simplemachines.screen;

import com.benjamenja.simplemachines.SimpleMachines;
import com.benjamenja.simplemachines.screenhandler.WasherScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class WasherScreen extends HandledScreen<WasherScreenHandler> {

    private static final Identifier TEXTURE = SimpleMachines.id("textures/gui/container/washer.png");

    public WasherScreen(WasherScreenHandler handler, PlayerInventory inventory, Text title) {
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
        int energyBarSize = MathHelper.ceil(this.handler.getEnergyPercent() * 66);
        context.fill(this.x + 144, this.y + 10 + 66 - energyBarSize, this.x + 144 + 20, this.y + 10 + 66, 0xFFFF0025);
        int fluidBarSize = MathHelper.ceil(this.handler.getFluidPercent() * 66);
        context.fill(this.x + 16, this.y + 10 + 66 - fluidBarSize, this.x + 16 + 20, this.y + 10 + 66, 0xFF0000EE);
    }
}
