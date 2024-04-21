package io.github.skippyall.servermoney.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.skippyall.servermoney.ServerMoney;
import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import io.github.skippyall.servermoney.money.MoneyStorage;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
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
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("amount", DoubleArgumentType.doubleArg(0))
                                        .executes(MoneyCommand::give)
                                )
                        )
                        .requires(Permissions.require(ServerMoney.MOD_ID+".money.give", 2))
                )
                .then(literal("pay")
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("amount", DoubleArgumentType.doubleArg(0))
                                        .executes(MoneyCommand::pay)
                                )
                        )
                        .requires(Permissions.require(ServerMoney.MOD_ID+".money.pay", true))
                )
                .then(literal("query")
                        .then(argument("player", EntityArgumentType.player())
                                .executes(MoneyCommand::queryPlayer)
                                .requires(Permissions.require(ServerMoney.MOD_ID+".money.query.others", 2))
                        )
                        .executes(MoneyCommand::query)
                        .requires(Permissions.require(ServerMoney.MOD_ID+".money.query", true))
                )
                .then(literal("set")
                        .then(argument("player", EntityArgumentType.player())
                                .then(argument("amount", DoubleArgumentType.doubleArg(0))
                                        .executes(MoneyCommand::set)
                                )
                        )
                        .requires(Permissions.require(ServerMoney.MOD_ID+".money.set", 2))
                )
        );
    }

    public static int give(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        double addition = DoubleArgumentType.getDouble(context, "amount");
        MoneyStorage.setMoney(player, MoneyStorage.getMoney(player) + addition);
        context.getSource().sendMessage(Text.translatable("servermoney.command.money.give", player.getDisplayName(), addition, ServerMoneyConfig.moneySymbol));
        return 1;
    }

    public static int set(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        double money = DoubleArgumentType.getDouble(context, "amount");
        MoneyStorage.setMoney(player, money);
        context.getSource().sendMessage(Text.translatable("servermoney.command.money.set", player.getDisplayName(), money, ServerMoneyConfig.moneySymbol));
        return 1;
    }

    public static int query(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        ServerPlayerEntity targetPlayer = context.getSource().getPlayerOrThrow();
        double money = MoneyStorage.getMoney(targetPlayer);
        context.getSource().sendMessage(Text.translatable("servermoney.command.money.query", money, ServerMoneyConfig.moneySymbol));
        return (int) money;
    }

    public static int queryPlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
        double money = MoneyStorage.getMoney(targetPlayer);
        context.getSource().sendMessage(Text.translatable("servermoney.command.money.query.others", targetPlayer.getDisplayName(), money, ServerMoneyConfig.moneySymbol));
        return (int) money;
    }

    public static int pay(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        ServerPlayerEntity sourcePlayer = context.getSource().getPlayerOrThrow();
        ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");

        if(sourcePlayer == targetPlayer) {
            context.getSource().sendError(Text.translatable("servermoney.command.money.pay.error_self_pay"));
        }

        double transfer = DoubleArgumentType.getDouble(context, "amount");

        if(MoneyStorage.tryPay(sourcePlayer.getUuid(), targetPlayer.getUuid(), transfer)){
            sourcePlayer.sendMessage(Text.translatable("servermoney.command.money.pay.sender", targetPlayer.getDisplayName(), transfer, ServerMoneyConfig.moneySymbol));
            sourcePlayer.sendMessage(Text.translatable("servermoney.command.money.pay.receiver", targetPlayer.getDisplayName(), transfer, ServerMoneyConfig.moneySymbol));
            return 1;
        } else {
            double sourceMoney = MoneyStorage.getMoney(sourcePlayer);
            context.getSource().sendError(Text.translatable("servermoney.command.money.pay.error_not_enough_money", sourceMoney, ServerMoneyConfig.moneySymbol, transfer, ServerMoneyConfig.moneySymbol));
            return 0;
        }
    }
}
