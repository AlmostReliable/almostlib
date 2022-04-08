package com.github.almostreliable.lib.registry.builders;

import com.github.almostreliable.lib.Utils;
import com.github.almostreliable.lib.registry.DataGenContainer;
import com.github.almostreliable.lib.registry.RegisterCallback;
import com.github.almostreliable.lib.registry.RegistryEntry;
import com.github.almostreliable.lib.registry.RegistryManager;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractEntryBuilder<T, BASE, SELF extends AbstractEntryBuilder<T, BASE, SELF>>
        implements RegistryEntryBuilder<T, BASE> {
    protected final String name;
    protected final RegisterCallback registerCallback;
    protected final RegistryManager manager;
    protected final DataGenContainer<T> dataGenContainer;

    public AbstractEntryBuilder(String name, RegistryManager manager, RegisterCallback registerCallback) {
        this.name = name;
        this.registerCallback = registerCallback;
        this.manager = manager;
        this.dataGenContainer = new DataGenContainer<>();
    }

    public RegistryEntry<T> register() {
        return registerCallback.onRegister(this);
    }

    @Override
    public String getName() {
        return name;
    }

    public SELF lang(Function<T, String> keyProvider, Function<T, String> valueProvider) {
        dataGenContainer.lang(keyProvider, valueProvider);
        return Utils.cast(this);
    }

    public SELF lang(String key, String value) {
        return lang($ -> key, $ -> value);
    }

    protected String nameToLang() {
        return Arrays
                .stream(name.split("_"))
                .filter(s -> !s.isBlank())
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }

    public DataGenContainer<T> getDataGenData() {
        return dataGenContainer;
    }
}
