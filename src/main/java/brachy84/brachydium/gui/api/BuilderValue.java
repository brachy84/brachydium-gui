package brachy84.brachydium.gui.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BuilderValue<T> {

    private final String id;
    private T value;
    private Supplier<T> supplier;
    private boolean applied;
    private String[] depends = {};

    public BuilderValue(@NotNull String id, @NotNull T value, @Nullable JsonReader<T> jsonReader) {
        this.id = id;
        this.value = value;
        this.applied = false;
    }

    public BuilderValue(@NotNull String id, @NotNull T value) {
        this(id, value, null);
    }

    public void apply() {
        if (supplier != null) {
            value = supplier.get();
        }
        applied = true;
    }

    public T applyAndGet() {
        apply();
        return getValue();
    }

    public String getId() {
        return id;
    }

    public sub getValue() {
        return value;
    }

    public void setValue(T value, String... depends) {
        if (applied) {
            this.value = value;
        } else {
            supplier(() -> value);
        }
    }

    public void supplier(Supplier<T> supplier, String... depends) {
        this.supplier = supplier;
    }
}
