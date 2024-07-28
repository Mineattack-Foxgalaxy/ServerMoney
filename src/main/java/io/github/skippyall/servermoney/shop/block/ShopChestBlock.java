package io.github.skippyall.servermoney.shop.block;

import io.github.skippyall.servermoney.MoneyBlocks;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class ShopChestBlock extends ChestBlock implements ShopBlock {
    public ShopChestBlock() {
        super(Blocks.CHEST.getSettings(), () -> MoneyBlocks.SHOP_CHEST.blockEntityType());
    }

    @Override
    public Block getVanillaBlock() {
        return Blocks.CHEST;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ShopChestBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return ShopBlock.super.onUse(state, world, pos, player, hit);
    }

    @Override
    public void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if(shouldExplode(state, world, pos)) {
            super.onExploded(state, world, pos, explosion, stackMerger);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        ShopBlock.super.onPlaced(world, pos, state, placer, itemStack);
    }
}