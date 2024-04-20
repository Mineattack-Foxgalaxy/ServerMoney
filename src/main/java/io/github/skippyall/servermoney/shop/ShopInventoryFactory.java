package io.github.skippyall.servermoney.shop;

import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ShopInventoryFactory implements NamedScreenHandlerFactory {
    Item soldItem;
    int amount;
    double price;
    Storage<ItemVariant> storage;
    UUID owner;

    protected ShopInventoryFactory(Item soldItem, int amount, double price, Storage<ItemVariant> storage, UUID owner) {
        this.soldItem = soldItem;
        this.amount = amount;
        this.price = price;
        this.storage = storage;
        this.owner = owner;
    }

    public static void openShopInventory(Item soldItem, int amount, double price, PlayerEntity viewer, Storage<ItemVariant> storage, UUID owner) {
        viewer.openHandledScreen(new ShopInventoryFactory(soldItem, amount, price, storage, owner));
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Shop | Buy " + amount + " for "+ price + ServerMoneyConfig.moneySymbol);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ShopScreenHandler(syncId, playerInventory, soldItem, amount, price, storage, owner);
    }
}
