package io.github.skippyall.servermoney.shop;

import io.github.skippyall.servermoney.mixins.Generic3x3ContainerScreenHandlerAccessor;
import io.github.skippyall.servermoney.money.MoneyStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.Main;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

public class ShopScreenHandler extends Generic3x3ContainerScreenHandler {
    Item soldItem;
    ItemVariant soldItemVariant;
    int amount;
    long price;
    Storage<ItemVariant> storage;
    UUID owner;

    public ShopScreenHandler(int syncId, PlayerInventory playerInventory, Item soldItem, int amount, long price, Storage<ItemVariant> storage, UUID owner) {
        super(syncId, playerInventory, createInventory(soldItem, amount, storage));
        this.soldItem = soldItem;
        this.amount = amount;
        this.price = price;
        this.storage = storage;
        soldItemVariant = ItemVariant.of(soldItem);
    }

    private static Inventory createInventory(Item soldItem, int amount, Storage<ItemVariant> storage){
        SimpleInventory inventory = new SimpleInventory(9);
        updateIcon(inventory, storage, soldItem, amount);
        return inventory;
    }

    private static void updateIcon(SimpleInventory inventory, Storage<ItemVariant> storage, Item soldItem, int amount) {
        try(Transaction t = Transaction.openOuter()) {
            long extracted = storage.extract(ItemVariant.of(soldItem), amount, t);
            if(extracted < amount){
                inventory.setStack(4, new ItemStack(Items.BARRIER));
            } else {
                inventory.heldStacks.set(4, new ItemStack(soldItem, amount));
                inventory.markDirty();
            }
        }
    }


    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if(actionType == SlotActionType.PICKUP) {
            long money = MoneyStorage.getMoney(player);
            if (money > price) {
                long extracted;
                try (Transaction t = Transaction.openOuter()) {
                    extracted = storage.extract(soldItemVariant, amount, t);
                    if (extracted == amount) {
                        PlayerInventoryStorage playerInventory = PlayerInventoryStorage.of(player.getInventory());
                        long inserted;
                        try (Transaction t2 = t.openNested()) {
                            inserted = playerInventory.insert(soldItemVariant, amount, t2);
                            if (inserted == amount) {
                                MoneyStorage.setMoney(player, money-price);
                                t2.commit();
                                t.commit();
                                MoneyStorage.setMoney(owner, MoneyStorage.getMoney(owner)+price);
                                updateIcon((SimpleInventory) ((Generic3x3ContainerScreenHandlerAccessor)this).getInventory(), storage, soldItem, amount);
                            }
                        }
                    }
                }
            }
        }
    }
}
