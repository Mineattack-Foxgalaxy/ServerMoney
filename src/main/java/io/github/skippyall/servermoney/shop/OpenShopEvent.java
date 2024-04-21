package io.github.skippyall.servermoney.shop;

import io.github.skippyall.servermoney.ServerMoney;
import io.github.skippyall.servermoney.input.Input;
import io.github.skippyall.servermoney.input.InputAttachment;
import io.github.skippyall.servermoney.shop.owner.OwnerShopInventoryFactory;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import java.util.Set;

public class OpenShopEvent implements UseBlockCallback {
    public static final Set<BlockEntityType<?>> VALID_BLOCK_ENTITY_TYPES = Set.of(BlockEntityType.CHEST, BlockEntityType.BARREL, BlockEntityType.SHULKER_BOX);
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if(player.isSpectator() || player.isSneaking()) return ActionResult.PASS;

        BlockEntity be = world.getBlockEntity(hitResult.getBlockPos());
        if(be==null) return ActionResult.PASS;


        if(!VALID_BLOCK_ENTITY_TYPES.contains(be.getType())) return ActionResult.PASS;

        if(InputAttachment.hasInputType(player, Input.InputType.SHOP)){
            InputAttachment modificationComponent = InputAttachment.getScheduledInput(player);
            modificationComponent.getCompletableFuture(Input.InputType.SHOP).complete(be);
            InputAttachment.removeScheduledInput(player);

            player.sendMessage(Text.literal("Successfully selected shop."));

            return ActionResult.SUCCESS;
        }

        if(ShopAttachment.isShop(be)) {
            ShopAttachment shop = ShopAttachment.getAttachment(be);
            Storage<ItemVariant> storage = ItemStorage.SIDED.find(world, hitResult.getBlockPos(), null);
            if(storage == null) {
                ServerMoney.LOGGER.warn("No storage found on " + Registries.BLOCK.getId(world.getBlockState(hitResult.getBlockPos()).getBlock())+". It should be removed from valid block entities list.");
            }
            if (player.getGameProfile().getId().equals(shop.getShopOwner())) {
                OwnerShopInventoryFactory.openOwnerShopInventory(player, shop, be, storage);
            }else {
                ShopInventoryFactory.openShopInventory(shop.getStack(), shop.getPrice(), player, storage, shop.getShopOwner());
            }
            return ActionResult.SUCCESS;
        } else {
            return ActionResult.PASS;
        }
    }
}
