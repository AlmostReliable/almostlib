package com.almostreliable.almostlib.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import javax.annotation.Nullable;

public class MapItemGenerator {
    protected final MapPosFactory factory;
    @Nullable protected Component displayName;
    protected MapDecoration.Type destinationType = MapDecoration.Type.RED_X;
    private boolean renderBiomePreviewMap = true;
    private byte mapViewScale = 2;
    private boolean trackPosition = true;
    private boolean unlimitedTracking = true;

    public MapItemGenerator(MapPosFactory factory) {
        this.factory = factory;
    }

    public MapItemGenerator displayName(Component name) {
        this.displayName = name;
        return this;
    }

    public MapItemGenerator marker(MapDecoration.Type type) {
        this.destinationType = type;
        return this;
    }

    public MapItemGenerator noPreview() {
        this.renderBiomePreviewMap = false;
        return this;
    }

    public MapItemGenerator scale(byte scale) {
        this.mapViewScale = scale;
        return this;
    }

    public MapItemGenerator disablePositionTracking() {
        this.trackPosition = false;
        return this;
    }

    public MapItemGenerator disableUnlimitedTracking() {
        this.unlimitedTracking = false;
        return this;
    }

    @Nullable
    public ItemStack generate(ServerLevel level, BlockPos pos) {
        MapPosFactory.Info info = factory.apply(level, pos);
        if (info == null) return null;

        ItemStack map = MapItem.create(level,
                info.pos().getX(),
                info.pos().getZ(),
                this.mapViewScale,
                this.trackPosition,
                this.unlimitedTracking);

        if (renderBiomePreviewMap) MapItem.renderBiomePreviewMap(level, map);
        MapItemSavedData.addTargetDecoration(map, info.pos(), "+", this.destinationType);
        map.setHoverName(displayName == null ? info.name() : displayName);
        return map;
    }
}
