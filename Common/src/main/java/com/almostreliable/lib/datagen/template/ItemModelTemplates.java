package com.almostreliable.lib.datagen.template;

import com.almostreliable.lib.datagen.provider.ItemModelProvider;
import com.almostreliable.lib.registry.ItemEntry;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;

public class ItemModelTemplates {

    public static <T extends BlockItem> void inheritFromBlock(ItemEntry<T> entry, ItemModelProvider provider) {
        ResourceLocation resourceLocation = entry.getKey().location();
        ModelTemplates.FLAT_HANDHELD_ITEM.create(
            ModelLocationUtils.getModelLocation(entry.get()),
            TextureMapping.layer0(new ResourceLocation(
                resourceLocation.getNamespace(),
                "block/" + resourceLocation.getPath()
            )),
            provider.getModelConsumer()
        );
    }
}
