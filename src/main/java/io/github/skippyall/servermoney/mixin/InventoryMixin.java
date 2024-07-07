package io.github.skippyall.servermoney.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.inventory.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Inventory.class)
public class InventoryMixin {
    @ModifyReturnValue(method = "canTransferTo", at = @At("RETURN"))
    private boolean preventShopTransfer(boolean transfer) {
        /*if(transfer) {
            if((Object)this instanceof BlockEntity be) {
                if(ShopAttachment.isShop(be)) {
                    return false;
                }
            }
        }*/
        return transfer;
    }
}
