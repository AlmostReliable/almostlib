package com.almostreliable.almostlib.util.filter;

import com.almostreliable.almostlib.util.AlmostUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BlockPredicate implements Predicate<BlockState> {

    public static final BlockPredicate ANY = new BlockPredicate(List.of(), Set.of(), StatePropertiesPredicate.ANY, NbtPredicate.ANY);
    public static final Serializer SERIALIZER = new Serializer();

    private final List<TagKey<Block>> tags;
    private final Set<Block> blocks;
    private final StatePropertiesPredicate properties;
    private final NbtPredicate nbt;
    @Nullable Set<Block> decomposedBlocks;
    @Nullable Multimap<Block, BlockState> decomposedBlockStates;

    public static BlockPredicate.Builder blocks(Block... blocks) {
        return new Builder().blocks(blocks);
    }

    @SafeVarargs
    public static BlockPredicate.Builder tags(TagKey<Block>... tags) {
        return new Builder().tags(tags);
    }

    private BlockPredicate(List<TagKey<Block>> tags, Set<Block> blocks, StatePropertiesPredicate properties, NbtPredicate nbt) {
        this.tags = tags;
        this.blocks = blocks;
        this.properties = properties;
        this.nbt = nbt;
    }

    private boolean testTags(BlockState state) {
        for (TagKey<Block> tag : tags) {
            if (state.is(tag)) return true;
        }
        return false;
    }

    private boolean testBlocks(BlockState state) {
        return blocks.contains(state.getBlock());
    }

    private boolean testNbt(ServerLevel level, BlockPos pos) {
        if (nbt == NbtPredicate.ANY) return true;
        var blockEntity = level.getBlockEntity(pos);
        return blockEntity != null && nbt.matches(blockEntity.saveWithFullMetadata());
    }

    @Override
    public boolean test(BlockState state) {
        if (equals(ANY)) return true;
        return (testTags(state) || testBlocks(state)) && properties.matches(state);
    }

    public boolean test(ServerLevel level, BlockPos pos) {
        if (equals(ANY)) return true;
        if (!level.isLoaded(pos)) return false;
        BlockState state = level.getBlockState(pos);
        return test(state) && testNbt(level, pos);
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
                if (properties.matches(state)) {
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
     * @param state The block state to compare against.
     * @return A list of differences.
     */
    public List<Difference> difference(BlockState state) {
        List<Difference> differences = new ArrayList<>();
        var stateDefinition = state.getBlock().getStateDefinition();
        for (var matcher : properties.properties) {
            Property<?> property = stateDefinition.getProperty(matcher.getName());
            boolean matched = matcher.match(stateDefinition, state);
            if (matcher instanceof StatePropertiesPredicate.ExactPropertyMatcher epm) {
                var diff = new ExactDifference(epm.getName(), matched, property, epm.value);
                differences.add(diff);
            } else if (matcher instanceof StatePropertiesPredicate.RangedPropertyMatcher rpm) {
                var diff = new RangedDifference(rpm.getName(), matched, property, rpm.minValue, rpm.maxValue);
                differences.add(diff);
            }
        }

        return differences;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {

        private final List<TagKey<Block>> tags = new ArrayList<>();
        private final Set<Block> blocks = new HashSet<>();
        private StatePropertiesPredicate properties = StatePropertiesPredicate.ANY;
        private NbtPredicate nbt = NbtPredicate.ANY;

        private Builder() {

        }

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

        public Builder properties(StatePropertiesPredicate spp) {
            this.properties = spp;
            return this;
        }

        public Builder properties(Consumer<StatePropertiesPredicate.Builder> consumer) {
            var builder = StatePropertiesPredicate.Builder.properties();
            consumer.accept(builder);
            this.properties = builder.build();
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

            if (blockPredicate.properties != StatePropertiesPredicate.ANY) {
                json.add("state", blockPredicate.properties.serializeToJson());
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
                builder.properties(StatePropertiesPredicate.fromJson(properties));
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
