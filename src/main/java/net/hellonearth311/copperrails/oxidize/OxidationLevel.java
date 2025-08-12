package net.hellonearth311.copperrails.oxidize;

import net.minecraft.util.StringIdentifiable;

public enum OxidationLevel implements StringIdentifiable {
    UNAFFECTED("unaffected", 0.75f),
    EXPOSED("exposed", 0.6875f),
    WEATHERED("weathered", 0.625f),
    OXIDIZED("oxidized", 0.5625f);

    private final String name;
    private final float speedMultiplier;

    OxidationLevel(String name, float speedMultiplier) {
        this.name = name;
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public float getSpeedMultiplier() {
        return this.speedMultiplier;
    }

    public OxidationLevel getNext() {
        return switch (this) {
            case UNAFFECTED -> EXPOSED;
            case EXPOSED -> WEATHERED;
            case WEATHERED -> OXIDIZED;
            case OXIDIZED -> OXIDIZED;
        };
    }

    public OxidationLevel getPrevious() {
        return switch (this) {
            case UNAFFECTED -> UNAFFECTED;
            case EXPOSED -> UNAFFECTED;
            case WEATHERED -> EXPOSED;
            case OXIDIZED -> WEATHERED;
        };
    }

    public boolean isFullyOxidized() {
        return this == OXIDIZED;
    }
}
