package io.github.skippyall.servermoney.money;

import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class MoneyDistributor {
    public static void tick(MinecraftServer server) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            MoneyStorage.addMoney(player, ServerMoneyConfig.moneyPerTick);
        }
    }
}
