package io.github.skippyall.servermoney.shop;

import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import io.github.skippyall.servermoney.shop.block.ShopBlockEntity;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BreakShopEvent implements PlayerBlockBreakEvents.Before {
    @Override
    public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        if (ServerMoneyConfig.protectShops && blockEntity instanceof ShopBlockEntity shop) {
            return player.getUuid().equals(shop.getShop().getShopOwner());
        } else {
            return true;
        }
    }
}
