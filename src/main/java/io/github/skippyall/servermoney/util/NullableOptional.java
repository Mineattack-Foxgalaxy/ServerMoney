package io.github.skippyall.servermoney.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class NullableOptional<T> {
    private static final NullableOptional<?> EMPTY = new NullableOptional<>(false, null);
    boolean isPresent;
    T value;
    private NullableOptional(boolean isPresent, T value){
        this.isPresent = isPresent;
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    public static <T> NullableOptional<T> empty(){
        return (NullableOptional<T>) EMPTY;
    }

    public static <T> NullableOptional<T> of(T value) {
        return new NullableOptional<>(true, value);
    }

    public boolean isPresent(){
        return isPresent;
    }

    public boolean isEmpty(){
        return !isPresent;
    }

    public T get() {
        if(isPresent){
            return value;
        } else {
            throw new NoSuchElementException("No value present");
        }
    }

    public void ifPresent(Consumer<? super T> action) {
        if (isPresent) {
            action.accept(value);
        }
    }

    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        if (isPresent) {
            action.accept(value);
        } else {
            emptyAction.run();
        }
    }

    public T orElse(T other) {
        return isPresent ? value : other;
    }
}
