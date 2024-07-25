package io.github.skippyall.servermoney.shop.block;

import io.github.skippyall.servermoney.shop.Shop;
import mineattack.customthings.api.CustomBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public interface ShopBlockEntity extends CustomBlockEntity {
    Shop getShop();

    Storage<ItemVariant> getStorage();

    default UUID getShopOwner() {
        return getShop().getShopOwner();
    }

    default ItemVariant getItem() {
        return getShop().getItem();
    }

    default int getCount() {
        return getShop().getCount();
    }

    default double getPrice() {
        return getShop().getPrice();
    }

    default void setShopOwner(UUID shopOwner) {
        getShop().setShopOwner(shopOwner);
    }

    default void setItem(ItemVariant variant) {
        getShop().setItem(variant);
    }

    default void setCount(int count) {
        getShop().setCount(count);
    }

    default void setPrice(double price) {
        getShop().setPrice(price);
    }
}
