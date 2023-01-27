package com.almostreliable.almostlib.datagen.template;

import com.almostreliable.almostlib.mixin.ModelTemplateAccessor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class LayeredModelTemplate extends ModelTemplate {

    private final RenderLayer renderLayer;

    public LayeredModelTemplate(RenderLayer renderLayer, Optional<ResourceLocation> model, Optional<String> suffix, TextureSlot... textureSlots) {
        super(model, suffix, textureSlots);
        this.renderLayer = renderLayer;
    }

    public static LayeredModelTemplate of(ModelTemplate modelTemplate, RenderLayer renderLayer) {
        ModelTemplateAccessor template = (ModelTemplateAccessor) modelTemplate;
        return new LayeredModelTemplate(
            renderLayer, template.getModel(), template.getSuffix(), template.getRequiredSlots().toArray(new TextureSlot[0])
        );
    }

    @Override
    public ResourceLocation create(ResourceLocation resourceLocation, TextureMapping textureMapping, BiConsumer<ResourceLocation, Supplier<JsonElement>> biConsumer) {
        return super.create(
            resourceLocation,
            textureMapping,
            renderLayer == RenderLayer.SOLID ? biConsumer : (rl, supplier) -> injectRenderLayer(rl, supplier, biConsumer)
        );
    }

    private void injectRenderLayer(ResourceLocation resourceLocation, Supplier<JsonElement> supplier, BiConsumer<ResourceLocation, Supplier<JsonElement>> biConsumer) {
        var json = supplier.get();
        if (json instanceof JsonObject jsonObject) {
            jsonObject.addProperty("render_type", renderLayer.name);
        }
        biConsumer.accept(resourceLocation, () -> json);
    }

    public enum RenderLayer {
        SOLID("solid", RenderType::solid),
        CUTOUT("cutout", RenderType::cutout),
        CUTOUT_MIPPED("cutout_mipped", RenderType::cutoutMipped),
        TRANSLUCENT("translucent", RenderType::translucent);

        public final String name;
        public final Supplier<RenderType> renderType;

        RenderLayer(String name, Supplier<RenderType> renderType) {
            this.name = name;
            this.renderType = renderType;
        }
    }
}
