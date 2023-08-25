package com.almostreliable.lib.config;

import com.almostreliable.lib.AlmostLib;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileNotFoundAction;
import com.electronwill.nightconfig.core.io.IndentStyle;
import com.electronwill.nightconfig.core.io.NewlineStyle;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.hocon.HoconParser;
import com.electronwill.nightconfig.hocon.HoconWriter;
import com.google.common.base.Preconditions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.util.function.Function;
import java.util.function.Supplier;

public class Config<T> implements Supplier<T> {

    @Nullable private T value;
    private final File file;
    private final Class<T> type;
    private final Function<ConfigBuilder, T> factory;

    Config(File file, Class<T> type, Function<ConfigBuilder, T> factory) {
        Preconditions.checkArgument(FilenameUtils.getExtension(file.getName()).equals("hocon"), "Config file must be a HOCON file");
        this.file = file;
        this.type = type;
        this.factory = factory;
    }

    @Override
    public T get() {
        if (value == null) {
            value = read();
        }
        return value;
    }

    void reload() {
        AlmostLib.LOGGER.info("Reloading config '" + type.getSimpleName() + "':" + file);
        value = read();
    }

    private ConfigBuilder createBuilder(File file) {
        if (!file.exists()) {
            return new ConfigBuilder();
        }

        try {
            HoconParser parser = new HoconParser();
            CommentedConfig config = ConfigBuilder.defaultConfig();
            parser.parse(file, config, ParsingMode.REPLACE, FileNotFoundAction.READ_NOTHING);
            return new ConfigBuilder(config);
        } catch (Exception e) {
            AlmostLib.LOGGER.error("Failed to load config file", e);
            return new ConfigBuilder();
        }
    }

    private T read() {
        ConfigBuilder builder = createBuilder(file);
        T value = factory.apply(builder);
        if (builder.requiresSave()) {
            save(builder);
        }
        return value;
    }

    private void save(ConfigBuilder builder) {
        try {
            HoconWriter writer = new HoconWriter();
            writer.setIndent(IndentStyle.SPACES_2);
            writer.setNewline(NewlineStyle.UNIX);
            FileUtils.createParentDirectories(file);
            writer.write(builder.getConfig(), file, WritingMode.REPLACE);
            AlmostLib.LOGGER.info("Saved config '" + type.getSimpleName() + "': " + file);
        } catch (Exception e) {
            AlmostLib.LOGGER.error("Failed to save config file", e);
        }
    }
}
