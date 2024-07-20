package io.github.skippyall.servermoney.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.skippyall.servermoney.ServerMoney;
import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import io.github.skippyall.servermoney.input.Input;
import io.github.skippyall.servermoney.input.InputAttachment;
import io.github.skippyall.servermoney.money.MoneyStorage;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class MoneyCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("money")
                .then(literal("give")
                        .then(argument("player", GameProfileArgumentType.gameProfile())
                                .then(argument("amount", DoubleArgumentType.doubleArg(0))
                                        .executes(MoneyCommand::give)
                                )
                        )
                        .requires(Permissions.require(ServerMoney.MOD_ID+".money.give", 2))
                )
                .then(literal("pay")
                        .then(argument("player", GameProfileArgumentType.gameProfile())
                                .then(argument("amount", DoubleArgumentType.doubleArg(0))
                                        .executes(MoneyCommand::pay)
                                )
                        )
                        .requires(Permissions.require(ServerMoney.MOD_ID+".money.pay", true))
                )
                .then(literal("query")
                        .then(argument("player", GameProfileArgumentType.gameProfile())
                                .executes(MoneyCommand::queryPlayer)
                                .requires(Permissions.require(ServerMoney.MOD_ID+".money.query.others", 2))
                        )
                        .executes(MoneyCommand::query)
                        .requires(Permissions.require(ServerMoney.MOD_ID+".money.query", true))
                )
                .then(literal("set")
                        .then(argument("player", GameProfileArgumentType.gameProfile())
                                .then(argument("amount", DoubleArgumentType.doubleArg(0))
                                        .executes(MoneyCommand::set)
                                )
                        )
                        .requires(Permissions.require(ServerMoney.MOD_ID+".money.set", 2))
                )
                .then(literal("withdraw")
                        .then(argument("amount", IntegerArgumentType.integer(0))
                                .executes(MoneyCommand::withdraw)
                        )
                )
                .then(literal("deposit")
                        .then(argument("amount", IntegerArgumentType.integer(0))
                                .executes(MoneyCommand::deposit)
                        )
                        .executes(MoneyCommand::depositAll)
                )
                .then(literal("confirm")
                        .executes(MoneyCommand::confirm)
                )
        );
    }

    public static int give(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        GameProfile player = GameProfileArgumentType.getProfileArgument(context, "player").iterator().next();
        double addition = DoubleArgumentType.getDouble(context, "amount");
        MoneyStorage.addMoney(player.getId(), addition);
        context.getSource().sendMessage(Text.translatable("servermoney.command.money.give", player.getName(), addition, ServerMoneyConfig.moneySymbol));
        return 1;
    }

    public static int set(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        GameProfile player = GameProfileArgumentType.getProfileArgument(context, "player").iterator().next();
        double money = DoubleArgumentType.getDouble(context, "amount");
        MoneyStorage.setMoney(player.getId(), money);
        context.getSource().sendMessage(Text.translatable("servermoney.command.money.set", player.getName(), money, ServerMoneyConfig.moneySymbol));
        return 1;
    }

    public static int query(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        ServerPlayerEntity targetPlayer = context.getSource().getPlayerOrThrow();
        double money = MoneyStorage.getMoney(targetPlayer);
        context.getSource().sendMessage(Text.translatable("servermoney.command.money.query", money, ServerMoneyConfig.moneySymbol));
        return (int) money;
    }

    public static int queryPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        GameProfile targetPlayer = GameProfileArgumentType.getProfileArgument(context, "player").iterator().next();
        double money = MoneyStorage.getMoney(targetPlayer.getId());
        context.getSource().sendMessage(Text.translatable("servermoney.command.money.query.others", targetPlayer.getId(), money, ServerMoneyConfig.moneySymbol));
        return (int) money;
    }

    public static int pay(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        ServerPlayerEntity sourcePlayer = context.getSource().getPlayerOrThrow();
        GameProfile targetPlayer = GameProfileArgumentType.getProfileArgument(context, "player").iterator().next();

        if(sourcePlayer.getUuid().equals(targetPlayer.getId())) {
            context.getSource().sendError(Text.translatable("servermoney.command.money.pay.error_self_pay"));
        }

        double transfer = DoubleArgumentType.getDouble(context, "amount");

        if(MoneyStorage.tryPay(sourcePlayer.getUuid(), targetPlayer.getId(), transfer)){
            sourcePlayer.sendMessage(Text.translatable("servermoney.command.money.pay.sender", targetPlayer.getName(), transfer, ServerMoneyConfig.moneySymbol));
            sourcePlayer.sendMessage(Text.translatable("servermoney.command.money.pay.receiver", targetPlayer.getName(), transfer, ServerMoneyConfig.moneySymbol));
            return 1;
        } else {
            double sourceMoney = MoneyStorage.getMoney(sourcePlayer);
            context.getSource().sendError(Text.translatable("servermoney.command.money.pay.error_not_enough_money", sourceMoney, ServerMoneyConfig.moneySymbol, transfer, ServerMoneyConfig.moneySymbol));
            return 0;
        }
    }

    public static int withdraw(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        PlayerInventoryStorage storage = PlayerInventoryStorage.of(player);

        try(Transaction t = Transaction.openOuter()) {
            if (storage.insert(ItemVariant.of(ServerMoney.COIN_ITEM), amount, t) == amount && MoneyStorage.tryRemoveMoney(player, amount)) {
                t.commit();
            }
        }
        return 0;
    }

    public static int depositAll(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        deposit(player, Integer.MAX_VALUE);
        return 0;
    }

    public static int deposit(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        int amount = IntegerArgumentType.getInteger(context, "amount");
        deposit(player, amount);
        return 0;
    }

    public static void deposit(ServerPlayerEntity player, int amount) {
        PlayerInventoryStorage storage = PlayerInventoryStorage.of(player);

        try(Transaction t = Transaction.openOuter()) {
            MoneyStorage.addMoney(player, storage.extract(ItemVariant.of(ServerMoney.COIN_ITEM), amount, t));
            t.commit();
        }
    }

    public static int confirm(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
        if(InputAttachment.hasInputType(player, Input.InputType.CONFIRM_PAY)) {
            InputAttachment.getCompletableFuture(player, Input.InputType.CONFIRM_PAY).complete(null);
        }
        return 0;
    }
}
