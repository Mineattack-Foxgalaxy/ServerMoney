package io.github.skippyall.servermoney.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.skippyall.servermoney.ServerMoney;
import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import io.github.skippyall.servermoney.input.Input;
import io.github.skippyall.servermoney.shop.modification.ShopModification;
import io.github.skippyall.servermoney.shop.modification.ShopModificationComponent;
import io.github.skippyall.servermoney.shop.modification.ShopModificationStorage;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class ShopCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("shop")
                .then(literal("create")
                        .then(argument("price", DoubleArgumentType.doubleArg())
                                .then(argument("item", ItemStackArgumentType.itemStack(access))
                                        .then(argument("amount", IntegerArgumentType.integer())
                                                .executes(ShopCommand::create)
                                        )
                                )
                        )
                        .requires(Permissions.require(ServerMoney.MOD_ID+".shop.create", true))
                )
                .then(literal("modify")
                        .then(literal("price")
                                .then(argument("price", DoubleArgumentType.doubleArg(0))
                                        .executes(ShopCommand::modifyPrice)
                                )
                        )
                        .then(literal("amount")
                                .then(argument("amount", IntegerArgumentType.integer(0))
                                        .executes(ShopCommand::modifyAmount)
                                )
                        )
                        .requires(Permissions.require(ServerMoney.MOD_ID+".shop.modify", true))
                )
                .then(literal("close")
                        .requires(source -> {
                            ServerPlayerEntity player = source.getPlayer();
                            if(player != null) {
                                ShopModificationComponent component = ShopModificationStorage.getData(player);
                                return component.getInputType() == Input.InputType.CLOSE;
                            }
                            return false;
                        })
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            ShopModificationComponent modification = ShopModificationStorage.getData(player);
                            if (modification.getInputType() == Input.InputType.CLOSE) {
                                modification.getCompletableFuture(Input.InputType.CLOSE).complete(null);
                                modification.reset();
                            }
                            return 0;
                        })
                )
        );
    }

    public static int create(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        UUID id = player.getGameProfile().getId();
        double price = DoubleArgumentType.getDouble(context, "price");
        Item item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        ShopModification modification = new ShopModification()
                .modifyIsShop(true)
                .modifyShopOwner(id)
                .modifyPrice(price)
                .modifyItem(item)
                .modifyAmount(amount)
                .addPredicate(shop -> !shop.isShop);
        player.sendMessage(Text.translatable("servermoney.command.shop.create",amount, item.toString(), price, ServerMoneyConfig.moneySymbol));
        Input.selectShop(player, modification);
        return 1;
    }

    public static int modifyPrice(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ShopModificationComponent component = ShopModificationStorage.getData(player);
        double price = DoubleArgumentType.getDouble(context, "price");
        if(component.getInputType() == Input.InputType.PRICE) {
            component.getCompletableFuture(Input.InputType.PRICE).complete(price);
        } else {
            Input.selectShop(player, new ShopModification().modifyPrice(price).addPredicate(shop -> shop.shopOwner.equals(player.getGameProfile().getId())));
        }
        return 1;
    }

    public static int modifyAmount(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ShopModificationComponent component = ShopModificationStorage.getData(player);
        int amount = IntegerArgumentType.getInteger(context, "amount");
        if(component.getInputType() == Input.InputType.AMOUNT) {
            component.getCompletableFuture(Input.InputType.AMOUNT).complete(amount);
        } else {
            Input.selectShop(player, new ShopModification().modifyAmount(amount).addPredicate(shop -> shop.shopOwner.equals(player.getGameProfile().getId())));
        }
        return 1;
    }
}
