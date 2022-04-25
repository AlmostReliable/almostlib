package com.github.almostreliable.lib.datagen;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;

//create own tag provider and dont extend
public class TagsProvider<T> extends net.minecraft.data.tags.TagsProvider<T> {
    private final String modId;

    protected TagsProvider(String modId, DataGenerator dataGenerator, Registry<T> registry) {
        super(dataGenerator, registry);
        this.modId = modId;
    }

    @Override
    protected void addTags() {
        // Not in use
        String s = "";
    }

    @Override
    public String getName() {
        return modId + " " + getClass().getSimpleName();
    }

    @Override
    public TagAppender<T> tag(TagKey<T> tagKey) {
        Tag.Builder builder = getOrCreateRawBuilder(tagKey);
        return new TagsProvider.TagAppender<>(builder, this.registry, modId);
    }

    @Override
    public Tag.Builder getOrCreateRawBuilder(TagKey<T> tagKey) {
        return this.builders.computeIfAbsent(tagKey.location(), ($) -> new Tag.Builder());
    }
}
