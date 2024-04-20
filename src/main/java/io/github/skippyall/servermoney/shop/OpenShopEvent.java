package io.github.skippyall.servermoney.shop;

import io.github.skippyall.servermoney.ServerMoney;
import io.github.skippyall.servermoney.input.Input;
import io.github.skippyall.servermoney.shop.modification.ShopModificationComponent;
import io.github.skippyall.servermoney.shop.modification.ShopModificationStorage;
import io.github.skippyall.servermoney.shop.owner.OwnerShopInventoryFactory;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import java.util.Set;

public class OpenShopEvent implements UseBlockCallback {
    public static final Set<Class<? extends BlockEntity>> VALID_BLOCK_ENTITIES = Set.of(ChestBlockEntity.class, BarrelBlockEntity.class, ShulkerBoxBlockEntity.class);
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if(player.isSpectator() || player.isSneaking()) return ActionResult.PASS;

        BlockEntity be = world.getBlockEntity(hitResult.getBlockPos());
        if(be==null) return ActionResult.PASS;

        ShopComponent shop = ShopStorage.getShop(be);
        if(shop == null) return ActionResult.PASS;

        if(ShopModificationStorage.getData(player).getInputType() == Input.InputType.SHOP){
            ShopModificationComponent modificationComponent = ShopModificationStorage.getData(player);
            modificationComponent.getCompletableFuture(Input.InputType.SHOP).complete(be);
            player.sendMessage(Text.literal("Successfully selected shop."));
            modificationComponent.reset();
            return ActionResult.SUCCESS;
        }

        if(shop.isShop) {
            Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, hitResult.getBlockPos(), null);
            if(storage == null) {
                ServerMoney.LOGGER.warn("No storage found on " + Registries.BLOCK.getId(world.getBlockState(hitResult.getBlockPos()).getBlock())+". It should be removed from valid block entities list.");
            }
            if (player.getGameProfile().getId().equals(shop.shopOwner)) {
                OwnerShopInventoryFactory.openOwnerShopInventory(player, shop, be, storage);
            }else {
                ShopInventoryFactory.openShopInventory(shop.item, shop.amount, shop.price, player, storage, shop.shopOwner);
            }
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }
}
