package io.github.skippyall.servermoney.shop.owner;

import io.github.skippyall.servermoney.ServerMoney;
import io.github.skippyall.servermoney.shop.ShopComponent;
import io.github.skippyall.servermoney.shop.ShopInventoryFactory;
import io.github.skippyall.servermoney.shop.ShopScreenHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OwnerShopInventoryFactory implements NamedScreenHandlerFactory {
    PlayerEntity viewer;
    ShopComponent shop;
    BlockEntity be;
    Storage<ItemVariant> storage;

    protected OwnerShopInventoryFactory(PlayerEntity viewer, ShopComponent shop, BlockEntity be, Storage<ItemVariant> storage) {
        this.viewer = viewer;
        this.shop = shop;
        this.be = be;
        this.storage = storage;
    }


    public static void openOwnerShopInventory(PlayerEntity viewer, ShopComponent shop, BlockEntity be, Storage<ItemVariant> storage) {
        viewer.openHandledScreen(new OwnerShopInventoryFactory(viewer, shop, be, storage));
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Shop Editor");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new OwnerShopScreenHandler(syncId, playerInventory, viewer, shop, be, storage);
    }
}
