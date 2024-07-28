package io.github.skippyall.servermoney.client;

import io.github.skippyall.servermoney.ServerMoney;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ServerMoneyClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ServerMoney.PACKET_ID, (payload, context) -> {});
    }
}
