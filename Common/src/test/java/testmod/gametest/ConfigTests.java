package testmod.gametest;

import com.almostreliable.lib.gametest.AlmostGameTestHelper;
import com.almostreliable.lib.gametest.GameTestProvider;
import net.minecraft.gametest.framework.GameTest;
import testmod.TestConfigHolder;

import java.util.HashMap;
import java.util.Map;

public class ConfigTests implements GameTestProvider {

    @GameTest
    public void stringValueSpecs(AlmostGameTestHelper helper) {
        Map<String, String> values = new HashMap<>();

        var config = TestConfigHolder.of("StringValueSpecs", b -> {
            // without default value
            values.put("StringValue1", b.stringValue("StringValue1").read());
            // with default value
            values.put("StringValue2", b.stringValue("StringValue2", "DefaultStringValue").read());
            // with single comment
            values.put("StringValue3", b.stringValue("StringValue3").comment("This is StringValue3").read());
            // with value comment
            values.put("StringValue4", b.stringValue("StringValue4").valueComment("Value1", "Value2").read());
            // with comment and value comment
            values.put(
                "StringValue5",
                b.stringValue("StringValue5")
                    .comment("This is StringValue5")
                    .valueComment("Value1", "Value2")
                    .read()
            );
        });

        // only load the config without any config text, so all default values are applied
        // we only test if the specs are working correctly, not the actual config reading
        config.get();

        helper.succeedIf(() -> {
            if (!config.getConfigText().equals(Constants.STRING_VALUE_SPEC_DEFAULTS)) {
                helper.fail("Config text is not correct!");
            }

            for (var entry : values.entrySet()) {
                if (entry.getKey().equals("StringValue2")) {
                    if (!entry.getValue().equals("DefaultStringValue")) {
                        helper.fail("Value of " + entry.getKey() + " is not correct!");
                    }
                    continue;
                }

                if (!entry.getValue().isEmpty()) {
                    helper.fail("Value of " + entry.getKey() + " is not correct!");
                }
            }
        });
    }

    @GameTest
    public void booleanValueSpecs(AlmostGameTestHelper helper) {
        Map<String, Boolean> values = new HashMap<>();

        var config = TestConfigHolder.of("BooleanValueSpecs", b -> {
            // without default value
            values.put("BooleanValue1", b.booleanValue("BooleanValue1").read());
            // with default value
            values.put("BooleanValue2", b.booleanValue("BooleanValue2", true).read());
            // with single comment
            values.put("BooleanValue3", b.booleanValue("BooleanValue3").comment("This is BooleanValue3").read());
            // with value comment
            values.put("BooleanValue4", b.booleanValue("BooleanValue4").valueComment(true, false).read());
            // with comment and value comment
            values.put(
                "BooleanValue5",
                b.booleanValue("BooleanValue5")
                    .comment("This is BooleanValue5")
                    .valueComment(true, false)
                    .read()
            );
        });

        // only load the config without any config text, so all default values are applied
        // we only test if the specs are working correctly, not the actual config reading
        config.get();

        helper.succeedIf(() -> {
            if (!config.getConfigText().equals(Constants.BOOLEAN_VALUE_SPEC_DEFAULTS)) {
                helper.fail("Config text is not correct!");
            }

            for (var entry : values.entrySet()) {
                if (entry.getKey().equals("BooleanValue2")) {
                    if (!entry.getValue().equals(true)) {
                        helper.fail("Value of " + entry.getKey() + " is not correct!");
                    }
                    continue;
                }

                if (!entry.getValue().equals(false)) {
                    helper.fail("Value of " + entry.getKey() + " is not correct!");
                }
            }
        });
    }

    @GameTest
    public void integerRangeSpecs(AlmostGameTestHelper helper) {
        Map<String, Integer> values = new HashMap<>();

        var config = TestConfigHolder.of("IntegerRangeSpecs", b -> {
            // without default value
            values.put("IntegerValue1", b.intValue("IntegerValue1").read());
            // with default value
            values.put("IntegerValue2", b.intValue("IntegerValue2", 5).read());
            // with single comment
            values.put("IntegerValue3", b.intValue("IntegerValue3").comment("This is IntegerValue3").read());
            // with min value
            values.put("IntegerValue4", b.intValue("IntegerValue4").min(2).read());
            // with max value
            values.put("IntegerValue5", b.intValue("IntegerValue5").max(10).read());
            // with range
            values.put("IntegerValue6", b.intValue("IntegerValue6").range(2, 10).read());
            // with comment and range
            values.put(
                "IntegerValue7",
                b.intValue("IntegerValue7")
                    .range(2, 10)
                    .comment("This is IntegerValue7")
                    .read()
            );
        });

        // only load the config without any config text, so all default values are applied
        // we only test if the specs are working correctly, not the actual config reading
        config.get();

        helper.succeedIf(() -> {
            if (!config.getConfigText().equals(Constants.INTEGER_VALUE_SPEC_DEFAULTS)) {
                helper.fail("Config text is not correct!");
            }

            for (var entry : values.entrySet()) {
                if (entry.getKey().equals("IntegerValue2")) {
                    if (!entry.getValue().equals(5)) {
                        helper.fail("Value of " + entry.getKey() + " is not correct!");
                    }
                    continue;
                }

                if (!entry.getValue().equals(0)) {
                    helper.fail("Value of " + entry.getKey() + " is not correct!");
                }
            }
        });
    }

    @GameTest
    public void longRangeSpecs(AlmostGameTestHelper helper) {
        Map<String, Long> values = new HashMap<>();

        var config = TestConfigHolder.of("LongRangeSpecs", b -> {
            // without default value
            values.put("LongValue1", b.longValue("LongValue1").read());
            // with default value
            values.put("LongValue2", b.longValue("LongValue2", 100L).read());
            // with single comment
            values.put("LongValue3", b.longValue("LongValue3").comment("This is LongValue3").read());
            // with min value
            values.put("LongValue4", b.longValue("LongValue4").min(50L).read());
            // with max value
            values.put("LongValue5", b.longValue("LongValue5").max(750L).read());
            // with range
            values.put("LongValue6", b.longValue("LongValue6").range(50L, 750L).read());
            // with comment and range
            values.put(
                "LongValue7",
                b.longValue("LongValue7")
                    .range(50L, 750L)
                    .comment("This is LongValue7")
                    .read()
            );
        });

        // only load the config without any config text, so all default values are applied
        // we only test if the specs are working correctly, not the actual config reading
        config.get();

        helper.succeedIf(() -> {
            if (!config.getConfigText().equals(Constants.LONG_VALUE_SPEC_DEFAULTS)) {
                helper.fail("Config text is not correct!");
            }

            for (var entry : values.entrySet()) {
                if (entry.getKey().equals("LongValue2")) {
                    if (!entry.getValue().equals(100L)) {
                        helper.fail("Value of " + entry.getKey() + " is not correct!");
                    }
                    continue;
                }

                if (!entry.getValue().equals(0L)) {
                    helper.fail("Value of " + entry.getKey() + " is not correct!");
                }
            }
        });
    }

    @GameTest
    public void floatRangeSpecs(AlmostGameTestHelper helper) {
        Map<String, Float> values = new HashMap<>();

        var config = TestConfigHolder.of("FloatRangeSpecs", b -> {
            // without default value
            values.put("FloatValue1", b.floatValue("FloatValue1").read());
            // with default value
            values.put("FloatValue2", b.floatValue("FloatValue2", 7.5f).read());
            // with single comment
            values.put("FloatValue3", b.floatValue("FloatValue3").comment("This is FloatValue3").read());
            // with min value
            values.put("FloatValue4", b.floatValue("FloatValue4").min(2.5f).read());
            // with max value
            values.put("FloatValue5", b.floatValue("FloatValue5").max(12.5f).read());
            // with range
            values.put("FloatValue6", b.floatValue("FloatValue6").range(2.5f, 12.5f).read());
            // with comment and range
            values.put(
                "FloatValue7",
                b.floatValue("FloatValue7")
                    .range(2.5f, 12.5f)
                    .comment("This is FloatValue7")
                    .read()
            );
        });

        // only load the config without any config text, so all default values are applied
        // we only test if the specs are working correctly, not the actual config reading
        config.get();

        helper.succeedIf(() -> {
            if (!config.getConfigText().equals(Constants.FLOAT_VALUE_SPEC_DEFAULTS)) {
                helper.fail("Config text is not correct!");
            }

            for (var entry : values.entrySet()) {
                if (entry.getKey().equals("FloatValue2")) {
                    if (!entry.getValue().equals(7.5f)) {
                        helper.fail("Value of " + entry.getKey() + " is not correct!");
                    }
                    continue;
                }

                if (!entry.getValue().equals(0f)) {
                    helper.fail("Value of " + entry.getKey() + " is not correct!");
                }
            }
        });
    }

    @GameTest
    public void doubleRangeSpecs(AlmostGameTestHelper helper) {
        Map<String, Double> values = new HashMap<>();

        var config = TestConfigHolder.of("DoubleRangeSpecs", b -> {
            // without default value
            values.put("DoubleValue1", b.doubleValue("DoubleValue1").read());
            // with default value
            values.put("DoubleValue2", b.doubleValue("DoubleValue2", 8.5).read());
            // with single comment
            values.put("DoubleValue3", b.doubleValue("DoubleValue3").comment("This is DoubleValue3").read());
            // with min value
            values.put("DoubleValue4", b.doubleValue("DoubleValue4").min(3.5).read());
            // with max value
            values.put("DoubleValue5", b.doubleValue("DoubleValue5").max(13.5).read());
            // with range
            values.put("DoubleValue6", b.doubleValue("DoubleValue6").range(3.5, 13.5).read());
            // with comment and range
            values.put(
                "DoubleValue7",
                b.doubleValue("DoubleValue7")
                    .range(3.5, 13.5)
                    .comment("This is DoubleValue7")
                    .read()
            );
        });

        // only load the config without any config text, so all default values are applied
        // we only test if the specs are working correctly, not the actual config reading
        config.get();

        helper.succeedIf(() -> {
            if (!config.getConfigText().equals(Constants.DOUBLE_VALUE_SPEC_DEFAULTS)) {
                helper.fail("Config text is not correct!");
            }

            for (var entry : values.entrySet()) {
                if (entry.getKey().equals("DoubleValue2")) {
                    if (!entry.getValue().equals(8.5)) {
                        helper.fail("Value of " + entry.getKey() + " is not correct!");
                    }
                    continue;
                }

                if (!entry.getValue().equals(0D)) {
                    helper.fail("Value of " + entry.getKey() + " is not correct!");
                }
            }
        });
    }

    @GameTest
    public void enumValueSpecs(AlmostGameTestHelper helper) {
        Map<String, Enum<?>> values = new HashMap<>();

        var config = TestConfigHolder.of("EnumValueSpecs", b -> {
            // without default value
            values.put("EnumValue1", b.enumValue("EnumValue1", TestEnum.class).read());
            // with default value
            values.put("EnumValue2", b.enumValue("EnumValue2", TestEnum.class, TestEnum.VALUE3).read());
            // with single comment
            values.put("EnumValue3", b.enumValue("EnumValue3", TestEnum.class).comment("This is EnumValue3").read());
        });

        // only load the config without any config text, so all default values are applied
        // we only test if the specs are working correctly, not the actual config reading
        config.get();

        helper.succeedIf(() -> {
            if (!config.getConfigText().equals(Constants.ENUM_VALUE_SPEC_DEFAULTS)) {
                helper.fail("Config text is not correct!");
            }

            for (var entry : values.entrySet()) {
                if (entry.getKey().equals("EnumValue2")) {
                    if (entry.getValue() != TestEnum.VALUE3) {
                        helper.fail("Value of " + entry.getKey() + " is not correct!");
                    }
                    continue;
                }

                if (entry.getValue() != TestEnum.class.getEnumConstants()[0]) {
                    helper.fail("Value of " + entry.getKey() + " is not correct!");
                }
            }
        });
    }

    private void example(AlmostGameTestHelper helper) {
        Map<String, Object> values = new HashMap<>();

        var tc = TestConfigHolder.of("example", b -> {
            values.put("TestBoolean", b.booleanValue("TestBoolean", true).comment("TestBoolean comment").read());
            values.put("TestInt", b.intValue("TestInt").range(0, 10).read());
        });

        // write something to the dummy config
        tc.setConfigText("""
            TestBoolean = false
            TestInt = 5
            """);
        // load the dummy config
        tc.get();

        helper.succeedIf(() -> {
            System.out.println(tc.getConfigText());

            if (values.get("TestBoolean").equals(true)) {
                helper.fail("TestBoolean was not set to false!");
            }
        });
    }

    private enum TestEnum {
        VALUE1,
        VALUE2,
        VALUE3
    }

    private static final class Constants {

        private static final String STRING_VALUE_SPEC_DEFAULTS = """
            StringValue1 = ""
            StringValue2 = "DefaultStringValue"
            # This is StringValue3
            StringValue3 = ""
            # Possible Values: Value1, Value2
            StringValue4 = ""
            # This is StringValue5
            # Possible Values: Value1, Value2
            StringValue5 = ""
                        
            """;

        private static final String BOOLEAN_VALUE_SPEC_DEFAULTS = """
            BooleanValue1 = false
            BooleanValue2 = true
            # This is BooleanValue3
            BooleanValue3 = false
            # Possible Values: true, false
            BooleanValue4 = false
            # This is BooleanValue5
            # Possible Values: true, false
            BooleanValue5 = false

            """;

        private static final String INTEGER_VALUE_SPEC_DEFAULTS = """
            IntegerValue1 = 0
            IntegerValue2 = 5
            # This is IntegerValue3
            IntegerValue3 = 0
            # Range: ≥2
            IntegerValue4 = 0
            # Range: ≤10
            IntegerValue5 = 0
            # Range: 2 to 10
            IntegerValue6 = 0
            # This is IntegerValue7
            # Range: 2 to 10
            IntegerValue7 = 0
                        
            """;

        private static final String LONG_VALUE_SPEC_DEFAULTS = """
            LongValue1 = 0
            LongValue2 = 100
            # This is LongValue3
            LongValue3 = 0
            # Range: ≥50
            LongValue4 = 0
            # Range: ≤750
            LongValue5 = 0
            # Range: 50 to 750
            LongValue6 = 0
            # This is LongValue7
            # Range: 50 to 750
            LongValue7 = 0
                        
            """;

        private static final String FLOAT_VALUE_SPEC_DEFAULTS = """
            FloatValue1 = 0.0
            FloatValue2 = 7.5
            # This is FloatValue3
            FloatValue3 = 0.0
            # Range: ≥2.5
            FloatValue4 = 0.0
            # Range: ≤12.5
            FloatValue5 = 0.0
            # Range: 2.5 to 12.5
            FloatValue6 = 0.0
            # This is FloatValue7
            # Range: 2.5 to 12.5
            FloatValue7 = 0.0
                        
            """;

        private static final String DOUBLE_VALUE_SPEC_DEFAULTS = """
            DoubleValue1 = 0.0
            DoubleValue2 = 8.5
            # This is DoubleValue3
            DoubleValue3 = 0.0
            # Range: ≥3.5
            DoubleValue4 = 0.0
            # Range: ≤13.5
            DoubleValue5 = 0.0
            # Range: 3.5 to 13.5
            DoubleValue6 = 0.0
            # This is DoubleValue7
            # Range: 3.5 to 13.5
            DoubleValue7 = 0.0
                        
            """;

        private static final String ENUM_VALUE_SPEC_DEFAULTS = """
            # Possible Values: VALUE1, VALUE2, VALUE3
            EnumValue1 = "VALUE1"
            # Possible Values: VALUE1, VALUE2, VALUE3
            EnumValue2 = "VALUE3"
            # This is EnumValue3
            # Possible Values: VALUE1, VALUE2, VALUE3
            EnumValue3 = "VALUE1"
                        
            """;
    }
}
