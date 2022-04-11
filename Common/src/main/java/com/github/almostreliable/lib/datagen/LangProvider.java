package com.github.almostreliable.lib.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.HashCache;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LangProvider extends AbstractDataProvider {
    private final Map<String, String> langs;

    public LangProvider(String modId, DataGenerator dataGenerator) {
        super(modId, dataGenerator);
        this.langs = new HashMap<>();
    }

    @Override
    public void run(HashCache var1) throws IOException {

    }

    public void addLang(String key, String value) {
        if (langs.containsKey(key)) {
            throw new IllegalArgumentException(key + " already exists in lang provider");
        }

        langs.put(key, value);
    }
}
