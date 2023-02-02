package com.almostreliable.almostlib.client;

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
