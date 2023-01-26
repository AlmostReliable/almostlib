package com.almostreliable.almostlib.fabric.mixin;

import com.almostreliable.almostlib.datagen.template.LayeredModelTemplate.RenderLayer;
import com.almostreliable.almostlib.fabric.FabricInitializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.lang.reflect.Type;
import java.util.List;

@Mixin(BlockModel.Deserializer.class)
public abstract class BlockModelDeserializerMixin {

    @Inject(
        method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/client/renderer/block/model/BlockModel;",
        at = @At("TAIL"),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void fetchRenderLayer(JsonElement element, Type type, JsonDeserializationContext context, CallbackInfoReturnable<BlockModel> cir, JsonObject json, List<BlockElement> list, String name) {
        var renderLayer = RenderLayer.valueOf(GsonHelper.getAsString(json, "render_type", "solid").toUpperCase());
        if (renderLayer == RenderLayer.SOLID) return;

        ResourceLocation resourceLocation = name.isEmpty() ? null : new ResourceLocation(name);
        if (resourceLocation != null) FabricInitializer.initRenderType(resourceLocation, renderLayer);
    }
}
