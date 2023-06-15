package com.almostreliable.almostlib.forge.component;

import com.almostreliable.almostlib.component.ComponentHolder;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class AlmostCapabilityProvider implements ICapabilityProvider {

    private final ComponentHolder componentHolder;
    private final Map<Object, LazyOptional<?>> capabilityCache = new HashMap<>();

    public AlmostCapabilityProvider(ComponentHolder componentHolder) {
        this.componentHolder = componentHolder;
        this.componentHolder.addInvalidateListener(this::onInvalidate);
    }

    private void onInvalidate(Object o) {
        LazyOptional<?> lazyOptional = capabilityCache.get(o);
        if (lazyOptional != null) {
            lazyOptional.invalidate();
        }
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction direction) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            var container = componentHolder.getItemContainer(direction);
            if (container != null) {
                return capabilityCache.computeIfAbsent(container, $ -> LazyOptional.of(() -> new InvWrapper(container))).cast();
            }
        }

        if (capability == ForgeCapabilities.ENERGY) {
            var container = componentHolder.getEnergyContainer(direction);
            if (container != null) {
                return capabilityCache.computeIfAbsent(container, $ -> LazyOptional.of(() -> new EnergyComponentWrapper(container))).cast();
            }
        }

        return LazyOptional.empty();
    }
}
