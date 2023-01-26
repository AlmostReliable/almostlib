package com.almostreliable.almostlib.mixin;

import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;
import java.util.Set;

@Mixin(ModelTemplate.class)
public interface ModelTemplateAccessor {

    @Accessor("model")
    Optional<ResourceLocation> getModel();

    @Accessor("requiredSlots")
    Set<TextureSlot> getRequiredSlots();

    @Accessor("suffix")
    Optional<String> getSuffix();
}
