package com.almostreliable.almostlib.datagen.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;

import java.nio.file.Path;

public abstract class AbstractDataProvider implements DataProvider {
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    protected final String namespace;
    protected final DataGenerator dataGenerator;

    public AbstractDataProvider(String namespace, DataGenerator dataGenerator) {
        this.namespace = namespace;
        this.dataGenerator = dataGenerator;
    }

    @Override
    public String getName() {
        return namespace + " " + getClass().getSimpleName();
    }

    protected Path getAssetsPath() {
        return dataGenerator.getOutputFolder().resolve("assets/");
    }

    protected Path getDataPath() {
        return dataGenerator.getOutputFolder().resolve("data/");
    }
}
