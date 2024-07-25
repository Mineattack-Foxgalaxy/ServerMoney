package io.github.skippyall.servermoney.shop.modification;

import io.github.skippyall.servermoney.input.Input;
import io.github.skippyall.servermoney.shop.block.ShopBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

/**
 * A class that represents a modification of an unknown shop. By default, the modification does nothing.
 * Modifications can be added using the {@code modifyPropertyName()} methods.
 * Can be used for {@link Input#selectShop(PlayerEntity, ShopModification)} to express what should be done
 * with the selected shop.
 */
public class ShopModification {
    public ShopModification(){}

    private boolean shopOwnerSet = false;
    private UUID shopOwner = null;

    private boolean amountSet = false;
    private int amount = 0;

    private boolean itemSet = false;
    private ItemVariant item = null;

    private boolean priceSet = false;
    private double price = 0;

    private final Set<Predicate<ShopBlockEntity>> predicates = new HashSet<>();

    public ShopModification modifyShopOwner(@Nullable UUID owner) {
        this.shopOwner = owner;
        shopOwnerSet = true;
        return this;
    }

    public ShopModification modifyItem(ItemVariant item) {
        this.item = item;
        itemSet = true;
        return this;
    }

    public ShopModification modifyAmount(int amount) {
        this.amount = amount;
        amountSet = true;
        return this;
    }

    public ShopModification modifyPrice(double price) {
        this.price = price;
        priceSet = true;
        return this;
    }

    /**
     * Adds a condition to this {@code ShopModification}. The changes will get applied only if all predicates
     * return true.
     */
    public ShopModification addPredicate(Predicate<ShopBlockEntity> predicate) {
        predicates.add(predicate);
        return this;
    }

    /**
     * Applies this {@code ShopModification} to the specified shop.
     * @param shop The shop to apply the changes to.
     */
    public void apply(ShopBlockEntity shop) {
        for(Predicate<ShopBlockEntity> predicate : predicates){
            if(!predicate.test(shop)){
                return;
            }
        }

        if(shopOwnerSet) {
            shop.setShopOwner(shopOwner);
        }
        if(itemSet) {
            shop.setItem(item);
        }
        if(amountSet) {
            shop.setCount(amount);
        }
        if(priceSet) {
            shop.setPrice(price);
        }
    }
}
