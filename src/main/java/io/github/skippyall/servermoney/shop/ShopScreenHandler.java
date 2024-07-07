package io.github.skippyall.servermoney.shop;

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
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
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

    ItemStack stack;
    UUID owner;
    double price;
    ItemVariant soldItemVariant;

    public ShopScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity player, ShopBlockEntity shop) {
        super(syncId, playerInventory);
        this.stack = shop.getStack();
        this.price = shop.getPrice();
        this.storage = shop.getStorage();
        this.owner = shop.getShopOwner();
        soldItemVariant = ItemVariant.of(stack);
        this.viewer = player.getUuid();
        updateIcon();
    }

    public static void openShopScreen(PlayerEntity viewer, ShopBlockEntity shop) {
        viewer.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncid, playerInventory, player2) -> new ShopScreenHandler(syncid, playerInventory, player2, shop), getDisplayName(shop.getShop().getStack(), shop.getShop().getPrice())));
    }

    private void updateIcon() {
        try(Transaction t = Transaction.openOuter()) {
            long extracted = storage.extract(soldItemVariant, stack.getCount(), t);
            ItemStack icon = stack.copyWithCount(1);
            if(extracted < stack.getCount()){
                stack.getOrDefault(DataComponentTypes.LORE, new LoreComponent(new ArrayList<>())).with(Text.translatable("servermoney.shop.buy.out_of_stock"));
            } else if(!MoneyStorage.canPay(viewer, price)) {
                stack.getOrDefault(DataComponentTypes.LORE, new LoreComponent(new ArrayList<>())).with(Text.translatable("servermoney.shop.buy.not_enough_money"));
            }
            setStackInSlot(4, nextRevision(), stack.copyWithCount(1));
        }
    }

    public static Text getDisplayName(ItemStack stack, double price) {
        return Text.translatable("servermoney.shop.buy.title", stack.getCount(), Text.translatable(stack.getTranslationKey()), price, ServerMoneyConfig.moneySymbol);
    }

    Set<SlotActionType> ALLOWED_ACTIONS = Set.of(SlotActionType.PICKUP, SlotActionType.SWAP, SlotActionType.THROW, SlotActionType.CLONE);

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if(slotIndex == 4) {
            if (actionType == SlotActionType.PICKUP) {
                if (MoneyStorage.canPay(player.getUuid(), price)) {
                    long extracted;
                    try (Transaction t = Transaction.openOuter()) {
                        extracted = storage.extract(soldItemVariant, stack.getCount(), t);
                        if (extracted == stack.getCount()) {
                            PlayerInventoryStorage playerInventory = PlayerInventoryStorage.of(player.getInventory());
                            long inserted;
                            try (Transaction t2 = t.openNested()) {
                                inserted = playerInventory.insert(soldItemVariant, stack.getCount(), t2);
                                if (inserted == stack.getCount()) {
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
