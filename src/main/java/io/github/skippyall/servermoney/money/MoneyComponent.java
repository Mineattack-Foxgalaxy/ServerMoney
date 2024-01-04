package io.github.skippyall.servermoney.money;

import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.nbt.NbtCompound;

public class MoneyComponent implements PlayerComponent<MoneyComponent> {
    public static final String KEY = "money";
    long money = 0;
    @Override
    public void readFromNbt(NbtCompound tag) {
        money = tag.getLong("money");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putLong(KEY, money);
    }
}
