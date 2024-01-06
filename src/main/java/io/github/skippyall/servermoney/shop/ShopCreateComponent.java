package io.github.skippyall.servermoney.shop;

import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import net.minecraft.nbt.NbtCompound;

public class ShopCreateComponent implements PlayerComponent<ShopCreateComponent> {
    public static final String KEY = "money";
    private static final String IS_CREATING_SHOP_KEY = "is_creating_shop";
    public boolean isCreatingShop = false;
    public ShopComponent shopCreating;

    @Override
    public void readFromNbt(NbtCompound tag) {}

    @Override
    public void writeToNbt(NbtCompound tag) {}
}
