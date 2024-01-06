package io.github.skippyall.servermoney.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.github.skippyall.servermoney.ServerMoney;
import io.github.skippyall.servermoney.shop.ShopCreateComponent;
import io.github.skippyall.servermoney.money.MoneyStorage;
import io.github.skippyall.servermoney.shop.ShopComponent;
import io.github.skippyall.servermoney.shop.ShopCreateStorage;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;


public class MoneyCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
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
                                                targetPlayer.sendMessage(sourcePlayer.getDisplayName().copy().append(Text.literal(" paid you "+transfer+ServerMoney.MONEY_SYMBOL+".")));
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
                                    context.getSource().sendMessage(targetPlayer.getDisplayName().copy().append(Text.literal( " has " + money + ServerMoney.MONEY_SYMBOL+".")));
                                    return (int) money;
                                })
                        )
                        .executes(context -> {
                            ServerPlayerEntity targetPlayer = context.getSource().getPlayerOrThrow();
                            long money = MoneyStorage.getMoney(targetPlayer);
                            context.getSource().sendMessage(Text.literal("You have " + money + ServerMoney.MONEY_SYMBOL+"."));
                            return (int) money;
                        })
                )
                .then(LiteralArgumentBuilder.<ServerCommandSource>literal("createshop")
                        .then(RequiredArgumentBuilder.<ServerCommandSource, Long>argument("price", LongArgumentType.longArg())
                                .then(RequiredArgumentBuilder.<ServerCommandSource, ItemStackArgument>argument("item", ItemStackArgumentType.itemStack(access))
                                        .then(RequiredArgumentBuilder.<ServerCommandSource, Integer>argument("amount", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                            ShopCreateComponent component = ShopCreateStorage.getData(player);
                                            component.isCreatingShop = true;
                                            component.shopCreating = new ShopComponent();
                                            component.shopCreating.isShop = true;
                                            component.shopCreating.shopOwner = player.getGameProfile().getId();
                                            component.shopCreating.price = LongArgumentType.getLong(context, "price");
                                            component.shopCreating.item = ItemStackArgumentType.getItemStackArgument(context, "item").getItem();
                                            component.shopCreating.amount = IntegerArgumentType.getInteger(context, "amount");
                                            player.sendMessage(Text.literal("Creating a shop, selling "+component.shopCreating.amount+" "+component.shopCreating.item.toString()+" for "+component.shopCreating.price+ServerMoney.MONEY_SYMBOL+"."));
                                            return 1;
                                        })
                                )
                                )
                        )
                )
        );
    }
}
