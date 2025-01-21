package io.github.skippyall.servermoney;

import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.api.item.PolymerBlockItem;
import io.github.skippyall.servermoney.paybutton.PayButtonBlock;
import io.github.skippyall.servermoney.paybutton.PayButtonBlockEntity;
import io.github.skippyall.servermoney.shop.block.ShopBarrelBlock;
import io.github.skippyall.servermoney.shop.block.ShopBarrelBlockEntity;
import io.github.skippyall.servermoney.shop.block.ShopChestBlock;
import io.github.skippyall.servermoney.shop.block.ShopChestBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class MoneyBlocks {
    public static final RegisteredBlock<ShopBarrelBlock, PolymerBlockItem, BlockEntityType<ShopBarrelBlockEntity>> SHOP_BARREL = register(
            Identifier.of(ServerMoney.MOD_ID, "shop_barrel"),
            ShopBarrelBlock::new,
            AbstractBlock.Settings.copy(Blocks.BARREL),
            new Item.Settings(),
            Items.BARREL,
            ShopBarrelBlockEntity::new
    );

    public static final RegisteredBlock<ShopChestBlock, PolymerBlockItem, BlockEntityType<ShopChestBlockEntity>> SHOP_CHEST = register(
            Identifier.of(ServerMoney.MOD_ID, "shop_chest"),
            ShopChestBlock::new,
            AbstractBlock.Settings.copy(Blocks.CHEST),
            new Item.Settings(),
            Items.CHEST,
            ShopChestBlockEntity::new
    );

    public static final RegisteredBlock<PayButtonBlock, PolymerBlockItem, BlockEntityType<PayButtonBlockEntity>> PAY_BUTTON = register(
            Identifier.of(ServerMoney.MOD_ID, "pay_button"),
            PayButtonBlock::new,
            AbstractBlock.Settings.copy(Blocks.POLISHED_BLACKSTONE_BUTTON),
            new Item.Settings(),
            Items.POLISHED_BLACKSTONE_BUTTON,
            PayButtonBlockEntity::new
    );

    public static <B extends Block, E extends BlockEntity> RegisteredBlock<B, PolymerBlockItem, BlockEntityType<E>> register(Identifier id, Function<AbstractBlock.Settings, B> blockFactory, AbstractBlock.Settings settings, Item.Settings itemSettings, Item vanillaItem, FabricBlockEntityTypeBuilder.Factory<E> factory) {
        B block = blockFactory.apply(settings.registryKey(RegistryKey.of(RegistryKeys.BLOCK, id)));
        BlockEntityType<E> blockEntityType = Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create(factory, block).build());
        PolymerBlockUtils.registerBlockEntity(blockEntityType);
        return register(id, block, itemSettings, vanillaItem).withBlockEntityType(
                blockEntityType
        );
    }

    public static <B extends Block> RegisteredBlock<B, PolymerBlockItem, Void> register(Identifier id, B block, Item.Settings itemSettings, Item vanillaItem) {
        return register(id, block, new PolymerBlockItem(block, itemSettings.registryKey(RegistryKey.of(RegistryKeys.ITEM, id)), vanillaItem));
    }

    public static <B extends Block, I extends BlockItem, E extends BlockEntity> RegisteredBlock<B, I, BlockEntityType<E>> register(Identifier id, B block, I item, FabricBlockEntityTypeBuilder.Factory<E> factory) {
        BlockEntityType<E> blockEntityType = Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create(factory, block).build(null));
        PolymerBlockUtils.registerBlockEntity(blockEntityType);
        return register(id, block, item).withBlockEntityType(
                blockEntityType
        );
    }

    public static <B extends Block, I extends BlockItem> RegisteredBlock<B, I, Void> register(Identifier id, B block, I item) {
        return new RegisteredBlock<>(
                Registry.register(Registries.BLOCK, id, block),
                Registry.register(Registries.ITEM, id, item),
                null
        );
    }

    public static void register() {
    }

    public record RegisteredBlock<B extends Block, I extends BlockItem, T>(B block, I item, T blockEntityType) {
        public <T2> RegisteredBlock<B, I, T2> withBlockEntityType(T2 blockEntityType) {
            return new RegisteredBlock<>(block, item, blockEntityType);
        }
    }
}
