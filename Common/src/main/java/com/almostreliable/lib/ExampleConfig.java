package com.almostreliable.lib;

import com.almostreliable.lib.config.ConfigBuilder;

import java.util.List;

public class ExampleConfig {

    public final int age;
    public final String name;
    public final Platform platform;
    public final SubConfig sub;
    public final List<Double> machineHealths;

    public ExampleConfig(ConfigBuilder builder) {
        name = builder.stringValue("name", "default").comment("The name of the character.").read();
        age = builder.intValue("age", 0).comment("Age of the character.").read();
        platform = builder.enumValue("platform", Platform.class, Platform.FORGE).comment("Which platform currently is used").read();
        machineHealths = builder.doubleListValue("machineHealths", List.of(0.0, 1.0, 2.0)).comment("The health of the machines.").read();
        sub = new SubConfig(builder.category("sub", """
            New Category.
            You can see what happens when you change the config file.
            Awesome Line
                * Bullet Point
                * Bullet Point 2
                * Bullet Point 3
            More comment
            """));
    }

    public static class SubConfig {

        public final int level;
        public final String name;
        public final float health;

        public SubConfig(ConfigBuilder builder) {
            level = builder.intValue("level", 0).comment("Level of the character. NEW").read();
            name = builder.stringValue("name", "default").comment("The name of the character.").read();
            health = builder.floatValue("health", 0.0f).comment("The health of the character.").read();
        }
    }
}
