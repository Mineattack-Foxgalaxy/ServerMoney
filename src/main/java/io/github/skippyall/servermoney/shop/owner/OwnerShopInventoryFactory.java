package io.github.skippyall.servermoney.shop.owner;

import io.github.skippyall.servermoney.shop.ShopAttachment;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class OwnerShopInventoryFactory implements NamedScreenHandlerFactory {
    PlayerEntity viewer;
    ShopAttachment shop;
    BlockEntity be;
    Storage<ItemVariant> storage;

    protected OwnerShopInventoryFactory(PlayerEntity viewer, ShopAttachment shop, BlockEntity be, Storage<ItemVariant> storage) {
        this.viewer = viewer;
        this.shop = shop;
        this.be = be;
        this.storage = storage;
    }


    public static void openOwnerShopInventory(PlayerEntity viewer, ShopAttachment shop, BlockEntity be, Storage<ItemVariant> storage) {
        viewer.openHandledScreen(new OwnerShopInventoryFactory(viewer, shop, be, storage));
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("servermoney.shop.owner.title");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new OwnerShopScreenHandler(syncId, playerInventory, viewer, shop, be, storage);
    }
}
