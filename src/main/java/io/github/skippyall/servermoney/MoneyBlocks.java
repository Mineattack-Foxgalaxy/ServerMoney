package io.github.skippyall.servermoney;

import io.github.skippyall.servermoney.paybutton.PayButtonBlock;
import io.github.skippyall.servermoney.paybutton.PayButtonBlockEntity;
import io.github.skippyall.servermoney.shop.block.ShopBarrelBlock;
import io.github.skippyall.servermoney.shop.block.ShopBarrelBlockEntity;
import mineattack.customthings.api.CustomBlockEntityType;
import mineattack.customthings.api.CustomBlockItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MoneyBlocks {
    public static final RegisteredBlock<ShopBarrelBlock, BlockEntityType<ShopBarrelBlockEntity>> SHOP_BARREL = register(
            Identifier.of(ServerMoney.MOD_ID, "shop_barrel"),
            new ShopBarrelBlock(),
            new Item.Settings(),
            Items.BARREL,
            ShopBarrelBlockEntity::new
    );

    public static final RegisteredBlock<PayButtonBlock, BlockEntityType<PayButtonBlockEntity>> PAY_BUTTON = register(
            Identifier.of(ServerMoney.MOD_ID, "pay_button"),
            new PayButtonBlock(),
            new Item.Settings(),
            Items.POLISHED_BLACKSTONE_BUTTON,
            PayButtonBlockEntity::new
    );

    public static <B extends Block, E extends BlockEntity> RegisteredBlock<B, BlockEntityType<E>> register(Identifier id, B block, Item.Settings itemSettings, Item vanillaItem, BlockEntityType.BlockEntityFactory<?> factory) {
        return register(id, block, itemSettings, vanillaItem).withBlockEntityType(
                Registry.register(Registries.BLOCK_ENTITY_TYPE, id, CustomBlockEntityType.Builder.create(factory, block).build())
        );
    }

    public static <B extends Block> RegisteredBlock<B, Void> register(Identifier id, B block, Item.Settings itemSettings, Item vanillaItem) {
        return new RegisteredBlock<>(
                Registry.register(Registries.BLOCK, id, block),
                Registry.register(Registries.ITEM, id, new CustomBlockItem(block, itemSettings, vanillaItem)),
                null
        );
    }

    public static void register() {

    }

    public record RegisteredBlock<B extends Block, T>(B block, BlockItem item, T blockEntityType) {
        public <T2> RegisteredBlock<B, T2> withBlockEntityType(T2 blockEntityType) {
            return new RegisteredBlock<>(block, item, blockEntityType);
        }
    }
}
