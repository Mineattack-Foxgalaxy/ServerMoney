package io.github.skippyall.servermoney.shop.screen;

import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import io.github.skippyall.servermoney.money.MoneyStorage;
import io.github.skippyall.servermoney.shop.block.ShopBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class ShopScreenHandler extends Generic3x3ContainerScreenHandler {
    ShopBlockEntity shop;
    Storage<ItemVariant> storage;
    UUID viewer;

    UUID owner;
    double price;
    ItemVariant soldItemVariant;
    int count;

    public ShopScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity player, ShopBlockEntity shop) {
        super(syncId, playerInventory);
        this.price = shop.getPrice();
        this.storage = shop.getStorage();
        this.owner = shop.getShopOwner();
        soldItemVariant = shop.getItem();
        count = shop.getCount();
        this.viewer = player.getUuid();
        updateIcon();
    }

    public static void openShopScreen(PlayerEntity viewer, ShopBlockEntity shop) {
        viewer.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncid, playerInventory, player2) -> new ShopScreenHandler(syncid, playerInventory, player2, shop), getDisplayName(shop.getShop().getItem(), shop.getCount(), shop.getShop().getPrice())));
    }

    private void updateIcon() {
        try(Transaction t = Transaction.openOuter()) {
            long extracted = storage.extract(soldItemVariant, count, t);
            ItemStack icon = soldItemVariant.toStack();
            if(extracted < count){
                icon.set(DataComponentTypes.LORE, icon.getOrDefault(DataComponentTypes.LORE, new LoreComponent(new ArrayList<>())).with(Text.translatable("servermoney.shop.buy.out_of_stock")));
            } else if(!MoneyStorage.canPay(viewer, price)) {
                icon.set(DataComponentTypes.LORE, icon.getOrDefault(DataComponentTypes.LORE, new LoreComponent(new ArrayList<>())).with(Text.translatable("servermoney.shop.buy.not_enough_money")));
            }
            setStackInSlot(4, nextRevision(), icon.copyWithCount(1));
        }
    }

    public static Text getDisplayName(ItemVariant item, int count, double price) {
        return Text.translatable("servermoney.shop.buy.title", count, Text.translatable(item.getItem().getTranslationKey()), price, ServerMoneyConfig.moneySymbol);
    }

    Set<SlotActionType> ALLOWED_ACTIONS = Set.of(SlotActionType.PICKUP, SlotActionType.SWAP, SlotActionType.THROW, SlotActionType.CLONE);

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if(slotIndex == 4) {
            if (actionType == SlotActionType.PICKUP) {
                if (MoneyStorage.canPay(player.getUuid(), price)) {
                    long extracted;
                    try (Transaction t = Transaction.openOuter()) {
                        extracted = storage.extract(soldItemVariant, count, t);
                        if (extracted == count) {
                            PlayerInventoryStorage playerInventory = PlayerInventoryStorage.of(player.getInventory());
                            long inserted;
                            try (Transaction t2 = t.openNested()) {
                                inserted = playerInventory.insert(soldItemVariant, count, t2);
                                if (inserted == count) {
                                    MoneyStorage.tryPay(player.getUuid(), owner, price);
                                    t2.commit();
                                    t.commit();
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
