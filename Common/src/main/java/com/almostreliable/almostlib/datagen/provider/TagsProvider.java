package com.almostreliable.almostlib.datagen.provider;

import com.almostreliable.almostlib.AlmostLib;
import com.almostreliable.almostlib.registry.RegistryEntry;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TagsProvider<T> extends AbstractDataProvider {
    public final Map<ResourceLocation, TagBuilder> builders = Maps.newLinkedHashMap();
    private final Registry<T> registry;

    public TagsProvider(String namespace, DataGenerator dataGenerator, Registry<T> registry) {
        super(namespace, dataGenerator);
        this.registry = registry;
    }

    @Override
    public void run(CachedOutput cachedOutput) throws IOException {
        for (var entry : builders.entrySet()) {
            List<TagEntry> invalidEntries = entry.getValue().build().stream().filter(this::isInvalid).toList();
            if (!invalidEntries.isEmpty()) {
                throw new IllegalStateException(
                        "Invalid entries in tag " + entry.getKey() + ": " + invalidEntries.stream().map(
                                Objects::toString).collect(Collectors.joining(",")));
            }

            DataResult<JsonElement> result = TagFile.CODEC.encodeStart(JsonOps.INSTANCE,
                    new TagFile(entry.getValue().build(), false));
            JsonElement json = result.getOrThrow(false, AlmostLib.LOGGER::error);
            DataProvider.saveStable(cachedOutput, json, getTagPath(entry.getKey()));
        }
    }

    protected boolean isInvalid(TagEntry entry) {
        return !entry.verifyIfPresent(registry::containsKey, builders::containsKey);
    }

    protected Path getTagPath(ResourceLocation resourceLocation) {
        return getDataPath().resolve(
                resourceLocation.getNamespace() + "/" + TagManager.getTagDir(registry.key()) + "/" +
                resourceLocation.getPath() + ".json");
    }

    @Override
    public String getName() {
        return namespace + " " + getClass().getSimpleName();
    }

    public TagAppender<T> tag(TagKey<T> tagKey) {
        TagBuilder builder = this.builders.computeIfAbsent(tagKey.location(), ($) -> new TagBuilder());
        return new TagAppender<>(builder, this.registry);
    }

    public record TagAppender<T>(TagBuilder builder, Registry<T> registry) {
        @SafeVarargs
        public final TagAppender<T> add(T... entries) {
            for (T entry : entries) {
                builder.addElement(Objects.requireNonNull(registry.getKey(entry)));
            }
            return this;
        }

        @SafeVarargs
        public final TagAppender<T> add(RegistryEntry<T>... entries) {
            for (RegistryEntry<T> entry : entries) {
                builder.addElement(entry.getId());
            }
            return this;
        }

        public TagAppender<T> addOptional(ResourceLocation id) {
            builder.addOptionalElement(id);
            return this;
        }

        public TagAppender<T> addTag(TagKey<T> tag) {
            builder.addTag(tag.location());
            return this;
        }

        public TagAppender<T> addOptionalTag(ResourceLocation tag) {
            builder.addOptionalTag(tag);
            return this;
        }
    }
}
