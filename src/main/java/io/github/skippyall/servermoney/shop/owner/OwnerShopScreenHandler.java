package io.github.skippyall.servermoney.shop.owner;

import io.github.skippyall.servermoney.shop.ShopAttachment;
import io.github.skippyall.servermoney.input.Input;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.Generic3x3ContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public class OwnerShopScreenHandler extends Generic3x3ContainerScreenHandler {
    PlayerEntity viewer;
    ShopAttachment shop;
    BlockEntity be;
    Storage<ItemVariant> storage;

    public OwnerShopScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity viewer, ShopAttachment shop, BlockEntity be, Storage<ItemVariant> storage) {
        super(syncId, playerInventory);
        this.viewer = viewer;
        this.shop = shop;
        this.be = be;
        this.storage = storage;

        updateIcon();
    }

    private void updateIcon() {
        try(Transaction t = Transaction.openOuter()) {
            long extracted = storage.extract(ItemVariant.of(shop.getStack()), Long.MAX_VALUE, t);
            int amount = shop.getStack().getCount();
            if (extracted / amount >= 5) {
                setStackInSlot(1, nextRevision(), new ItemStack(Items.GREEN_WOOL, (int)extracted / amount));
            } else if (extracted >= amount) {
                setStackInSlot(1, nextRevision(), new ItemStack(Items.YELLOW_WOOL, (int)extracted / amount));
            } else {
                setStackInSlot(1, nextRevision(), new ItemStack(Items.RED_WOOL, 1));
            }
            setStackInSlot(4, getRevision(), new ItemStack(Items.CHEST));

            ItemStack close = new ItemStack(Items.BARRIER);
            close.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("servermoney.shop.owner.close"));
            setStackInSlot(8, getRevision(), close);

            ItemStack price = new ItemStack(Items.GOLD_INGOT);
            price.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("servermoney.shop.owner.price"));
            setStackInSlot(6, getRevision(), price);

            ItemStack amountc = new ItemStack(Items.NETHERITE_SCRAP);
            amountc.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("servermoney.shop.owner.amount"));
            setStackInSlot(7, getRevision(), amountc);
        }
    }


    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if(actionType == SlotActionType.PICKUP) {
            if (slotIndex == 4) {
                if (be instanceof NamedScreenHandlerFactory factory) {
                    player.openHandledScreen(factory);
                }
            }
            if(slotIndex == 6) {
                Input.selectPrice(player, be);
            }
            if(slotIndex == 7) {
                Input.selectAmount(player, be);
            }
            if (slotIndex == 8) {
                Input.closeShop(player, be);
            }
        }
    }
}
