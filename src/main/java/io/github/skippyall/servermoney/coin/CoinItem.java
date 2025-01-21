package io.github.skippyall.servermoney.coin;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import io.github.skippyall.servermoney.ServerMoney;
import io.github.skippyall.servermoney.money.MoneyStorage;
import io.github.skippyall.servermoney.polymer.PolymerUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

public class CoinItem extends Item implements PolymerItem {
    public CoinItem(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return Identifier.of(ServerMoney.MOD_ID, "item/coin");
    }

    @Override
    public Item getPolymerItem(ItemStack stack, PacketContext context) {
        if (PolymerUtil.shouldReplace(context.getPlayer())) {
            return Items.GOLD_NUGGET;
        } else {
            return stack.getItem();
        }
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        MoneyStorage.setMoney(user, MoneyStorage.getMoney(user) + user.getStackInHand(hand).getCount());
        return ActionResult.CONSUME.withNewHandStack(ItemStack.EMPTY);
    }
}
