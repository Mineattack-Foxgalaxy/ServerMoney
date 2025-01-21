package io.github.skippyall.servermoney.compat.minions;

import io.github.skippyall.servermoney.ServerMoney;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class MinionsCompat {
    private static final boolean MINIONS_INSTALLED = FabricLoader.getInstance().isModLoaded("minions");

    public static boolean isMinion(ServerPlayerEntity player) {
        try {
            return MINIONS_INSTALLED && MinionsCompatImpl.isMinion(player);
        } catch (Throwable ex) {
            ServerMoney.LOGGER.error("Error in Minions compat:", ex);
            return false;
        }
    }

    public static boolean isMinion(UUID uuid) {
        try {
            return MINIONS_INSTALLED && MinionsCompatImpl.isMinion(uuid);
        } catch (Throwable ex) {
            ServerMoney.LOGGER.error("Error in Minions compat:", ex);
            return false;
        }
    }
}
