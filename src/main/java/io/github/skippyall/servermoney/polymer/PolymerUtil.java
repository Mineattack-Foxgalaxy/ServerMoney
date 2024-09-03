package io.github.skippyall.servermoney.polymer;

import io.github.skippyall.servermoney.ServerMoney;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;

public class PolymerUtil {
    public static boolean shouldReplaceItem(ServerPlayerEntity player) {
        return shouldReplace(player) && FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT;
    }

    public static boolean shouldReplace(ServerPlayerEntity player) {
        return !(player != null && ServerPlayNetworking.canSend(player, ServerMoney.PACKET_ID));
    }
}
