package io.github.skippyall.servermoney.coin;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import io.github.skippyall.servermoney.ServerMoney;
import io.github.skippyall.servermoney.money.MoneyStorage;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CoinItem extends Item implements PolymerItem, PolymerClientDecoded, PolymerKeepModel {
    public CoinItem() {
        super(new Settings());
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return 10000;
    }

    @Override
    public Item getPolymerItem(ItemStack stack, ServerPlayerEntity player) {
        if(player != null && ServerPlayNetworking.canSend(player, ServerMoney.PACKET_ID)) {
            return this;
        } else {
            return Items.GOLD_NUGGET;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        MoneyStorage.setMoney(user, MoneyStorage.getMoney(user) + user.getStackInHand(hand).getCount());
        return TypedActionResult.consume(ItemStack.EMPTY);
    }
}
