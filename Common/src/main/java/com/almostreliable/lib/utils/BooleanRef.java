package com.almostreliable.lib.utils;

public class BooleanRef {
    private boolean value;

    public BooleanRef(boolean value) {
        this.value = value;
    }

    public boolean get() {
        return value;
    }

    public void set(boolean value) {
        this.value = value;
    }
}
