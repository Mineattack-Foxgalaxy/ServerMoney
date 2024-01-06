package io.github.skippyall.servermoney.shop;

import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import io.github.skippyall.servermoney.ServerMoney;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.util.Identifier;

public class ShopStorage implements BlockComponentInitializer {
    private static final ComponentKey<ShopComponent> KEY = ComponentRegistry.getOrCreate(new Identifier(ServerMoney.MOD_ID,"shop"), ShopComponent.class);
    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
        for (Class<? extends BlockEntity> beclazz:OpenChestEvent.VALID_BLOCK_ENTITIES) {
            registry.registerFor(beclazz, KEY, be -> new ShopComponent());
        }
    }

    public static ShopComponent getShop(BlockEntity be) {
        return KEY.getNullable(be);
    }
}
