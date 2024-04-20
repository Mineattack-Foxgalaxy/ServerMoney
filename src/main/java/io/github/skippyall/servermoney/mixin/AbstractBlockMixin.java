package io.github.skippyall.servermoney.mixin;

import io.github.skippyall.servermoney.config.ServerMoneyConfig;
import io.github.skippyall.servermoney.shop.ShopComponent;
import io.github.skippyall.servermoney.shop.ShopResendCallback;
import io.github.skippyall.servermoney.shop.ShopStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.tick.SimpleTickScheduler;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallback;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.ScheduledFuture;
import java.util.function.BiConsumer;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Inject(method = "onExploded", at = @At("HEAD"), cancellable = true)
    public void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger, CallbackInfo ci){
        if(ServerMoneyConfig.protectShops) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be == null) return;
            ShopComponent shop = ShopStorage.getShop(be);
            if (shop != null && shop.isShop) {
                if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
                    Timer<MinecraftServer> timer = world.getServer().getSaveProperties().getMainWorldProperties().getScheduledEvents();
                    timer.setEvent("shopresend_" + ShopResendCallback.counter, world.getTime()+ 20, new ShopResendCallback(state, pos, world));
                    ShopResendCallback.counter++;
                }
                ci.cancel();
            }
        }
    }
}
