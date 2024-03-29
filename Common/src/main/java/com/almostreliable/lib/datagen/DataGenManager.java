package com.almostreliable.lib.datagen;

import com.almostreliable.lib.AlmostLib;
import com.almostreliable.lib.Platform;
import com.almostreliable.lib.datagen.provider.*;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class DataGenManager {

    public static final String ALMOST_DATA_GEN_PLATFORM = "almost.datagen.platform";
    private static final Queue EMPTY = new Queue() {};
    private final boolean isEnabled;
    private final Queue commonQueue;
    private final Queue forgeQueue;
    private final Queue fabricQueue;
    private final String namespace;

    private DataGenManager(String namespace) {
        this.namespace = namespace;
        isEnabled = AlmostLib.PLATFORM.isDataGenEnabled();
        commonQueue = isEnabled ? new ExistingQueue() : EMPTY;
        forgeQueue = isEnabled ? new ExistingQueue() : EMPTY;
        fabricQueue = isEnabled ? new ExistingQueue() : EMPTY;
    }

    public static DataGenManager create(String namespace) {
        return new DataGenManager(namespace);
    }

    public Queue common() {
        return commonQueue;
    }

    public Queue platform() {
        return switch (AlmostLib.PLATFORM.getPlatform()) {
            case COMMON -> common();
            case FORGE -> forge();
            case FABRIC -> fabric();
        };
    }

    public Queue forge() {
        return forgeQueue;
    }

    public Queue fabric() {
        return fabricQueue;
    }

    public void collectProviders(DataGenerator generator) {
        if (!isEnabled) {
            throw new IllegalStateException("DataGen is not enabled");
        }

        DataGenProviders providers = new DataGenProviders(namespace, generator);
        Platform[] platforms = readPlatform();

        for (Platform platform : platforms) {
            (switch (platform) {
                case FORGE -> forge();
                case FABRIC -> fabric();
                case COMMON -> common();
            }).run(providers);
        }
    }

    private Platform[] readPlatform() {
        String property = System.getProperty(ALMOST_DATA_GEN_PLATFORM);

        if (property == null) {
            return new Platform[]{ Platform.COMMON };
        }

        String[] platforms = property.split(",");
        Platform[] result = new Platform[platforms.length];
        for (int i = 0; i < platforms.length; i++) {
            result[i] = Platform.valueOf(platforms[i].toUpperCase());
        }
        return result;
    }

    public interface Queue {

        default void add(Consumer<DataGenProviders> consumer) {}

        default void loot(Consumer<LootTableProvider> consumer) {
            add(p -> consumer.accept(p.getLootTableProvider()));
        }

        default void blockState(Consumer<BlockStateProvider> consumer) {
            add(p -> consumer.accept(p.getBlockStateProvider()));
        }

        default <T> void tags(Registry<T> registry, Consumer<TagsProvider<T>> consumer) {
            add(p -> consumer.accept(p.getTagsProvider(registry)));
        }

        default void itemModel(Consumer<ItemModelProvider> consumer) {
            add(p -> consumer.accept(p.getItemModelProvider()));
        }

        default void lang(Consumer<LangProvider> consumer) {
            add(p -> consumer.accept(p.getLangProvider()));
        }

        default void run(DataGenProviders providers) {}
    }

    private static class ExistingQueue implements Queue {

        private final List<Consumer<DataGenProviders>> consumers = new ArrayList<>();

        private ExistingQueue() {}

        public void add(Consumer<DataGenProviders> consumer) {
            consumers.add(consumer);
        }

        @Override
        public void run(DataGenProviders providers) {
            consumers.forEach(c -> c.accept(providers));
        }
    }
}
