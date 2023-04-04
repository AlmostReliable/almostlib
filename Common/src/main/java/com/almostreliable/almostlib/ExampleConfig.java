package com.almostreliable.almostlib;

import com.almostreliable.almostlib.config.ConfigBuilder;

public class ExampleConfig {

    public final int age;
    public final String name;
    public final Platform platform;
    public final SubConfig sub;

    public ExampleConfig(ConfigBuilder builder) {
        age = builder.intValue("age", 0).comment("Age of the character.").read();
        name = builder.value("name", "default").comment("The name of the character.").read();
        platform = builder.enumValue("platform", Platform.class, Platform.FORGE).comment("Which platform currently is used").read();
        sub = new SubConfig(builder.category("sub", """
            New Category.
            You can see what happens when you change the config file. 
            """));
    }

    public static class SubConfig {

        public final int level;
        public final String name;
        public final float health;

        public SubConfig(ConfigBuilder builder) {
            level = builder.intValue("level", 0).comment("Level of the character. NEW").read();
            name = builder.value("name", "default").comment("The name of the character.").read();
            health = builder.floatValue("health", 0.0f).comment("The health of the character.").read();
        }
    }
}
