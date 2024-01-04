package io.github.skippyall.servermoney.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.github.skippyall.servermoney.ServerMoney;
import io.github.skippyall.servermoney.money.MoneyStorage;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;


public class MoneyCommand {
    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<ServerCommandSource>literal("money")
                .then(LiteralArgumentBuilder.<ServerCommandSource>literal("give")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>argument("player", EntityArgumentType.player())
                                .then(RequiredArgumentBuilder.<ServerCommandSource, Long>argument("amount", LongArgumentType.longArg())
                                        .executes(context->{
                                            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                                            long addition = LongArgumentType.getLong(context, "amount");
                                            MoneyStorage.setMoney(player, MoneyStorage.getMoney(player) + addition);
                                            context.getSource().sendMessage(Text.literal("You gave ").append(player.getDisplayName()).append(" "+addition+ServerMoney.MONEY_SYMBOL+"."));
                                            return 1;
                                        })
                                )
                        )
                        .requires(source -> {
                            return source.hasPermissionLevel(2);
                        })
                )
                .then(LiteralArgumentBuilder.<ServerCommandSource>literal("pay")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>argument("player", EntityArgumentType.player())
                                .then(RequiredArgumentBuilder.<ServerCommandSource, Long>argument("amount", LongArgumentType.longArg())
                                        .executes(context->{
                                            ServerPlayerEntity sourcePlayer = context.getSource().getPlayerOrThrow();
                                            ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
                                            long transfer = LongArgumentType.getLong(context, "amount");
                                            long sourceMoney = MoneyStorage.getMoney(sourcePlayer);
                                            if(sourceMoney >= transfer){
                                                MoneyStorage.setMoney(sourcePlayer, sourceMoney - transfer);
                                                MoneyStorage.setMoney(targetPlayer, MoneyStorage.getMoney(targetPlayer) + transfer);
                                                sourcePlayer.sendMessage(Text.literal("You paid ").append(targetPlayer.getDisplayName()).append(" "+transfer+ServerMoney.MONEY_SYMBOL+"."));
                                                targetPlayer.sendMessage(targetPlayer.getDisplayName().copy().append(Text.literal(" paid you "+transfer+ServerMoney.MONEY_SYMBOL+".")));
                                                return 1;
                                            } else {
                                                context.getSource().sendError(Text.literal("Not enough money"));
                                                return 0;
                                            }
                                        })
                                )
                        )
                )
                .then(LiteralArgumentBuilder.<ServerCommandSource>literal("query")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, EntitySelector>argument("player", EntityArgumentType.player())
                                .executes(context -> {
                                    ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "player");
                                    long money = MoneyStorage.getMoney(targetPlayer);
                                    context.getSource().sendMessage(targetPlayer.getDisplayName().copy().append(Text.literal( " has " + money + ServerMoney.MONEY_SYMBOL)));
                                    return (int) money;
                                })
                        )
                        .executes(context -> {
                            ServerPlayerEntity targetPlayer = context.getSource().getPlayerOrThrow();
                            long money = MoneyStorage.getMoney(targetPlayer);
                            context.getSource().sendMessage(Text.literal("You have " + money + ServerMoney.MONEY_SYMBOL));
                            return (int) money;
                        })
                )
        );
    }
}
