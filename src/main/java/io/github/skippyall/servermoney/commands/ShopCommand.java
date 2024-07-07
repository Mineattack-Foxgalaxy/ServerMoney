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
                /*.then(literal("create")
                        .then(argument("price", DoubleArgumentType.doubleArg())
                                .then(argument("amount", IntegerArgumentType.integer())
                                        .then(argument("item", ItemStackArgumentType.itemStack(access))
                                                .executes(ShopCommand::create)
                                        )
                                        .executes(ShopCommand::createHand)
                                )
                                .executes(ShopCommand::createHandWithCount)
                        )
                        .requires(Permissions.require(ServerMoney.MOD_ID+".shop.create", true))
                )*/
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
                /*.then(literal("close")
                        .requires(source -> {
                            ServerPlayerEntity player = source.getPlayer();
                            if(player != null) {
                                return InputAttachment.hasInputType(player, Input.InputType.CLOSE);
                            }
                            return false;
                        })
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                            if (InputAttachment.hasInputType(player, Input.InputType.CLOSE)) {
                                InputAttachment.getCompletableFuture(player, Input.InputType.CLOSE).complete(null);
                                InputAttachment.removeScheduledInput(player);
                            }
                            return 0;
                        })
                )*/
        );
    }

    /*public static int create(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        double price = DoubleArgumentType.getDouble(context, "price");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        ItemStack item = ItemStackArgumentType.getItemStackArgument(context, "item").createStack(amount, false);

        create(player, price, item);
        return 1;
    }

    public static int createHand(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        double price = DoubleArgumentType.getDouble(context, "price");
        int amount = IntegerArgumentType.getInteger(context, "amount");
        ItemStack item = player.getMainHandStack().copyWithCount(amount);

        create(player, price, item);
        return 1;
    }

    public static int createHandWithCount(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        double price = DoubleArgumentType.getDouble(context, "price");
        ItemStack item = player.getMainHandStack().copy();

        create(player, price, item);
        return 1;
    }

    public static void create(ServerPlayerEntity player, double price, ItemStack item) {
        ShopModification modification = new ShopModification()
                .modifyPrice(price)
                .modifyStack(item)
                .modifyAmount(item.getCount())
                .modifyShopOwner(player.getGameProfile().getId())
                .addPredicate(be -> true)
                .addShop();
        player.sendMessage(Text.translatable("servermoney.command.shop.create", item.getCount(), Text.translatable(item.getTranslationKey()), price, ServerMoneyConfig.moneySymbol));
        Input.selectShop(player, modification);
    }*/

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
            InputAttachment.getCompletableFuture(player, Input.InputType.ITEM).complete(stack);
        } else {
            Input.selectShop(player, new ShopModification().modifyStack(stack).addPredicate(be -> be.getShop().getShopOwner().equals(player.getGameProfile().getId())));
        }
    }
}
