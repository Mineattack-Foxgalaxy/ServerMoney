package io.github.skippyall.servermoney.shop;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;

import java.util.UUID;

public class Shop {
    private UUID shopOwner = FakePlayer.DEFAULT_UUID;
    private ItemVariant item = ItemVariant.blank();
    private int count = 0;
    private double price = 0;

    public UUID getShopOwner() {
        return shopOwner;
    }

    public ItemVariant getItem() {
        return item;
    }

    public int getCount(){
        return count;
    }

    public double getPrice() {
        return price;
    }

    public void setShopOwner(UUID shopOwner) {
        this.shopOwner = shopOwner;
    }

    public void setItem(ItemVariant item) {
        this.item = item;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public NbtCompound encode(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound shop = new NbtCompound();
        shop.putUuid("owner", getShopOwner());
        shop.putDouble("price", getPrice());
        shop.put("item", ItemVariant.CODEC.encode(getItem(), NbtOps.INSTANCE, null).getOrThrow());
        shop.putInt("count", getCount());
        return shop;
    }

    public void decode(NbtCompound shop, RegistryWrapper.WrapperLookup registryLookup) {
        setShopOwner(shop.getUuid("owner"));
        setPrice(shop.getDouble("price"));
        ItemVariant.CODEC.decode(NbtOps.INSTANCE, shop.get("item")).ifSuccess(pair -> setItem(pair.getFirst()));
        setCount(shop.getInt("count"));
    }
}
