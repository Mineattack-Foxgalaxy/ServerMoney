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
import io.github.skippyall.servermoney.input.InputAttachment;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class ShopCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("shop")
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
                        .then(literal("item")
                                .then(argument("item", ItemStackArgumentType.itemStack(access))
                                        .executes(ShopCommand::modifyItem)
                                )
                                .executes(ShopCommand::modifyItemHand)

                        )
                        .requires(Permissions.require(ServerMoney.MOD_ID+".shop.modify", true))
                )
        );
    }

    public static int modifyPrice(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        double price = DoubleArgumentType.getDouble(context, "price");

        if(InputAttachment.hasInputType(player, Input.InputType.PRICE)) {
            InputAttachment.getCompletableFuture(player, Input.InputType.PRICE).complete(price);
        } else {
            Input.selectShop(player, new ShopModification().modifyPrice(price).addPredicate(be -> be.getShop().getShopOwner().equals(player.getGameProfile().getId())));
        }
        return 1;
    }

    public static int modifyAmount(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        int amount = IntegerArgumentType.getInteger(context, "amount");

        if(InputAttachment.hasInputType(player, Input.InputType.AMOUNT)) {
            InputAttachment.getCompletableFuture(player, Input.InputType.AMOUNT).complete(amount);
        } else {
            Input.selectShop(player, new ShopModification().modifyAmount(amount).addPredicate(be -> be.getShop().getShopOwner().equals(player.getGameProfile().getId())));
        }
        return 1;
    }

    public static int modifyItem(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ItemStack item = ItemStackArgumentType.getItemStackArgument(context, "item").createStack(1, false);
        modifyItem(player, item);
        return 1;
    }

    public static int modifyItemHand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        ItemStack item = player.getMainHandStack().copy();
        modifyItem(player, item);
        return 1;
    }

    public static void modifyItem(ServerPlayerEntity player, ItemStack stack) {
        if(InputAttachment.hasInputType(player, Input.InputType.ITEM)) {
            InputAttachment.getCompletableFuture(player, Input.InputType.ITEM).complete(ItemVariant.of(stack));
        } else {
            Input.selectShop(player, new ShopModification().modifyItem(ItemVariant.of(stack)).addPredicate(be -> be.getShop().getShopOwner().equals(player.getGameProfile().getId())));
        }
    }
}
