package io.github.skippyall.servermoney.shop;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import io.github.skippyall.servermoney.ServerMoney;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class ShopCreateStorage implements EntityComponentInitializer {
    public static final ComponentKey<ShopCreateComponent> KEY = ComponentRegistry.getOrCreate(new Identifier(ServerMoney.MOD_ID,"shopcreate"), ShopCreateComponent.class);
    public static ShopCreateComponent getData(PlayerEntity player) {
        return KEY.get(player);
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(KEY, player->new ShopCreateComponent());
    }
}
