package com.almostreliable.almostlib.block;

import com.almostreliable.almostlib.menu.synchronizer.DataHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.function.Consumer;

public class SideConfiguration implements DataHandler {

    private final SideConfigurable host;
    @Nullable private final Runnable changeListener;
    private final EnumMap<Direction, Setting> config = new EnumMap<>(Direction.class);

    private boolean changed;

    public SideConfiguration(SideConfigurable host, @Nullable Runnable changeListener) {
        this.host = host;
        this.changeListener = changeListener;
        reset();
    }

    public SideConfiguration(SideConfigurable host) {
        this(host, null);
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        for (Direction dir : Direction.values()) {
            buffer.writeByte(config.get(dir).ordinal());
        }
        changed = false;
    }

    @Override
    public void decode(FriendlyByteBuf buffer) {
        for (Direction dir : Direction.values()) {
            config.put(dir, Setting.values()[buffer.readByte()]);
        }
    }

    @Override
    public boolean hasChanged() {
        return changed;
    }

    public CompoundTag serialize() {
        var tag = new CompoundTag();
        for (var direction : Direction.values()) {
            tag.putInt(direction.toString(), get(direction).ordinal());
        }
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        for (var direction : Direction.values()) {
            set(direction, Setting.values()[tag.getInt(direction.toString())]);
        }
    }

    public void reset() {
        for (var direction : Direction.values()) {
            config.put(direction, Setting.OFF);
        }
    }

    public Setting get(Direction direction) {
        return config.get(direction);
    }

    public Setting get(BlockSide side) {
        return get(getDirectionFromSide(side));
    }

    public void set(Direction direction, Setting setting) {
        config.put(direction, setting);
        if (changeListener != null) {
            changeListener.run();
        }
        changed = true;
    }

    public void set(BlockSide side, Setting setting) {
        set(getDirectionFromSide(side), setting);
    }

    public int count(Setting setting) {
        return (int) config.values().stream().filter(s -> s == setting).count();
    }

    public void forEach(Setting setting, Consumer<Direction> consumer) {
        config.forEach((dir, s) -> {
            if (s == setting) {
                consumer.accept(dir);
            }
        });
    }

    public boolean isConfigured() {
        return count(Setting.OFF) != config.size();
    }

    private Direction getDirectionFromSide(BlockSide side) {
        var facing = host.getFacing();
        var top = host.getTop() == null ? facing : host.getTop();
        return facing == top ? horizontalConversion(side, facing) : verticalConversion(side, facing, top);
    }

    private Direction horizontalConversion(BlockSide side, Direction facing) {
        return switch (side) {
            case FRONT -> facing;
            case BACK -> facing.getOpposite();
            case LEFT -> facing.getClockWise();
            case RIGHT -> facing.getCounterClockWise();
            case TOP -> Direction.UP;
            case BOTTOM -> Direction.DOWN;
        };
    }

    private Direction verticalConversion(BlockSide side, Direction facing, Direction top) {
        return switch (side) {
            case FRONT -> facing;
            case BACK -> facing.getOpposite();
            case LEFT -> facing == Direction.UP ? top.getCounterClockWise(): top.getClockWise();
            case RIGHT -> facing == Direction.UP ? top.getClockWise() : top.getCounterClockWise();
            case TOP -> top;
            case BOTTOM -> top.getOpposite();
        };
    }

    public enum Setting {
        OFF,
        IN,
        OUT,
        IO
    }

    public enum BlockSide {
        FRONT,
        BACK,
        LEFT,
        RIGHT,
        TOP,
        BOTTOM
    }
}
