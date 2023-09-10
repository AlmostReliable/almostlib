package com.almostreliable.lib.config;

import com.almostreliable.lib.AlmostLib;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.io.*;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import com.google.common.base.Preconditions;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfigHolder<T> implements Supplier<T> {

    private final Path path;
    private final Class<T> clazz;
    private final Function<ConfigBuilder, T> factory;

    @Nullable private T value;

    public ConfigHolder(Path path, Class<T> clazz, Function<ConfigBuilder, T> factory) {
        if (FilenameUtils.getExtension(path.toFile().getName()).isEmpty()) {
            path = path.resolveSibling(path.getFileName() + ".toml");
        }
        Preconditions.checkArgument(FilenameUtils.getExtension(path.toFile().getName()).equals("toml"), "config must be a TOML file");

        this.path = path;
        this.clazz = clazz;
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
        AlmostLib.LOGGER.info("Reloading config '" + clazz.getSimpleName() + "': " + path);
        value = read();
    }

    private T read() {
        ConfigBuilder builder = createBuilder();
        T newValue = factory.apply(builder);
        if (builder.requiresSave()) {
            write(builder);
        }
        return newValue;
    }

    private void write(ConfigBuilder builder) {
        try (var stream = getOutputStream()) {
            TomlWriter writer = new TomlWriter();
            writer.setIndent(IndentStyle.SPACES_2);
            writer.setNewline(NewlineStyle.UNIX);
            writer.write(builder.getConfig(), stream);
            AlmostLib.LOGGER.info("Saved config '" + clazz.getSimpleName() + "': " + path);
        } catch (IOException | WritingException e) {
            AlmostLib.LOGGER.error("Failed to write config '" + clazz.getSimpleName() + "': " + path, e);
        }
    }

    private ConfigBuilder createBuilder() {
        try (var stream = getInputStream()) {
            TomlParser parser = new TomlParser();
            CommentedConfig config = ConfigBuilder.defaultConfig();
            parser.parse(stream, config, ParsingMode.REPLACE);
            return new ConfigBuilder(config);
        } catch (IOException | ParsingException e) {
            AlmostLib.LOGGER.error("Failed to read config '" + clazz.getSimpleName() + "': " + path, e);
            return new ConfigBuilder();
        }
    }

    protected InputStream getInputStream() throws IOException {
        if (Files.notExists(path)) {
            return InputStream.nullInputStream();
        }

        return Files.newInputStream(path);
    }

    protected OutputStream getOutputStream() throws IOException {
        if (Files.notExists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }

        return Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
