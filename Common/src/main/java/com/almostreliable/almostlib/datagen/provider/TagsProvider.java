package com.almostreliable.almostlib.datagen.provider;

import com.almostreliable.almostlib.registry.RegistryEntry;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TagsProvider<T> extends AbstractDataProvider {
    public final Map<ResourceLocation, Tag.Builder> builders = Maps.newLinkedHashMap();
    private final Registry<T> registry;

    public TagsProvider(String namespace, DataGenerator dataGenerator, Registry<T> registry) {
        super(namespace, dataGenerator);
        this.registry = registry;
    }

    @Override
    public void run(HashCache hashCache) throws IOException {
        for (var entry : builders.entrySet()) {
            List<Tag.BuilderEntry> invalidEntries = entry.getValue().getEntries().filter(this::isInvalid).toList();
            if (!invalidEntries.isEmpty()) {
                throw new IllegalStateException(
                        "Invalid entries in tag " + entry.getKey() + ": " + invalidEntries.stream().map(
                                Objects::toString).collect(Collectors.joining(",")));
            }

            JsonObject jsonObject = entry.getValue().serializeToJson();
            DataProvider.save(GSON, hashCache, jsonObject, getTagPath(entry.getKey()));
        }
    }

    protected boolean isInvalid(Tag.BuilderEntry builderEntry) {
        return !builderEntry.entry().verifyIfPresent(registry::containsKey, builders::containsKey);
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
        Tag.Builder builder = this.builders.computeIfAbsent(tagKey.location(), ($) -> new Tag.Builder());
        return new TagAppender<>(builder, this.registry, namespace);
    }

    public record TagAppender<T>(Tag.Builder builder, Registry<T> registry, String namespace) {
        @SafeVarargs
        public final TagAppender<T> add(T... entries) {
            for (T entry : entries) {
                builder.addElement(Objects.requireNonNull(registry.getKey(entry)), namespace);
            }
            return this;
        }

        @SafeVarargs
        public final TagAppender<T> add(RegistryEntry<T>... entries) {
            for (RegistryEntry<T> entry : entries) {
                builder.addElement(entry.getId(), namespace);
            }
            return this;
        }

        public TagAppender<T> addOptional(ResourceLocation id) {
            builder.addOptionalElement(id, namespace);
            return this;
        }

        public TagAppender<T> addTag(TagKey<T> tag) {
            builder.addTag(tag.location(), namespace);
            return this;
        }

        public TagAppender<T> addOptionalTag(ResourceLocation tag) {
            builder.addOptionalTag(tag, namespace);
            return this;
        }
    }
}