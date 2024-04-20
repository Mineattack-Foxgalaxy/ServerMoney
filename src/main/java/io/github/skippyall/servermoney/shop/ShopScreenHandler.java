package io.github.skippyall.servermoney.shop;

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

import java.util.Set;
import java.util.UUID;

public class ShopScreenHandler extends Generic3x3ContainerScreenHandler {
    Item soldItem;
    ItemVariant soldItemVariant;
    int amount;
    double price;
    Storage<ItemVariant> storage;
    UUID owner;

    public ShopScreenHandler(int syncId, PlayerInventory playerInventory, Item soldItem, int amount, double price, Storage<ItemVariant> storage, UUID owner) {
        super(syncId, playerInventory);
        this.soldItem = soldItem;
        this.amount = amount;
        this.price = price;
        this.storage = storage;
        this.owner = owner;
        soldItemVariant = ItemVariant.of(soldItem);
        updateIcon();
    }

    private void updateIcon() {
        try(Transaction t = Transaction.openOuter()) {
            long extracted = storage.extract(ItemVariant.of(soldItem), amount, t);
            if(extracted < amount){
                setStackInSlot(4, nextRevision(), new ItemStack(Items.BARRIER));
            } else {
                setStackInSlot(4, nextRevision(), new ItemStack(soldItem));
            }
        }
    }

    Set<SlotActionType> ALLOWED_ACTIONS = Set.of(SlotActionType.PICKUP, SlotActionType.SWAP, SlotActionType.THROW, SlotActionType.CLONE);

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if(slotIndex == 4) {
            if (actionType == SlotActionType.PICKUP) {
                double money = MoneyStorage.getMoney(player);
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
                                    MoneyStorage.setMoney(player, money - price);
                                    t2.commit();
                                    t.commit();
                                    MoneyStorage.setMoney(owner, MoneyStorage.getMoney(owner) + price);
                                    updateIcon();
                                }
                            }
                        }
                    }
                }
            }
        } else if(slotIndex < 0 || slotIndex > 8 && ALLOWED_ACTIONS.contains(actionType)){
            super.onSlotClick(slotIndex, button, actionType, player);
        }
    }
}
