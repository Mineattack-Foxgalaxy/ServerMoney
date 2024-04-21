package io.github.skippyall.servermoney.shop.modification;

import io.github.skippyall.servermoney.input.Input;
import io.github.skippyall.servermoney.shop.ShopAttachment;
import io.github.skippyall.servermoney.util.NullableOptional;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
    private OptionalInt amount = OptionalInt.empty();
    private Optional<ItemStack> stack = Optional.empty();
    private OptionalDouble price = OptionalDouble.empty();

    private Set<Predicate<BlockEntity>> predicates = new HashSet<>();


    public ShopModification addShop() {
        this.isShop = Optional.of(true);
        return this;
    }

    public ShopModification modifyShopOwner(@Nullable UUID owner) {
        this.shopOwner = NullableOptional.of(owner);
        return this;
    }

    public ShopModification modifyStack(ItemStack stack) {
        this.stack = Optional.of(stack);
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
    public ShopModification addPredicate(Predicate<BlockEntity> predicate) {
        predicates.add(predicate);
        return this;
    }

    /**
     * Applies this {@code ShopModification} to the specified shop.
     * @param be The shop to apply the changes to.
     */
    public void apply(BlockEntity be) {
        for(Predicate<BlockEntity> predicate : predicates){
            if(!predicate.test(be)){
                return;
            }
        }
        if(isShop.isPresent() && isShop.get()) {
            ShopAttachment.addShop(be, new ShopAttachment());
        }
        if(ShopAttachment.isShop(be)) {
            if(isShop.isPresent() && !isShop.get()) {
                ShopAttachment.removeShop(be);
            }
            ShopAttachment shop = ShopAttachment.getAttachment(be);
            shopOwner.ifPresent(value -> shop.setShopOwner(be, value));
            stack.ifPresent(value -> shop.setStack(be, value));
            amount.ifPresent(value -> shop.setStack(be, shop.getStack().copyWithCount(value)));
            price.ifPresent(value -> shop.setPrice(be, value));
        }
    }

    /**
     * @return A {@code ShopModification} that deletes the shop that it is applied to.
     */
    public static ShopModification delShopModification(){
        ShopModification mod = new ShopModification();
        mod.isShop = Optional.of(false);
        return mod;
    }
}
