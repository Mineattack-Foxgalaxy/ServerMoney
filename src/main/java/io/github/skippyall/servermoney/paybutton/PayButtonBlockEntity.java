package io.github.skippyall.servermoney.paybutton;

import io.github.skippyall.servermoney.ServerMoney;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class PayButtonBlockEntity extends BlockEntity {
    private UUID owner;
    private double amount;

    public PayButtonBlockEntity(BlockPos pos, BlockState state) {
        super(ServerMoney.PAY_BUTTON_BET, pos, state);
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        markDirty();
    }

    public void setAmount(double amount) {
        this.amount = amount;
        markDirty();
    }

    public UUID getOwner() {
        return owner;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        nbt.putUuid("owner", owner);
        nbt.putDouble("amount", amount);
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        owner = nbt.getUuid("owner");
        amount = nbt.getDouble("amount");
    }
}
