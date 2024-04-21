package io.github.skippyall.servermoney.shop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.servermoney.ServerMoney;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class ShopAttachment {
    public static final Codec<ShopAttachment> CODEC = RecordCodecBuilder.create(instance -> {
        return instance.group(
                Uuids.CODEC.fieldOf("owner").forGetter(ShopAttachment::getShopOwner),
                ItemStack.CODEC.fieldOf("stack").forGetter(ShopAttachment::getStack),
                Codec.DOUBLE.fieldOf("price").forGetter(ShopAttachment::getPrice)
        ).apply(instance, ShopAttachment::new);
    });

    public static final AttachmentType<ShopAttachment> SHOP_ATTACHMENT = AttachmentRegistry.createPersistent(new Identifier(ServerMoney.MOD_ID, "shop"), CODEC);

    private UUID shopOwner;
    private ItemStack stack;
    private double price;

    public ShopAttachment(UUID shopOwner, ItemStack stack, double price) {
        this.shopOwner = shopOwner;
        this.stack = stack;
        this.price = price;
    }

    public ShopAttachment() {
        this.shopOwner = null;
        this.stack = ItemStack.EMPTY;
        this.price = 0;
    }

    public static ShopAttachment getAttachment(BlockEntity blockEntity) {
        return blockEntity.getAttached(SHOP_ATTACHMENT);
    }

    public static void addShop(BlockEntity blockEntity, ShopAttachment attachment) {
        blockEntity.setAttached(SHOP_ATTACHMENT, attachment);
    }

    public static boolean isShop(BlockEntity blockEntity) {
        return blockEntity.hasAttached(SHOP_ATTACHMENT);
    }

    public static void removeShop(BlockEntity blockEntity) {
        blockEntity.removeAttached(SHOP_ATTACHMENT);
    }

    public UUID getShopOwner() {
        return shopOwner;
    }

    public ItemStack getStack() {
        return stack;
    }

    public double getPrice() {
        return price;
    }

    public void setShopOwner(BlockEntity blockEntity, UUID shopOwner) {
        this.shopOwner = shopOwner;
        blockEntity.markDirty();
    }

    public void setStack(BlockEntity blockEntity, ItemStack stack) {
        this.stack = stack;
        blockEntity.markDirty();
    }

    public void setPrice(BlockEntity blockEntity, double price) {
        this.price = price;
        blockEntity.markDirty();
    }

    public static void register() {

    }
}
