package io.github.skippyall.servermoney.money;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class MoneyStorage implements EntityComponentInitializer {
    public static final ComponentKey<MoneyComponent> KEY = ComponentRegistry.getOrCreate(new Identifier("servermoney","money"), MoneyComponent.class);
    public static long getMoney(PlayerEntity player){
        MoneyComponent capability = KEY.get(player);
        return capability.money;
    }

    public static void setMoney(PlayerEntity player, long money){
        MoneyComponent capability = KEY.get(player);
        capability.money = money;
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(KEY, player->new MoneyComponent());
    }
}
