package com.almostreliable.almostlib.datagen;

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

    protected TagsProvider(String modId, DataGenerator dataGenerator, Registry<T> registry) {
        super(modId, dataGenerator);
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

    public CoolerTagAppender<T> tag(TagKey<T> tagKey) {
        Tag.Builder builder = this.builders.computeIfAbsent(tagKey.location(), ($) -> new Tag.Builder());
        return new CoolerTagAppender<>(builder, this.registry, namespace);
    }

    public record CoolerTagAppender<T>(Tag.Builder builder, Registry<T> registry, String namespace) {
        @SafeVarargs
        public final CoolerTagAppender<T> add(T... entries) {
            for (T entry : entries) {
                builder.addElement(Objects.requireNonNull(registry.getKey(entry)), namespace);
            }
            return this;
        }

        @SafeVarargs
        public final CoolerTagAppender<T> add(RegistryEntry<T>... entries) {
            for (RegistryEntry<T> entry : entries) {
                builder.addElement(entry.getId(), namespace);
            }
            return this;
        }

        public CoolerTagAppender<T> addOptional(ResourceLocation id) {
            builder.addOptionalElement(id, namespace);
            return this;
        }

        public CoolerTagAppender<T> addTag(TagKey<T> tag) {
            builder.addTag(tag.location(), namespace);
            return this;
        }

        public CoolerTagAppender<T> addOptionalTag(ResourceLocation tag) {
            builder.addOptionalTag(tag, namespace);
            return this;
        }
    }
}
