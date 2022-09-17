package com.almostreliable.almostlib.datagen;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

public interface DataGenHolder {

    @Nullable
    DataGenManager getDataGenManager();

    default void applyDataGen(Consumer<DataGenManager> consumer) {
        Optional.ofNullable(getDataGenManager()).ifPresent(consumer);
    }
}
