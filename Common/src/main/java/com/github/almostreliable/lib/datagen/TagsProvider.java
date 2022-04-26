package com.github.almostreliable.lib.datagen;

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
        return modId + " " + getClass().getSimpleName();
    }

    public net.minecraft.data.tags.TagsProvider.TagAppender<T> tag(TagKey<T> tagKey) {
        Tag.Builder builder = this.builders.computeIfAbsent(tagKey.location(), ($) -> new Tag.Builder());

        return new net.minecraft.data.tags.TagsProvider.TagAppender<>(builder, this.registry, modId);
    }
}
