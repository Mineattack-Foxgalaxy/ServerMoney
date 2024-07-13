package io.github.skippyall.servermoney;

import eu.midnightdust.lib.config.MidnightConfig;
import io.github.skippyall.servermoney.coin.CoinItem;
import io.github.skippyall.servermoney.commands.MoneyCommand;
import io.github.skippyall.servermoney.commands.ShopCommand;
import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import io.github.skippyall.servermoney.input.InputAttachment;
import io.github.skippyall.servermoney.money.MoneyDistributor;
import io.github.skippyall.servermoney.money.MoneyStorage;
import io.github.skippyall.servermoney.shop.BreakShopEvent;
import io.github.skippyall.servermoney.shop.ShopResendCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.TimerCallbackSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMoney implements ModInitializer {
    public static final String MOD_ID = "servermoney";
    public static final Logger LOGGER = LoggerFactory.getLogger("Server Money");

    public static final CoinItem COIN_ITEM = Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "coin"), new CoinItem());

    @Override
    public void onInitialize() {
        MoneyBlocks.register();
        CommandRegistrationCallback.EVENT.register(new MoneyCommand());
        CommandRegistrationCallback.EVENT.register(new ShopCommand());

        PlayerBlockBreakEvents.BEFORE.register(new BreakShopEvent());
        TimerCallbackSerializer.INSTANCE.registerSerializer(new ShopResendCallback.ShopResendSerializer());
        InputAttachment.register();

        ServerLifecycleEvents.SERVER_STARTED.register(MoneyStorage::init);
        ServerTickEvents.END_SERVER_TICK.register(MoneyDistributor::tick);

        MidnightConfig.init(MOD_ID, ServerMoneyConfig.class);
    }
}
