package io.github.skippyall.servermoney.shop.block;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import io.github.skippyall.servermoney.ServerMoney;
import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import io.github.skippyall.servermoney.input.Input;
import io.github.skippyall.servermoney.input.InputAttachment;
import io.github.skippyall.servermoney.shop.ShopResendCallback;
import io.github.skippyall.servermoney.shop.ShopScreenHandler;
import io.github.skippyall.servermoney.shop.OwnerShopScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.timer.Timer;
import org.jetbrains.annotations.Nullable;

public interface ShopBlock extends PolymerBlock, PolymerClientDecoded, PolymerKeepModel {
    default ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(player.isSpectator() || player.isSneaking()) return ActionResult.PASS;

        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof ShopBlockEntity shop) {
            if(InputAttachment.hasInputType(player, Input.InputType.SHOP)){
                InputAttachment modificationComponent = InputAttachment.getScheduledInput(player);
                modificationComponent.getCompletableFuture(Input.InputType.SHOP).complete(shop);
                InputAttachment.removeScheduledInput(player);

                player.sendMessage(Text.literal("Successfully selected shop."));

                return ActionResult.SUCCESS;
            }

            if (player.getGameProfile().getId().equals(shop.getShopOwner())) {
                OwnerShopScreenHandler.openOwnerShopScreen(player, shop);
            } else {
                if(!shop.getItem().isBlank()) {
                    ShopScreenHandler.openShopScreen(player, shop);
                }
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    default boolean shouldExplode(BlockState state, World world, BlockPos pos) {
        if(ServerMoneyConfig.protectShops) {
            if (state.getBlock() instanceof ShopBlock) {
                if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
                    Timer<MinecraftServer> timer = world.getServer().getSaveProperties().getMainWorldProperties().getScheduledEvents();
                    timer.setEvent("shopresend_" + ShopResendCallback.counter, world.getTime()+ 20, new ShopResendCallback(state, pos, world.getRegistryKey()));
                    ShopResendCallback.counter++;
                }
                return false;
            }
        }
        return true;
    }

    default void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof ShopBlockEntity pbe && placer instanceof ServerPlayerEntity player) {
            pbe.setShopOwner(player.getUuid());
        }
    }

    Block getVanillaBlock();

    @Override
    default BlockState getPolymerBlockState(BlockState state) {
        return getVanillaBlock().getStateWithProperties(state);
    }

    @Override
    default BlockState getPolymerBlockState(BlockState state, ServerPlayerEntity player) {
        if(ServerPlayNetworking.canSend(player, ServerMoney.PACKET_ID)) {
            return getPolymerBlockState(state);
        } else {
            return state;
        }
    }
}
