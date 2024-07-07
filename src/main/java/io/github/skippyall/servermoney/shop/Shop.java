package io.github.skippyall.servermoney.shop;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

import java.util.UUID;

public class Shop {
    private UUID shopOwner = FakePlayer.DEFAULT_UUID;
    private ItemStack stack = ItemStack.EMPTY;
    private double price = 0;

    public UUID getShopOwner() {
        return shopOwner;
    }

    public ItemStack getStack() {
        return stack;
    }

    public double getPrice() {
        return price;
    }

    public void setShopOwner(UUID shopOwner) {
        this.shopOwner = shopOwner;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public NbtCompound encode(RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound shop = new NbtCompound();
        shop.putUuid("owner", getShopOwner());
        shop.putDouble("price", getPrice());
        shop.put("stack", getStack().encodeAllowEmpty(registryLookup));
        return shop;
    }

    public void decode(NbtCompound shop, RegistryWrapper.WrapperLookup registryLookup) {
        setShopOwner(shop.getUuid("owner"));
        setPrice(shop.getDouble("price"));
        setStack(ItemStack.fromNbtOrEmpty(registryLookup, shop.getCompound("stack")));
    }
}
