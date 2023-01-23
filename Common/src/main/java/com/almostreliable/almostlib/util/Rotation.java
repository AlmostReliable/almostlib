package com.almostreliable.almostlib.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public enum Rotation {
    R0(0),
    R90(90),
    R180(180),
    R270(270);

    public final int value;

    Rotation(int value) {
        this.value = value;
    }

    public static Rotation ofHorizontalFacing(BlockState state, DirectionProperty facing) {
        return ofHorizontalFacing(state.getValue(facing));
    }

    public static Rotation ofHorizontalFacing(Direction facing) {
        return switch (facing) {
            case SOUTH -> Rotation.R180;
            case EAST -> Rotation.R90;
            case WEST -> Rotation.R270;
            default -> Rotation.R0;
        };
    }

    public static Rotation ofVerticalFacing(BlockState state, DirectionProperty facing) {
        return ofVerticalFacing(state.getValue(facing));
    }

    public static Rotation ofVerticalFacing(Direction facing) {
        return switch (facing) {
            case UP -> Rotation.R270;
            case DOWN -> Rotation.R90;
            default -> Rotation.R0;
        };
    }
}
