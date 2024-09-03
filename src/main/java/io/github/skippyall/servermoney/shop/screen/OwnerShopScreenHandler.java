package io.github.skippyall.servermoney.shop.screen;

import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import io.github.skippyall.servermoney.input.Input;
import io.github.skippyall.servermoney.shop.block.ShopBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class OwnerShopScreenHandler extends Generic3x3ContainerScreenHandler {
    PlayerEntity viewer;
    ShopBlockEntity shop;
    Storage<ItemVariant> storage;

    public OwnerShopScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity viewer, ShopBlockEntity shop) {
        super(syncId, playerInventory);
        this.viewer = viewer;
        this.shop = shop;
        this.storage = shop.getStorage();

        updateIcon();
    }

    public static void openOwnerShopScreen(PlayerEntity viewer, ShopBlockEntity shop) {
        viewer.openHandledScreen(
                new SimpleNamedScreenHandlerFactory(
                        (syncid, playerInventory, player2) -> new OwnerShopScreenHandler(syncid, playerInventory, player2, shop),
                        Text.literal("")
                )
        );
    }

    private void updateIcon() {
        int amount = shop.getCount();
        if(amount != 0 && !shop.getItem().isBlank()) {
            try (Transaction t = Transaction.openOuter()) {
                long extracted = storage.extract(shop.getItem(), Long.MAX_VALUE, t);
                if (extracted / amount >= 5) {
                    setStackInSlot(1, nextRevision(), new ItemStack(Items.GREEN_WOOL, (int) extracted / amount));
                } else if (extracted >= amount) {
                    setStackInSlot(1, nextRevision(), new ItemStack(Items.YELLOW_WOOL, (int) extracted / amount));
                } else {
                    setStackInSlot(1, nextRevision(), new ItemStack(Items.RED_WOOL, 1));
                }
            }
        }

        setStackInSlot(4, getRevision(), new ItemStack(Items.CHEST));

        ItemStack price = new ItemStack(Items.GOLD_INGOT);
        price.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("servermoney.shop.owner.price"));
        price.set(DataComponentTypes.LORE, new LoreComponent(List.of(
                Text.translatable("servermoney.shop.owner.price.current", shop.getPrice(), ServerMoneyConfig.moneySymbol)
        )));
        setStackInSlot(6, getRevision(), price);

        ItemStack amountc = new ItemStack(Items.NETHERITE_SCRAP);
        amountc.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("servermoney.shop.owner.amount"));
        amountc.set(DataComponentTypes.LORE, new LoreComponent(List.of(
            Text.translatable("servermoney.shop.owner.amount.current", amount)
        )));
        setStackInSlot(7, getRevision(), amountc);

        ItemStack itemc = new ItemStack(Items.ITEM_FRAME);
        itemc.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("servermoney.shop.owner.item"));
        itemc.set(DataComponentTypes.LORE, new LoreComponent(List.of(
                Text.translatable("servermoney.shop.owner.item.current", shop.getItem().getItem().getName(shop.getItem().toStack()))
        )));
        setStackInSlot(8, getRevision(), itemc);
    }


    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if(actionType == SlotActionType.PICKUP) {
            if (slotIndex == 4) {
                player.openHandledScreen(shop.getInventoryScreen());
            }
            if(slotIndex == 6) {
                Input.selectPrice((ServerPlayerEntity) player, shop);
            }
            if(slotIndex == 7) {
                Input.selectAmount((ServerPlayerEntity) player, shop);
            }
            if(slotIndex == 8) {
                Input.selectItem(player, shop);
            }
        }
    }
}
