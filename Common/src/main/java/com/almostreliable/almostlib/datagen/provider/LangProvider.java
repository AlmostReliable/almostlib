package com.almostreliable.almostlib.datagen.provider;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class LangProvider extends AbstractDataProvider {

    private final Map<String, String> langs;

    public LangProvider(String modId, DataGenerator dataGenerator) {
        super(modId, dataGenerator);
        this.langs = new HashMap<>();
    }

    @Override
    public void run(CachedOutput cachedOutput) throws IOException {
        JsonObject data = new JsonObject();
        SortedMap<String, String> sorted = new TreeMap<>(Comparator.naturalOrder());
        sorted.putAll(langs);
        sorted.forEach(data::addProperty);

        if (!sorted.isEmpty()) {
            DataProvider.saveStable(cachedOutput, data, getLangPath("en_us"));
        }
    }

    public void addLang(String key, String value) {
        if (langs.containsKey(key)) {
            throw new IllegalArgumentException(key + " already exists in lang provider");
        }

        langs.put(key, value);
    }

    private Path getLangPath(String lang) {
        return getAssetsPath().resolve(namespace + "/lang/" + lang + ".json");
    }

}
