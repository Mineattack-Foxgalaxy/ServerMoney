package io.github.skippyall.servermoney.compat.minions;

import io.github.skippyall.minions.minion.MinionProfileUtils;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

class MinionsCompatImpl {
    static boolean isMinion(ServerPlayerEntity player) {
        return player instanceof MinionFakePlayer;
    }

    static boolean isMinion(UUID uuid) {
        return MinionProfileUtils.isMinion(uuid);
    }
}
