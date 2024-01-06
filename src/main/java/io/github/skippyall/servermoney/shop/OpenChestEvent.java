package io.github.skippyall.servermoney.shop;

import io.github.skippyall.servermoney.ServerMoney;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import java.util.Set;

public class OpenChestEvent implements UseBlockCallback {
    public static final Set<Class<? extends BlockEntity>> VALID_BLOCK_ENTITIES = Set.of(ChestBlockEntity.class, BarrelBlockEntity.class, ShulkerBoxBlockEntity.class);
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if(player.isSpectator()) return ActionResult.PASS;

        BlockEntity be = world.getBlockEntity(hitResult.getBlockPos());
        if(be==null) return ActionResult.PASS;
        ShopComponent shop = ShopStorage.getShop(be);
        if(shop == null) return ActionResult.PASS;
        if(shop.isShop && !player.getGameProfile().getId().equals(shop.shopOwner)) {
            Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, hitResult.getBlockPos(), null);
            if(storage == null) {
                ServerMoney.LOGGER.warn("No storage found on " + Registries.BLOCK.getId(world.getBlockState(hitResult.getBlockPos()).getBlock())+". It should be removed from valid block entities list.");
            }
            ShopInventoryFactory.openShopInventory(shop.item, shop.amount, shop.price, player, storage, shop.shopOwner);
            return ActionResult.SUCCESS;
        } else if(ShopCreateStorage.getData(player).isCreatingShop && !shop.isShop){
            ShopCreateComponent createComponent = ShopCreateStorage.getData(player);
            shop.copyFrom(createComponent.shopCreating);
            createComponent.isCreatingShop = false;
            createComponent.shopCreating = null;
            player.sendMessage(Text.literal("Your shop was created."));
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }

    public static ShopComponent getShopIfExisting(BlockEntity be){
        ShopComponent shop = ShopStorage.getShop(be);
        if((shop != null) && shop.isShop) {
            return shop;
        } else {
            return null;
        }
    }
}
