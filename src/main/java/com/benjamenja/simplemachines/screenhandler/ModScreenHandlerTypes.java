package com.benjamenja.simplemachines.screenhandler;

import com.benjamenja.simplemachines.SimpleMachines;
import com.benjamenja.simplemachines.network.BlockPosPayload;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class ModScreenHandlerTypes {

    public static final ScreenHandlerType<FurnaceGeneratorScreenHandler> FURNACE_GENERATOR = register("furnace_generator", FurnaceGeneratorScreenHandler::new, BlockPosPayload.PACKET_CODEC);
    public static final ScreenHandlerType<WasherScreenHandler> WASHER = register("washer", WasherScreenHandler::new, BlockPosPayload.PACKET_CODEC);

    public static <T extends ScreenHandler, P extends CustomPayload> ExtendedScreenHandlerType<T, P> register(String name, ExtendedScreenHandlerType.ExtendedFactory<T, P> factory, PacketCodec<? super RegistryByteBuf, P> codec) {
        return Registry.register(Registries.SCREEN_HANDLER, SimpleMachines.id("name"), new ExtendedScreenHandlerType<>(factory, codec));
    }

    public static void initialize() {

    }

}
