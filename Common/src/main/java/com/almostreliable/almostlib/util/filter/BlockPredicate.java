package com.almostreliable.almostlib.util.filter;

import com.almostreliable.almostlib.util.AlmostUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class BlockPredicate implements Predicate<BlockState> {

    // TODO: add wrapper for NbtPredicate and implement NBT submatching

    public static final BlockPredicate ANY = new BlockPredicate(List.of(), Set.of(), List.of(), NbtPredicate.ANY);
    public static final Serializer SERIALIZER = new Serializer();

    private final List<TagKey<Block>> tags;
    private final Set<Block> blocks;
    private final List<PropertyPredicate> properties;
    private final NbtPredicate nbt;

    @Nullable private Set<Block> decomposedBlocks;
    @Nullable private Multimap<Block, BlockState> decomposedBlockStates;

    public static Builder blocks(Block... blocks) {
        return new Builder().blocks(blocks);
    }

    @SafeVarargs
    public static Builder tags(TagKey<Block>... tags) {
        return new Builder().tags(tags);
    }

    private BlockPredicate(List<TagKey<Block>> tags, Set<Block> blocks, List<PropertyPredicate> properties, NbtPredicate nbt) {
        this.tags = tags;
        this.blocks = blocks;
        this.properties = properties;
        this.nbt = nbt;
    }

    public boolean testTags(BlockState state) {
        for (TagKey<Block> tag : tags) {
            if (state.is(tag)) return true;
        }
        return false;
    }

    public boolean testBlocks(BlockState state) {
        return blocks.contains(state.getBlock());
    }

    public boolean testNbt(ServerLevel level, BlockPos pos) {
        if (nbt == NbtPredicate.ANY) return true;
        var blockEntity = level.getBlockEntity(pos);
        return testNbt(blockEntity);
    }

    public boolean testNbt(@Nullable BlockEntity blockEntity) {
        if (nbt == NbtPredicate.ANY) return true;
        return blockEntity != null && nbt.matches(blockEntity.saveWithFullMetadata());
    }

    public boolean testProperties(BlockState state) {
        var stateDefinition = state.getBlock().getStateDefinition();
        for (PropertyPredicate property : properties) {
            if (!property.match(stateDefinition, state)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean test(BlockState state) {
        if (equals(ANY)) return true;
        return (testBlocks(state) || testTags(state)) && testProperties(state);
    }

    public boolean test(ServerLevel level, BlockPos pos) {
        if (equals(ANY)) return true;
        if (!level.isLoaded(pos)) return false;
        BlockState state = level.getBlockState(pos);
        return test(state) && testNbt(level, pos);
    }

    public boolean test(BlockEntity blockEntity) {
        if (equals(ANY)) return true;
        BlockState state = blockEntity.getBlockState();
        return test(state) && testNbt(blockEntity);
    }

    /**
     * Decomposes the predicate into a set of blocks. The result will be cached.
     *
     * @return A collection of blocks that match the predicate.
     */
    public Collection<Block> decomposeBlocks() {
        if (decomposedBlocks != null) {
            return decomposedBlocks;
        }

        Set<Block> allBlocks = new HashSet<>();
        for (TagKey<Block> tag : tags) {
            for (Holder<Block> holder : Registry.BLOCK.getTagOrEmpty(tag)) {
                allBlocks.add(holder.value());
            }
        }

        allBlocks.addAll(blocks);
        decomposedBlocks = Collections.unmodifiableSet(allBlocks);
        return decomposedBlocks;
    }

    /**
     * Decomposes the predicate into a multimap of blocks to block states. The result will be cached.
     *
     * @return A multimap of blocks to block states that match the predicate.
     */
    public Multimap<Block, BlockState> decomposeBlockStates() {
        if (decomposedBlockStates != null) {
            return decomposedBlockStates;
        }

        ImmutableMultimap.Builder<Block, BlockState> builder = ImmutableMultimap.builder();
        for (Block block : decomposeBlocks()) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                if (testProperties(state)) {
                    builder.put(block, state);
                }
            }
        }

        decomposedBlockStates = builder.build();
        return decomposedBlockStates;
    }

    /**
     * Returns a list of {@link Difference}'s between the predicate and the given block state.
     *
     * @param state  The block state to compare against.
     * @param onDiff A callback that will be called for each property.
     */
    public void difference(BlockState state, BiConsumer<PropertyPredicate, Difference> onDiff) {
        var stateDefinition = state.getBlock().getStateDefinition();
        for (PropertyPredicate pp : properties) {
            Property<?> property = stateDefinition.getProperty(pp.getName());
            if (property == null) {
                onDiff.accept(pp, new MissingPropertyDifference(pp.getName()));
            } else {
                boolean matches = pp.match(stateDefinition, state);
                onDiff.accept(pp, pp.difference(matches, property));
            }
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {

        private final List<TagKey<Block>> tags = new ArrayList<>();
        private final Set<Block> blocks = new HashSet<>();
        private final List<PropertyPredicate> properties = new ArrayList<>();

        private NbtPredicate nbt = NbtPredicate.ANY;

        private Builder() {}

        public Builder block(Block block) {
            this.blocks.add(block);
            return this;
        }

        public Builder blocks(Block... blocks) {
            this.blocks.addAll(Arrays.asList(blocks));
            return this;
        }

        public Builder tag(TagKey<Block> tag) {
            this.tags.add(tag);
            return this;
        }

        @SafeVarargs
        public final Builder tags(TagKey<Block>... tags) {
            this.tags.addAll(Arrays.asList(tags));
            return this;
        }

        public Builder nbt(NbtPredicate nbt) {
            this.nbt = nbt;
            return this;
        }

        public Builder nbt(CompoundTag nbt) {
            this.nbt = new NbtPredicate(nbt);
            return this;
        }

        public Builder hasProperty(Property<?> property, String value) {
            this.properties.add(new ExactPropertyPredicate(property.getName(), value));
            return this;
        }

        public Builder hasProperty(Property<Integer> property, int value) {
            return hasProperty(property, Integer.toString(value));
        }

        public Builder hasProperty(Property<Boolean> property, boolean value) {
            return hasProperty(property, Boolean.toString(value));
        }

        public <T extends Comparable<T> & StringRepresentable> Builder hasProperty(Property<T> property, T value) {
            return this.hasProperty(property, value.getSerializedName());
        }

        public Builder hasProperty(Property<?> property, @Nullable String min, @Nullable String max) {
            this.properties.add(new RangedPropertyPredicate(property.getName(), min, max));
            return this;
        }

        public Builder hasProperty(Property<Integer> property, @Nullable Integer min, @Nullable Integer max) {
            String minStr = min == null ? null : Integer.toString(min);
            String maxStr = max == null ? null : Integer.toString(max);
            return hasProperty(property, minStr, maxStr);
        }

        public <T extends Comparable<T> & StringRepresentable> Builder hasProperty(Property<T> property, @Nullable T min, @Nullable T max) {
            String minStr = min == null ? null : min.getSerializedName();
            String maxStr = max == null ? null : max.getSerializedName();
            return this.hasProperty(property, minStr, maxStr);
        }

        public Builder hasProperty(PropertyPredicate predicate) {
            this.properties.add(predicate);
            return this;
        }

        public BlockPredicate build() {
            return new BlockPredicate(tags, blocks, properties, nbt);
        }
    }

    /**
     * Default serializer for {@link BlockPredicate}. The values "nbt" and "state" are optional.
     * <p>
     * Example JSON:<br>
     * <pre>{@code
     * {
     *     "blocks": [
     *          "minecraft:furnace",
     *          "minecraft:blast_furnace",
     *          "#forge:ores" // Referencing a tag with '#'
     *     ],
     *     "nbt": {
     *          ...
     *     },
     *     "state": {
     *          "facing": "north",
     *          "lit": true
     *     }
     * }
     * }</pre>
     */
    public static class Serializer {

        public void toNetwork(FriendlyByteBuf buffer, BlockPredicate blockPredicate) {
            JsonObject json = toJson(blockPredicate);
            buffer.writeUtf(json.toString());
        }

        public BlockPredicate fromNetwork(FriendlyByteBuf buffer) {
            String str = buffer.readUtf();
            JsonObject json = new Gson().fromJson(str, JsonObject.class);
            return fromJson(json);
        }

        public JsonObject toJson(BlockPredicate blockPredicate) {
            JsonObject json = new JsonObject();
            json.add("blocks", writeBlocks(blockPredicate));

            if (blockPredicate.nbt != NbtPredicate.ANY) {
                json.add("nbt", blockPredicate.nbt.serializeToJson());
            }

            if (!blockPredicate.properties.isEmpty()) {
                JsonObject stateJson = new JsonObject();
                for (PropertyPredicate property : blockPredicate.properties) {
                    stateJson.add(property.getName(), property.toJson());
                }

                json.add("state", stateJson);
            }

            return json;
        }

        private JsonArray writeBlocks(BlockPredicate blockPredicate) {
            JsonArray blocks = new JsonArray();

            for (TagKey<Block> tag : blockPredicate.tags) {
                blocks.add("#" + tag.location().toString());
            }

            for (Block block : blockPredicate.blocks) {
                blocks.add(Registry.BLOCK.getKey(block).toString());
            }

            return blocks;
        }

        public BlockPredicate fromJson(@Nullable JsonObject json) {
            if (json == null) return ANY;
            var builder = new Builder();

            JsonArray blocks = readBlockArray(json.get("blocks"));
            addBlocksAndTags(builder, blocks);

            if (json.get("nbt") instanceof JsonObject nbt) {
                builder.nbt(NbtPredicate.fromJson(nbt));
            }

            if (json.get("state") instanceof JsonObject properties) {
                readStatePredicates(builder, properties);
            }

            return builder.build();
        }

        private void addBlocksAndTags(Builder builder, JsonArray blocks) {
            for (JsonElement block : blocks) {
                String str = block.getAsString();
                if (str.startsWith("#")) {
                    ResourceLocation tagRl = new ResourceLocation(str.substring(1));
                    builder.tag(TagKey.create(Registry.BLOCK_REGISTRY, tagRl));
                } else {
                    Registry.BLOCK.getOptional(new ResourceLocation(str)).ifPresent(builder::block);
                }
            }
        }

        private JsonArray readBlockArray(@Nullable JsonElement blocks) {
            if (blocks == null) return new JsonArray();

            if (blocks instanceof JsonPrimitive prim) {
                if (!prim.isString()) {
                    throw new IllegalArgumentException("Expected string, got " + blocks);
                }
                JsonArray arr = new JsonArray();
                arr.add(prim.getAsString());
                return arr;
            }

            if (blocks instanceof JsonArray arr) {
                for (JsonElement element : arr) {
                    if (!(element instanceof JsonPrimitive prim) || !prim.isString()) {
                        throw new IllegalArgumentException("Expected string, got '" + element + "' inside " + blocks);
                    }
                }
                return arr;
            }

            return new JsonArray();
        }

        private static void readStatePredicates(Builder builder, JsonObject properties) {
            for (var e : properties.entrySet()) {
                if (e.getValue() instanceof JsonPrimitive exact) {
                    builder.hasProperty(new ExactPropertyPredicate(e.getKey(), exact.getAsString()));
                }

                if (e.getValue() instanceof JsonObject ranged) {
                    String min = getStringOrNull(ranged.get("min"));
                    String max = getStringOrNull(ranged.get("max"));
                    builder.hasProperty(new RangedPropertyPredicate(e.getKey(), min, max));
                }
            }
        }

        @Nullable
        private static String getStringOrNull(@Nullable JsonElement json) {
            if (json == null) {
                return null;
            }

            return json.isJsonNull() ? null : json.getAsString();
        }
    }

    public interface PropertyPredicate {

        default <S extends StateHolder<?, S>> boolean match(StateDefinition<?, S> properties, BlockState state) {
            Property<?> property = properties.getProperty(getName());
            return property != null && this.match(state, property);
        }

        <T extends Comparable<T>> boolean match(BlockState state, Property<T> property);

        Difference difference(boolean matches, Property<?> property);

        String getName();

        JsonElement toJson();
    }

    public record ExactPropertyPredicate(String getName, String value) implements PropertyPredicate {

        @Override
        public <T extends Comparable<T>> boolean match(BlockState state, Property<T> property) {
            T comparable = state.getValue(property);
            return property.getValue(value).map(v -> comparable.compareTo(v) == 0).orElse(false);
        }

        @Override
        public Difference difference(boolean matches, Property<?> property) {
            return new ExactDifference(getName(), matches, property, value);
        }

        @Override
        public JsonElement toJson() {
            return new JsonPrimitive(value);
        }
    }

    public record RangedPropertyPredicate(String getName, @Nullable String min, @Nullable String max) implements PropertyPredicate {

        @Override
        public <T extends Comparable<T>> boolean match(BlockState state, Property<T> property) {
            T comparable = state.getValue(property);
            if (min != null) {
                Optional<T> minOpt = property.getValue(min);
                boolean matches = minOpt.map(v -> comparable.compareTo(v) < 0).orElse(false);
                if (!matches) return false;
            }

            if (max != null) {
                Optional<T> maxOpt = property.getValue(max);
                boolean matches = maxOpt.map(v -> comparable.compareTo(v) > 0).orElse(false);
                if (!matches) return false;
            }

            return true;
        }

        @Override
        public Difference difference(boolean matches, Property<?> property) {
            return new RangedDifference(getName(), matches, property, min, max);
        }

        @Override
        public JsonElement toJson() {
            JsonObject json = new JsonObject();
            if (this.min != null) {
                json.addProperty("min", this.min);
            }

            if (this.max != null) {
                json.addProperty("max", this.max);
            }

            return json;
        }
    }

    public interface Difference {

        String getName();

        boolean isMatch();

        @Nullable
        Property<?> getProperty();

        @Nullable
        default String getPropertyValue(BlockState state) {
            if (getProperty() == null) {
                return null;
            }

            Comparable<?> value = state.getValue(getProperty());
            return getProperty().getName(AlmostUtils.cast(value));
        }

        String getValueRepresentation();
    }

    record MissingPropertyDifference(String getName) implements Difference {

        @Override
        public boolean isMatch() {
            return false;
        }

        @Nullable
        @Override
        public Property<?> getProperty() {
            return null;
        }

        @Override
        public String getValueRepresentation() {
            return "not exist";
        }
    }

    record ExactDifference(String getName, boolean isMatch, @Nullable Property<?> getProperty, String getValue) implements Difference {

        @Override
        public String getValueRepresentation() {
            return getValue;
        }
    }

    record RangedDifference(String getName, boolean isMatch, @Nullable Property<?> getProperty, @Nullable String getMinValue,
                            @Nullable String getMaxValue) implements Difference {

        @Override
        public String getValueRepresentation() {
            if (getMinValue == null && getMaxValue == null) {
                return "any";
            }

            if (getMinValue == null) {
                return "≤" + getMaxValue;
            }

            if (getMaxValue == null) {
                return "≥" + getMinValue;
            }

            return getMinValue + ".." + getMaxValue;
        }
    }
}
