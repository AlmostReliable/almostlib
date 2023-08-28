package testmod;

import com.almostreliable.lib.config.ConfigBuilder;
import com.almostreliable.lib.config.ConfigHolder;
import com.almostreliable.lib.config.ValueSpec;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utility class for testing the config library.
 * <p>
 * This class modifies the logic of how the text from the config file is read
 * and written. This allows for testing without an actual file.<br>
 * It's using an {@link Object} as the type of the config for simplicity, it's
 * not actually being used.
 * <p>
 * To create an instance of the TestConfig, use {@link #of(String, Consumer)}.<br>
 * The consumer will give you an instance of a {@link ConfigBuilder} and is
 * used to define the config specs. Make sure to store the values of the specs in an
 * external map, so you can keep track of them later in the tests.<br>
 * To obtain a value of a spec, use {@link ValueSpec#read()}.
 * <p>
 * After the instance is created and the structure has been defined, set
 * the config text with {@link #setConfigText(String)} and load the config
 * with {@link #get()}.<br>
 * This will return an Object, but it's not actually used, so you can ignore it.<br>
 * Instead, use the created map with the values and test them.
 */
public final class TestConfigHolder extends ConfigHolder<Object> {

    private String configText = "";
    @Nullable private OutputStream outputStream;

    /**
     * Creates a new instance of the TestConfig.
     * <p>
     * The consumer will give you an instance of a {@link ConfigBuilder} and is
     * used to define the config specs. Make sure to store the values of the specs in an
     * external map, so you can keep track of them later in the tests.<br>
     * To obtain a value of a spec, use {@link ValueSpec#read()}.
     * <p>
     * The name of the config is not important and is only used for the log message
     * in the tests when the config is loaded.
     *
     * @param name     The name of the config file.
     * @param consumer The consumer to define the config specs.
     * @return The instance of the TestConfig.
     */
    public static TestConfigHolder of(String name, Consumer<ConfigBuilder> consumer) {
        return new TestConfigHolder(name, builder -> {
            consumer.accept(builder);
            return new Object();
        });
    }

    private TestConfigHolder(String name, Function<ConfigBuilder, Object> factory) {
        super(Path.of(name + ".toml"), Object.class, factory);
    }

    /**
     * Sets the text of the config file.
     * <p>
     * This will be used when {@link #get()} is called.<br>
     * The text should be in TOML format.
     *
     * @param s The text of the config file.
     */
    public void setConfigText(String s) {
        this.configText = s;
    }

    /**
     * Gets the text of the config file.
     * <p>
     * This needs to be called after {@link #get()}.<br>
     * <p>
     * It will hold the text of the config file after parsing,
     * conversion and validation.
     *
     * @return The text of the config file.
     */
    public String getConfigText() {
        if (outputStream == null) return "";
        return outputStream.toString();
    }

    @Override
    protected InputStream getInputStream() {
        // overridden to return the set text instead of reading from a file
        return new ByteArrayInputStream(configText.getBytes());
    }

    @Override
    protected OutputStream getOutputStream() {
        // overridden to return a stream that stores the written text,
        // so it can be retrieved later with getConfigText()
        outputStream = new ByteArrayOutputStream();
        return outputStream;
    }
}
