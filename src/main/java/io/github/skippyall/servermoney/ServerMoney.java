package io.github.skippyall.servermoney;

import io.github.skippyall.servermoney.commands.MoneyCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class ServerMoney implements ModInitializer {
    public static final String MONEY_SYMBOL = "€";
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> MoneyCommand.registerCommand(dispatcher));
    }
}
