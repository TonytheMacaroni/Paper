package io.papermc.paper.util;

import com.google.common.base.Preconditions;
import net.kyori.adventure.pointer.Pointer;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.pointer.Pointers;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@NullMarked
public final class PointeredMap implements Pointered {

    private final Map<Pointer<?>, Supplier<?>> pointers;

    public PointeredMap() {
        this.pointers = new HashMap<>();
    }

    public PointeredMap(PointeredMap other) {
        this.pointers = new HashMap<>(other.pointers);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> get(Pointer<T> pointer) {
        Preconditions.checkArgument(pointer != null, "Pointer cannot be null");

        Supplier<?> supplier = this.pointers.get(pointer);
        return supplier != null ? Optional.ofNullable((T) supplier.get()) : Optional.empty();
    }

    @Override
    public <T> @Nullable T getOrDefault(Pointer<T> pointer, @Nullable T defaultValue) {
       return this.get(pointer).orElse(defaultValue);
    }

    @Override
    public <T> @UnknownNullability T getOrDefaultFrom(Pointer<T> pointer, Supplier<? extends @UnknownNullability T> defaultValue) {
        return this.get(pointer).orElseGet(defaultValue);
    }

    public <T> void putStatic(Pointer<T> pointer, @Nullable T value) {
        Preconditions.checkArgument(pointer != null, "Pointer cannot be null");
        this.pointers.put(pointer, () -> value);
    }

    public <T> void putDynamic(Pointer<T> pointer, Supplier<? extends @Nullable T> supplier) {
        Preconditions.checkArgument(pointer != null, "Pointer cannot be null");
        this.pointers.put(pointer, supplier);
    }

    public <T> boolean supports(Pointer<T> pointer) {
        Preconditions.checkArgument(pointer != null, "Pointer cannot be null");
        return this.pointers.containsKey(pointer);
    }

    public <T> void remove(Pointer<T> pointer) {
        Preconditions.checkArgument(pointer != null, "Pointer cannot be null");
        this.pointers.remove(pointer);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Pointers pointers() {
        Pointers.Builder builder = Pointers.builder();
        this.pointers.forEach((Pointer pointer, Supplier supplier) -> builder.withDynamic(pointer, supplier));

        return builder.build();
    }

}
