package io.github.skippyall.servermoney.shop;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class ShopComponent implements Component {
    private static final String IS_SHOP_KEY = "is_shop";
    private static final String PRICE_KEY = "price";
    private static final String OWNER_KEY = "owner";
    private static final String ITEM_KEY = "item";
    private static final String AMOUNT_KEY = "amount";

    public boolean isShop;
    public UUID shopOwner;
    public Item item;
    public int amount;
    public long price;

    @Override
    public void readFromNbt(NbtCompound tag) {
        isShop = tag.getBoolean(IS_SHOP_KEY);
        if(isShop) {
            shopOwner = tag.getUuid(OWNER_KEY);
            price = tag.getLong(PRICE_KEY);
            item = Registries.ITEM.get(Identifier.tryParse(tag.getString(ITEM_KEY)));
            amount = tag.getInt(AMOUNT_KEY);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean(IS_SHOP_KEY, isShop);
        if(isShop) {
            tag.putUuid(OWNER_KEY, shopOwner);
            tag.putLong(PRICE_KEY, price);
            tag.putString(ITEM_KEY, Registries.ITEM.getId(item).toString());
            tag.putInt(AMOUNT_KEY, amount);
        }
    }

    public void copyFrom(ShopComponent component) {
        this.isShop = component.isShop;
        this.shopOwner = component.shopOwner;
        this.amount = component.amount;
        this.item = component.item;
        this.price = component.price;
    }
}
