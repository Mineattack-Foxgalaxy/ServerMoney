package io.github.skippyall.servermoney.input;

import com.mojang.authlib.GameProfile;
import io.github.skippyall.servermoney.ServerMoney;
import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import io.github.skippyall.servermoney.money.MoneyStorage;
import io.github.skippyall.servermoney.paybutton.PayButtonBlockEntity;
import io.github.skippyall.servermoney.shop.ShopComponent;
import io.github.skippyall.servermoney.shop.ShopStorage;
import io.github.skippyall.servermoney.shop.modification.ShopModification;
import io.github.skippyall.servermoney.shop.modification.ShopModificationComponent;
import io.github.skippyall.servermoney.shop.modification.ShopModificationStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.awt.desktop.SystemEventListener;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Input {
    public static void selectPrice(PlayerEntity player, ShopComponent shop) {
        player.sendMessage(Text.literal("Click to set the price").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/shop modify price "))));
        scheduleInput(InputType.PRICE, player).thenAccept(price -> shop.price = price);
    }

    public static void selectAmount(PlayerEntity player, ShopComponent shop) {
        player.sendMessage(Text.literal("Click to set the amount").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/shop modify amount "))));
        scheduleInput(InputType.AMOUNT, player).thenAccept(amount -> shop.amount = amount);
    }

    /**
     * Lets the player select a shop. The modification will be applied to the shop that the player selects.
     * @param player The player that should select the shop
     * @param modification The ShopModification that should be applied to the selected shop
     */
    public static void selectShop(PlayerEntity player, ShopModification modification){
        scheduleInput(InputType.SHOP, player).thenAccept(blockEntity -> {
            ShopComponent shop = ShopStorage.getShop(blockEntity);
            if(shop != null) {
                modification.apply(shop);
            }
        });
    }

    public static void closeShop(PlayerEntity player, ShopComponent shop) {
        player.sendMessage(Text.literal("Do you really want to close the shop? If you want, click ").append(Text.literal("here.").setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/shop close")).withUnderline(true))));
        long start = System.currentTimeMillis();
        scheduleInput(InputType.CLOSE, player).thenAccept((v) -> {
            if (System.currentTimeMillis() < start + 10000) {
                ShopModification.delShopModification().apply(shop);
                player.sendMessage(Text.literal("Shop sucessful closed"));
            }
        });
    }

    public static void confirmPayButton(PlayerEntity sender, UUID receiver, double amount, BlockPos pos, World world) {
        Optional<GameProfile> profile = sender.getServer().getUserCache().getByUuid(receiver);
        String name = "unknown";
        if(profile.isPresent()) {
            name = profile.get().getName();
        }
        sender.sendMessage(Text.translatable("servermoney.input.paybutton.confirm", name, amount, ServerMoneyConfig.moneySymbol));
        long start = System.currentTimeMillis();
        scheduleInput(InputType.CONFIRM_PAY, sender).thenAccept((v) -> {
            if (System.currentTimeMillis() < start + 10000 && world.getBlockEntity(pos) instanceof PayButtonBlockEntity pbbe) {
                if(pbbe.getOwner().equals(receiver) && MoneyStorage.tryPay(sender.getUuid(), receiver, amount)) {
                    ServerMoney.PAY_BUTTON_BLOCK.onPay(world.getBlockState(pos), world, pos, sender);
                }
            }
        });
    }

    /**
     * Marks the player as selecting a value
     * @param type The type of the input
     * @param player The player that should select the value
     * @return A CompletableFuture that completes when player has selected a value
     * @param <T> The type of the input
     */
    public static <T> CompletableFuture<T> scheduleInput(InputType<T> type, PlayerEntity player){
        CompletableFuture<T> future = new CompletableFuture<>();
        ShopModificationComponent component = ShopModificationStorage.getData(player);
        component.setScheduledInput(future, type);
        return future;
    }

    public static class InputType<T> {
        public static final InputType<Double> PRICE = new InputType<>();
        public static final InputType<Integer> AMOUNT = new InputType<>();
        public static final InputType<BlockEntity> SHOP = new InputType<>();
        public static final InputType<Void> CLOSE = new InputType<>();
        public static final InputType<Void> CONFIRM_PAY = new InputType<>();
        private InputType() {

        }
    }
}
