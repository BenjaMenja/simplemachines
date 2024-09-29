package com.benjamenja.simplemachines;

import com.benjamenja.simplemachines.screen.FurnaceGeneratorScreen;
import com.benjamenja.simplemachines.screen.WasherScreen;
import com.benjamenja.simplemachines.screenhandler.ModScreenHandlerTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class SimpleMachinesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.

		HandledScreens.register(ModScreenHandlerTypes.FURNACE_GENERATOR, FurnaceGeneratorScreen::new);
		HandledScreens.register(ModScreenHandlerTypes.WASHER, WasherScreen::new);
	}
}