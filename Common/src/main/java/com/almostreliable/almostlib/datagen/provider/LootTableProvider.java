package com.almostreliable.almostlib.datagen.provider;

import com.almostreliable.almostlib.AlmostLib;
import com.google.common.collect.Multimap;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LootTableProvider extends AbstractDataProvider {

    Map<ResourceLocation, LootTable> lootTables = new HashMap<>();

    public LootTableProvider(String namespace, DataGenerator dataGenerator) {
        super(namespace, dataGenerator);
    }

    @Override
    public void run(CachedOutput cachedOutput) throws IOException {
        ValidationContext validation = new ValidationContext(LootContextParamSets.ALL_PARAMS,
            resourceLocation -> null,
            lootTables::get);

        lootTables.forEach((resourceLocation, lootTable) -> {
            LootTables.validate(validation, resourceLocation, lootTable);
        });

        Multimap<String, String> problems = validation.getProblems();
        if (!problems.isEmpty()) {
            problems.forEach((s1, s2) -> {
                AlmostLib.LOGGER.warn("Loot table {} has problems: {}", s1, s2);
            });
            throw new IllegalStateException("Loot table validation failed");
        }

        for (var entry : lootTables.entrySet()) {
            DataProvider.saveStable(cachedOutput, LootTables.serialize(entry.getValue()), createLootTablePath(entry.getKey()));
        }
    }

    public void dropSelf(Block block) {
        dropOther(block, block);
    }

    public void dropOther(Block block, ItemLike itemLike) {
        LootPool.Builder pool = LootPool
            .lootPool()
            .when(ExplosionCondition.survivesExplosion())
            .setRolls(ConstantValue.exactly(1))
            .add(LootItem.lootTableItem(itemLike));
        LootTable.Builder builder = LootTable.lootTable().withPool(pool);
        add(block, builder);
    }

    public void add(Block block, LootTable.Builder builder) {
        add(block.getLootTable(), builder.setParamSet(LootContextParamSets.BLOCK));
    }

    public void add(ResourceLocation location, LootTable.Builder builder) {
        if (location.equals(BuiltInLootTables.EMPTY)) {
            throw new IllegalStateException(
                "Cannot add the empty built-in loot table. Some block has 'noDrops' set to true but also calls this");
        }

        LootTable result = lootTables.put(location, builder.build());
        if (result != null) {
            throw new IllegalStateException("Duplicate loot table " + location);
        }
    }

    private Path createLootTablePath(ResourceLocation location) {
        return getDataPath().resolve(location.getNamespace() + "/loot_tables/" + location.getPath() + ".json");
    }
}
