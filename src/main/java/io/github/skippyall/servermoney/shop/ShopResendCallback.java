package io.github.skippyall.servermoney.shop;

import io.github.skippyall.servermoney.ServerMoney;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallback;

public record ShopResendCallback(BlockState state, BlockPos pos, World world) implements TimerCallback<MinecraftServer> {
    public static long counter = 0;
    @Override
    public void call(MinecraftServer server, Timer<MinecraftServer> events, long time) {
        world.getServer().getPlayerManager().sendToDimension(new BlockUpdateS2CPacket(pos, state), world.getRegistryKey());
    }

    public static class ShopResendSerializer extends Serializer<MinecraftServer, ShopResendCallback> {
        public ShopResendSerializer() {
            super(new Identifier(ServerMoney.MOD_ID, "shopresend"), ShopResendCallback.class);
        }

        @Override
        public void serialize(NbtCompound nbt, ShopResendCallback callback) {

        }

        @Override
        public ShopResendCallback deserialize(NbtCompound nbt) {
            return null;
        }
    }
}
