package io.github.skippyall.servermoney.shop.modification;

import io.github.skippyall.servermoney.input.Input;
import io.github.skippyall.servermoney.shop.ShopComponent;
import io.github.skippyall.servermoney.util.NullableOptional;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;
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
    private Optional<Boolean> isShop = Optional.empty();
    private NullableOptional<UUID> shopOwner = NullableOptional.empty();
    private NullableOptional<Item> item = NullableOptional.empty();
    private OptionalInt amount = OptionalInt.empty();
    private OptionalDouble price = OptionalDouble.empty();

    private Set<Predicate<ShopComponent>> predicates = new HashSet<>();


    public ShopModification modifyIsShop(boolean isShop) {
        this.isShop = Optional.of(isShop);
        return this;
    }

    public ShopModification modifyShopOwner(@Nullable UUID owner) {
        this.shopOwner = NullableOptional.of(owner);
        return this;
    }

    public ShopModification modifyItem(@Nullable Item item) {
        this.item = NullableOptional.of(item);
        return this;
    }
    public ShopModification modifyAmount(int amount) {
        this.amount = OptionalInt.of(amount);
        return this;
    }
    public ShopModification modifyPrice(double price) {
        this.price = OptionalDouble.of(price);
        return this;
    }

    /**
     * Adds a condition to this {@code ShopModification}. The changes will get applied only if all predicates
     * return true.
     */
    public ShopModification addPredicate(Predicate<ShopComponent> predicate) {
        predicates.add(predicate);
        return this;
    }

    /**
     * Applies this {@code ShopModification} to the specified shop.
     * @param shop The shop to apply the changes to.
     */
    public void apply(@NotNull ShopComponent shop) {
        for(Predicate<ShopComponent> predicate : predicates){
            if(!predicate.test(shop)){
                return;
            }
        }
        isShop.ifPresent(value -> shop.isShop = value);
        shopOwner.ifPresent(value -> shop.shopOwner = value);
        item.ifPresent(value -> shop.item = value);
        amount.ifPresent(value -> shop.amount = value);
        price.ifPresent(value -> shop.price = value);
    }

    /**
     * @return A {@code ShopModification} that deletes the shop that it is applied to.
     */
    public static ShopModification delShopModification(){
        return new ShopModification().modifyIsShop(false).modifyShopOwner(null).modifyItem(null).modifyAmount(0).modifyPrice(0);
    }
}
