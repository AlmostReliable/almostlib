package com.almostreliable.almostlib.client;

/**
 * A basic abstraction of Minecraft's {@code RenderType} class for
 * usage in code that is also used on the server.
 * <p>
 * Used in block registration to automatically register the render layer.
 */
public enum RenderLayerType {
    SOLID("solid"),
    CUTOUT("cutout"),
    CUTOUT_MIPPED("cutout_mipped"),
    TRANSLUCENT("translucent");

    private final String jsonDefinition;

    RenderLayerType(String jsonDefinition) {
        this.jsonDefinition = jsonDefinition;
    }

    public String getJsonDefinition() {
        return jsonDefinition;
    }
}
