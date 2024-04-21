package io.github.skippyall.servermoney;

//import eu.midnightdust.lib.config.MidnightConfig;
import io.github.skippyall.servermoney.commands.MoneyCommand;
import io.github.skippyall.servermoney.commands.ShopCommand;
import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import io.github.skippyall.servermoney.input.InputAttachment;
import io.github.skippyall.servermoney.money.MoneyStorage;
import io.github.skippyall.servermoney.paybutton.PayButtonBlock;
import io.github.skippyall.servermoney.paybutton.PayButtonBlockEntity;
import io.github.skippyall.servermoney.shop.BreakShopEvent;
import io.github.skippyall.servermoney.shop.OpenShopEvent;
import io.github.skippyall.servermoney.shop.ShopAttachment;
import io.github.skippyall.servermoney.shop.ShopResendCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.TimerCallbackSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMoney implements ModInitializer {
    public static final String MOD_ID = "servermoney";
    public static final Logger LOGGER = LoggerFactory.getLogger("Server Money");

    public static final PayButtonBlock PAY_BUTTON_BLOCK = Registry.register(Registries.BLOCK, new Identifier(MOD_ID, "pay_button"), new PayButtonBlock());
    public static final BlockEntityType<PayButtonBlockEntity> PAY_BUTTON_BET = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "pay_button"), BlockEntityType.Builder.create(PayButtonBlockEntity::new, PAY_BUTTON_BLOCK).build());
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(new MoneyCommand());
        CommandRegistrationCallback.EVENT.register(new ShopCommand());

        UseBlockCallback.EVENT.register(new OpenShopEvent());
        PlayerBlockBreakEvents.BEFORE.register(new BreakShopEvent());
        TimerCallbackSerializer.INSTANCE.registerSerializer(new ShopResendCallback.ShopResendSerializer());
        ShopAttachment.register();
        InputAttachment.register();

        ServerLifecycleEvents.SERVER_STARTED.register(MoneyStorage::init);

        //MidnightConfig.init(MOD_ID, ServerMoneyConfig.class);
    }
}
