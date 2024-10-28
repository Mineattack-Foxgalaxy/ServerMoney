package io.github.skippyall.servermoney.shop.block;

import io.github.skippyall.servermoney.MoneyBlocks;
import io.github.skippyall.servermoney.shop.Shop;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class ShopBarrelBlockEntity extends BarrelBlockEntity implements InventoryShopBlockEntity {
    private final Shop shop = new Shop();
    private final InventoryStorage storage = InventoryStorage.of(this, null);

    public ShopBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        type = MoneyBlocks.SHOP_BARREL.blockEntityType();
    }

    @Override
    public Shop getShop() {
        return shop;
    }

    @Override
    public Storage<ItemVariant> getStorage() {
        return storage;
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.put("shop", shop.encode(registryLookup));
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        shop.decode(nbt.getCompound("shop"), registryLookup);
    }
}
