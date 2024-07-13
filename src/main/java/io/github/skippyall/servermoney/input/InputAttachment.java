package io.github.skippyall.servermoney.input;

import io.github.skippyall.servermoney.ServerMoney;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class InputAttachment {
    public static final AttachmentType<InputAttachment> INPUT_ATTACHMENT = AttachmentRegistry.createDefaulted(Identifier.of(ServerMoney.MOD_ID, "input"), InputAttachment::new);
    public static final InputAttachment NULL_INPUT = new InputAttachment();
    private CompletableFuture<?> scheduledInput = null;
    private Input.InputType<?>  inputType = null;

    public InputAttachment() {

    }

    public static <T> void setScheduledInput(PlayerEntity player, CompletableFuture<T> scheduledInput, Input.InputType<T> inputType) {
        InputAttachment input = ((AttachmentTarget)player).getAttachedOrCreate(INPUT_ATTACHMENT);
        input.scheduledInput = scheduledInput;
        input.inputType = inputType;
    }

    public static <T> CompletableFuture<T> getCompletableFuture(PlayerEntity player, Input.InputType<T> inputType){
        return getScheduledInput(player).getCompletableFuture(inputType);
    }

    public static InputAttachment getScheduledInput(PlayerEntity player){
        return ((AttachmentTarget)player).getAttachedOrElse(INPUT_ATTACHMENT, NULL_INPUT);
    }

    public static void removeScheduledInput(PlayerEntity player) {
        ((AttachmentTarget)player).removeAttached(INPUT_ATTACHMENT);
    }

    public static boolean hasInputType(PlayerEntity player, Input.InputType<?> inputType){
        return getScheduledInput(player).getInputType() == inputType;
    }

    @SuppressWarnings("unchecked")
    public <T> CompletableFuture<T> getCompletableFuture(Input.InputType<T> inputType){
        if(inputType == this.inputType) {
            return (CompletableFuture<T>) scheduledInput;
        } else {
            return null;
        }
    }

    public CompletableFuture<?> getCompletableFuture(){
        return scheduledInput;
    }

    public Input.InputType<?> getInputType(){
        return inputType;
    }

    public static void register(){

    }
}
