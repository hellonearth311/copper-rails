package net.hellonearth311.copperrails.oxidize;

import net.minecraft.util.StringIdentifiable;

public enum OxidationLevel implements StringIdentifiable {
    UNAFFECTED("unaffected", 0.75f, false),
    EXPOSED("exposed", 0.6875f, false),
    WEATHERED("weathered", 0.625f, false),
    OXIDIZED("oxidized", 0.5625f, false),
    WAXED_UNAFFECTED("waxed_unaffected", 0.75f, true),
    WAXED_EXPOSED("waxed_exposed", 0.6875f, true),
    WAXED_WEATHERED("waxed_weathered", 0.625f, true),
    WAXED_OXIDIZED("waxed_oxidized", 0.5625f, true);

    private final String name;
    private final float speedMultiplier;
    private final boolean waxed;

    OxidationLevel(String name, float speedMultiplier, boolean waxed) {
        this.name = name;
        this.speedMultiplier = speedMultiplier;
        this.waxed = waxed;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public float getSpeedMultiplier() {
        return this.speedMultiplier;
    }

    public boolean isWaxed() {
        return this.waxed;
    }

    public OxidationLevel getWaxed() {
        return switch (this) {
            case UNAFFECTED -> WAXED_UNAFFECTED;
            case EXPOSED -> WAXED_EXPOSED;
            case WEATHERED -> WAXED_WEATHERED;
            case OXIDIZED -> WAXED_OXIDIZED;
            case WAXED_UNAFFECTED, WAXED_EXPOSED, WAXED_WEATHERED, WAXED_OXIDIZED -> this;
        };
    }

    public OxidationLevel getUnwaxed() {
        return switch (this) {
            case WAXED_UNAFFECTED -> UNAFFECTED;
            case WAXED_EXPOSED -> EXPOSED;
            case WAXED_WEATHERED -> WEATHERED;
            case WAXED_OXIDIZED -> OXIDIZED;
            case UNAFFECTED, EXPOSED, WEATHERED, OXIDIZED -> this;
        };
    }

    public OxidationLevel getNext() {
        if (this.waxed) return this;
        return switch (this) {
            case UNAFFECTED -> EXPOSED;
            case EXPOSED -> WEATHERED;
            case WEATHERED -> OXIDIZED;
            case OXIDIZED -> OXIDIZED;
            default -> this;
        };
    }

    public OxidationLevel getPrevious() {
        return switch (this) {
            case UNAFFECTED -> UNAFFECTED;
            case EXPOSED -> UNAFFECTED;
            case WEATHERED -> EXPOSED;
            case OXIDIZED -> WEATHERED;
            case WAXED_UNAFFECTED -> WAXED_UNAFFECTED;
            case WAXED_EXPOSED -> WAXED_UNAFFECTED;
            case WAXED_WEATHERED -> WAXED_EXPOSED;
            case WAXED_OXIDIZED -> WAXED_WEATHERED;
        };
    }

    public boolean isFullyOxidized() {
        return this == OXIDIZED || this == WAXED_OXIDIZED;
    }
}
