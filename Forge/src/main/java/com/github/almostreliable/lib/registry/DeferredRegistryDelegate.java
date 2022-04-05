package com.github.almostreliable.lib.registry;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class DeferredRegistryDelegate<T extends IForgeRegistryEntry<T>> extends AbstractRegistryDelegate<T> {
    private final IForgeRegistry<T> forgeRegistry;
    @Nullable
    private DeferredRegister<T> deferredRegister;

    public DeferredRegistryDelegate(Supplier<String> namespace, IForgeRegistry<T> forgeRegistry) {
        super(namespace);
        this.forgeRegistry = forgeRegistry;
    }

    @Override
    public <E extends T> Supplier<E> register(String id, Supplier<? extends E> supplier) {
        if (deferredRegister == null) {
            deferredRegister = DeferredRegister.create(forgeRegistry, getNamespace());
        }

        return deferredRegister.register(id, supplier);
    }

    @Override
    public boolean isPresent() {
        return deferredRegister != null;
    }

    @Override
    public void init() {
        if (deferredRegister != null) {
            deferredRegister.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }
}
