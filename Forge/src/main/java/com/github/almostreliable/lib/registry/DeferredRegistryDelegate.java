package com.github.almostreliable.lib.registry;

import com.github.almostreliable.lib.api.registry.RegistryDelegate;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class DeferredRegistryDelegate<T extends IForgeRegistryEntry<T>> implements RegistryDelegate<T> {
    private final IForgeRegistry<T> forgeRegistry;
    @Nullable
    private DeferredRegister<T> deferredRegister;

    public DeferredRegistryDelegate(IForgeRegistry<T> forgeRegistry) {
        this.forgeRegistry = forgeRegistry;
    }

    @Override
    public <E extends T> Supplier<E> register(String namespace, String id, Supplier<? extends E> supplier) {
        if (deferredRegister == null) {
            // TODO thinking about storing namespace to check always against it as DeferredRegister is fixed to one namespace.
            deferredRegister = DeferredRegister.create(forgeRegistry, namespace);
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
