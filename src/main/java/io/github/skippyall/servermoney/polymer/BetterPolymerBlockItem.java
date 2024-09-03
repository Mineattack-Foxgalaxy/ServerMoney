package io.github.skippyall.servermoney.polymer;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class BetterPolymerBlockItem extends BlockItem implements PolymerItem {
    Item vanillaItem;
    public BetterPolymerBlockItem(Block block, Settings settings, Item vanillaItem) {
        super(block, settings);
        this.vanillaItem = vanillaItem;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        if(PolymerUtil.shouldReplaceItem(player)) {
            return vanillaItem;
        } else {
            return itemStack.getItem();
        }
    }
}
