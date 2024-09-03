package io.github.skippyall.servermoney.shop.screen;

import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import io.github.skippyall.servermoney.input.Input;
import io.github.skippyall.servermoney.shop.block.ShopBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class NewOwnerShopScreen {
    public static void openOwnerShopScreen(ServerPlayerEntity player, ShopBlockEntity shop) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, player, false) {

        };

        int amount = shop.getCount();
        if(amount != 0 && !shop.getItem().isBlank()) {
            try (Transaction t = Transaction.openOuter()) {
                long extracted = shop.getStorage().extract(shop.getItem(), Long.MAX_VALUE, t);
                if (extracted / amount >= 5) {
                    gui.setSlot(1, new ItemStack(Items.GREEN_WOOL, (int) extracted / amount));
                } else if (extracted >= amount) {
                    gui.setSlot(1, new ItemStack(Items.YELLOW_WOOL, (int) extracted / amount));
                } else {
                    gui.setSlot(1, new ItemStack(Items.RED_WOOL, 1));
                }
            }
        }

        gui.setSlot(4, new ItemStack(Items.CHEST), (index, type, action) -> {
            player.openHandledScreen(shop.getInventoryScreen());
        });

        ItemStack price = new ItemStack(Items.GOLD_INGOT);
        price.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("servermoney.shop.owner.price"));
        price.set(DataComponentTypes.LORE, new LoreComponent(List.of(
                Text.translatable("servermoney.shop.owner.price.current", shop.getPrice(), ServerMoneyConfig.moneySymbol)
        )));
        gui.setSlot(6, price, (index, type, action) -> {
            Input.selectPrice(player, shop);
        });

        ItemStack amountc = new ItemStack(Items.NETHERITE_SCRAP);
        amountc.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("servermoney.shop.owner.amount"));
        amountc.set(DataComponentTypes.LORE, new LoreComponent(List.of(
                Text.translatable("servermoney.shop.owner.amount.current", amount)
        )));
        gui.setSlot(7, amountc, (index, type, action) -> {
            Input.selectAmount(player, shop);
        });

        ItemStack itemc = new ItemStack(Items.ITEM_FRAME);
        itemc.set(DataComponentTypes.CUSTOM_NAME, Text.translatable("servermoney.shop.owner.item"));
        itemc.set(DataComponentTypes.LORE, new LoreComponent(List.of(
                Text.translatable("servermoney.shop.owner.item.current", shop.getItem().getItem().getName(shop.getItem().toStack()))
        )));
        gui.setSlot(8, itemc, (index, type, action) -> {
            Input.selectItem(player, shop);
        });
        gui.open();
    }
}
