package io.github.skippyall.servermoney.shop.modification;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import io.github.skippyall.servermoney.ServerMoney;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ShopModificationStorage implements EntityComponentInitializer {
    public static final ComponentKey<ShopModificationComponent> KEY = ComponentRegistry.getOrCreate(new Identifier(ServerMoney.MOD_ID,"shopcreate"), ShopModificationComponent.class);
    public static ShopModificationComponent getData(PlayerEntity player) {
        return KEY.get(player);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(KEY, player->new ShopModificationComponent());
    }
}
