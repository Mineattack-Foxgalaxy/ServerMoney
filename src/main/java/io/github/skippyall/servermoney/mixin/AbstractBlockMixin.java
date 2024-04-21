package io.github.skippyall.servermoney.mixin;

import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import io.github.skippyall.servermoney.shop.ShopAttachment;
import io.github.skippyall.servermoney.shop.ShopResendCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.timer.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Inject(method = "onExploded", at = @At("HEAD"), cancellable = true)
    public void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger, CallbackInfo ci){
        if(ServerMoneyConfig.protectShops) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be == null) return;
            if (ShopAttachment.isShop(be)) {
                if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
                    Timer<MinecraftServer> timer = world.getServer().getSaveProperties().getMainWorldProperties().getScheduledEvents();
                    timer.setEvent("shopresend_" + ShopResendCallback.counter, world.getTime()+ 20, new ShopResendCallback(state, pos, world.getRegistryKey()));
                    ShopResendCallback.counter++;
                }
                ci.cancel();
            }
        }
    }
}
