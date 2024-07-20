package io.github.skippyall.servermoney.coin;

import io.github.skippyall.servermoney.money.MoneyStorage;
import mineattack.customthings.api.CustomItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CoinItem extends Item implements CustomItem {
    public CoinItem() {
        super(new Settings());
    }

    @Override
    public ItemStack getVanillaItemStack(ItemStack original, ServerPlayerEntity player) {
        ItemStack stack = new ItemStack(Items.GOLD_NUGGET);
        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(10000));
        stack.set(DataComponentTypes.ITEM_NAME, Text.translatable("item.servermoney.coin"));
        return stack;
    }

    @Override
    public Item getVanillaItem(ItemStack stack, ServerPlayerEntity player) {
        return Items.GOLD_NUGGET;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        MoneyStorage.setMoney(user, MoneyStorage.getMoney(user) + user.getStackInHand(hand).getCount());
        return TypedActionResult.consume(ItemStack.EMPTY);
    }
}
