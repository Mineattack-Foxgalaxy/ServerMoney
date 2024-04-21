package io.github.skippyall.servermoney.shop;

import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ShopInventoryFactory implements NamedScreenHandlerFactory {
    ItemStack stack;
    double price;
    Storage<ItemVariant> storage;
    UUID owner;

    protected ShopInventoryFactory(ItemStack stack, double price, Storage<ItemVariant> storage, UUID owner) {
        this.stack = stack;
        this.price = price;
        this.storage = storage;
        this.owner = owner;
    }

    public static void openShopInventory(ItemStack stack, double price, PlayerEntity viewer, Storage<ItemVariant> storage, UUID owner) {
        viewer.openHandledScreen(new ShopInventoryFactory(stack, price, storage, owner));
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("servermoney.shop.buy.title", stack.getCount(), Text.translatable(stack.getTranslationKey()), price, ServerMoneyConfig.moneySymbol);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ShopScreenHandler(syncId, playerInventory, stack, price, storage, owner);
    }
}
