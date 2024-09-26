package io.github.skippyall.servermoney.compat.minions;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class MinionsCompat {
    private static final boolean MINIONS_INSTALLED = FabricLoader.getInstance().isModLoaded("minions");

    public static boolean isMinion(ServerPlayerEntity player) {
        return MINIONS_INSTALLED && MinionsCompatImpl.isMinion(player);
    }

    public static boolean isMinion(UUID uuid) {
        return MINIONS_INSTALLED && MinionsCompatImpl.isMinion(uuid);
    }
}
