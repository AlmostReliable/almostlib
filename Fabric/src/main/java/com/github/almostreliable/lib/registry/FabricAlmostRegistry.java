package com.github.almostreliable.lib.registry;

import net.minecraft.core.Registry;

import java.util.Objects;

public class FabricAlmostRegistry<T> extends AbstractAlmostRegistry<T> {
    protected final Registry<T> registry;

    public FabricAlmostRegistry(String namespace, Registry<T> registry) {
        super(namespace);
        Objects.requireNonNull(registry);
        this.registry = registry;
    }

    @Override
    public void init() {
        for (var entry : entries.entrySet()) {
            Registry.register(registry, entry.getKey(), entry.getValue().get());
        }
    }
}
