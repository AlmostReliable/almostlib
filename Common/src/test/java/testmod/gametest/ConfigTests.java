package testmod.gametest;

import com.almostreliable.lib.gametest.AlmostGameTestHelper;
import com.almostreliable.lib.gametest.GameTestProvider;
import net.minecraft.gametest.framework.GameTest;
import testmod.TestConfig;

import java.util.HashMap;
import java.util.Map;

public class ConfigTests implements GameTestProvider {

    @GameTest
    public void stringValueSpecs(AlmostGameTestHelper helper) {
        Map<String, Object> values = new HashMap<>();

        var config = TestConfig.of(b -> {
            // without default value -> empty string
            values.put("StringValue1", b.stringValue("StringValue1").read());
            // with default value -> default value
            values.put("StringValue2", b.stringValue("StringValue2", "DefaultStringValue").read());
            // with single comment
            values.put("StringValue3", b.stringValue("StringValue3").comment("This is StringValue3").read());
            // with multiple comments
            values.put(
                "StringValue4",
                b.stringValue("StringValue4")
                    .comment("This is StringValue4")
                    .comment("This is StringValue4 again")
                    .read()
            );
            // with value comment
            values.put("StringValue5", b.stringValue("StringValue5").valueComment("Value1", "Value2").read());
            // with comment and value comment
            values.put(
                "StringValue6",
                b.stringValue("StringValue6")
                    .comment("This is StringValue6")
                    .valueComment("Value1", "Value2")
                    .read()
            );
            // with multiple comments and value comment
            values.put(
                "StringValue7",
                b.stringValue("StringValue7")
                    .comment("This is StringValue7")
                    .comment("This is StringValue7 again")
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
                if (!(entry.getValue() instanceof String)) {
                    helper.fail("Value of " + entry.getKey() + " is not a String!");
                }

                if (entry.getKey().equals("StringValue2")) {
                    if (!entry.getValue().equals("DefaultStringValue")) {
                        helper.fail("Value of " + entry.getKey() + " is not correct!");
                    }
                    continue;
                }

                if (!entry.getValue().equals("")) {
                    helper.fail("Value of " + entry.getKey() + " is not correct!");
                }
            }
        });
    }

    private void example(AlmostGameTestHelper helper) {
        Map<String, Object> values = new HashMap<>();

        var tc = TestConfig.of(b -> {
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

    private static final class Constants {

        private static final String STRING_VALUE_SPEC_DEFAULTS = """
            StringValue1 = ""
            StringValue2 = "DefaultStringValue"
            # This is StringValue3
            StringValue3 = ""
            # This is StringValue4 again
            StringValue4 = ""
            # Possible Values: "Value1", "Value2"
            StringValue5 = ""
            # This is StringValue6
            # Possible Values: "Value1", "Value2"
            StringValue6 = ""
            # This is StringValue7 again
            # Possible Values: "Value1", "Value2"
            StringValue7 = ""
                        
            """;
    }
}
