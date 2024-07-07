package io.github.skippyall.servermoney.shop.block;

import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public interface InventoryShopBlockEntity extends ShopBlockEntity, SidedInventory {
    @Override
    default int[] getAvailableSlots(Direction dir) {
        return new int[0];
    }

    @Override
    default boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    @Override
    default boolean canExtract(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }
}
