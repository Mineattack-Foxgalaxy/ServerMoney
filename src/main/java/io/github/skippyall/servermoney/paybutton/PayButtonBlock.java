package io.github.skippyall.servermoney.paybutton;

import eu.pb4.polymer.core.api.block.PolymerBlock;
import io.github.skippyall.servermoney.input.Input;
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
import org.jetbrains.annotations.Nullable;

public class PayButtonBlock extends ButtonBlock implements BlockEntityProvider, PolymerBlock {
    public PayButtonBlock() {
        super(BlockSetType.GOLD, 30, Blocks.POLISHED_BLACKSTONE_BUTTON.getSettings());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PayButtonBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!state.get(POWERED)) {
            if(!world.isClient() && world.getBlockEntity(pos) instanceof PayButtonBlockEntity pbbe) {
                Input.confirmPayButton(player, pbbe.getOwner(), pbbe.getAmount(), pos, world);
            }
            return ActionResult.success(world.isClient());
        }
        return ActionResult.CONSUME;
    }

    public void onPay(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        super.onUse(state, world, pos, player, null);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity be = world.getBlockEntity(pos);
        if(be instanceof PayButtonBlockEntity pbe && placer instanceof ServerPlayerEntity player) {
            pbe.setOwner(player.getUuid());
            String name = itemStack.getName().getLiteralString();
            if(name != null) {
                try {
                    pbe.setAmount(Double.parseDouble(name));
                } catch (NumberFormatException ignored) {}
            }
        }
    }

    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.POLISHED_BLACKSTONE_BUTTON.getStateWithProperties(state);
    }
}
