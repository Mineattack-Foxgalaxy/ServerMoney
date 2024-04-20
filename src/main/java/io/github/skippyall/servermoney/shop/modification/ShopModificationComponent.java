package io.github.skippyall.servermoney.shop.modification;

import dev.onyxstudios.cca.api.v3.entity.PlayerComponent;
import io.github.skippyall.servermoney.input.Input;
import net.minecraft.nbt.NbtCompound;

import java.util.concurrent.CompletableFuture;

public class ShopModificationComponent implements PlayerComponent<ShopModificationComponent> {
    private CompletableFuture<?> scheduledInput;
    private Input.InputType<?> inputType;

    public void reset(){
        scheduledInput = null;
        inputType = null;
    }

    public <T> void setScheduledInput(CompletableFuture<T> scheduledInput, Input.InputType<T> inputType) {
        this.scheduledInput = scheduledInput;
        this.inputType = inputType;
    }

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

    @Override
    public void readFromNbt(NbtCompound tag) {}

    @Override
    public void writeToNbt(NbtCompound tag) {}
}
