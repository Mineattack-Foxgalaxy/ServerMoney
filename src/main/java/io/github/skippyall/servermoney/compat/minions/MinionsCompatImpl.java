package io.github.skippyall.servermoney.compat.minions;

import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.minion.MinionPersistentState;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

class MinionsCompatImpl {
    static boolean isMinion(ServerPlayerEntity player) {
        return player instanceof MinionFakePlayer;
    }

    static boolean isMinion(UUID uuid) {
        return MinionPersistentState.INSTANCE.getMinionData().stream().anyMatch(data -> data.uuid == uuid);
    }
}
