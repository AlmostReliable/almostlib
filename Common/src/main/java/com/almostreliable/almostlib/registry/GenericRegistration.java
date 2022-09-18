package com.almostreliable.almostlib.registry;

import com.almostreliable.almostlib.util.AlmostUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class GenericRegistration<S> extends Registration<S, RegistryEntry<? extends S>> {
    GenericRegistration(String namespace, Registry<S> registry) {
        super(namespace, registry);
    }

    @Override
    protected RegistryEntry<? extends S> createEntry(ResourceLocation id, Supplier<? extends S> supplier) {
        return new RegistryEntryImpl<>(getRegistry(), id, AlmostUtils.cast(supplier));
    }
}
