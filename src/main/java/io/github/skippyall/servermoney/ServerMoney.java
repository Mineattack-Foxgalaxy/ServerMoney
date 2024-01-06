package io.github.skippyall.servermoney;

import io.github.skippyall.servermoney.commands.MoneyCommand;
import io.github.skippyall.servermoney.money.MoneyStorage;
import io.github.skippyall.servermoney.shop.OpenChestEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMoney implements ModInitializer {
    public static final String MONEY_SYMBOL = "€";
    public static final String MOD_ID = "servermoney";
    public static final Logger LOGGER = LoggerFactory.getLogger("Server Money");
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(new MoneyCommand());
        UseBlockCallback.EVENT.register(new OpenChestEvent());
        ServerLifecycleEvents.SERVER_STARTED.register(MoneyStorage::init);
    }
}
