package com.almostreliable.lib.mixin;

import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Use the same logic as fabrics mixin so we can have the structures in the same format and in the same directory for both
 * loaders...
 * // TODO Finding an alternative
 */
@Mixin(StructureUtils.class)
public abstract class StructureUtilsMixin {
    private static final String PATH = "gametest/structures/"; // same as fabric

    @Inject(method = "getStructureTemplate", at = @At(value = "HEAD"), cancellable = true)
    private static void almostlib$getStructureTemplate(String template, ServerLevel level, CallbackInfoReturnable<StructureTemplate> cir) {
        try {
            ResourceLocation asLocation = new ResourceLocation(template);
            ResourceLocation structureId = new ResourceLocation(asLocation.getNamespace(),
                    PATH + asLocation.getPath() + ".snbt");
            Resource resource = level.getServer().getResourceManager().getResource(structureId);
            try (InputStream stream = resource.getInputStream()) {
                String data = IOUtils.toString(stream, StandardCharsets.UTF_8);
                CompoundTag compoundTag = NbtUtils.snbtToStructure(data);
                StructureTemplate structureTemplate = level.getStructureManager().readStructure(compoundTag);
                cir.setReturnValue(structureTemplate);
            }
        } catch (Exception ignored) {
        }
    }

}
