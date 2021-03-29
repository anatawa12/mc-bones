package com.anatawa12.mbBones.math;

import org.jetbrains.annotations.NotNull;

/*
 * geneated by vecgen.kts
 */

/**
 * simple vector of 2 floats.
 * for UV, x: u and y: v
 */
public class Vec2f {
    public final float x;
    public final float y;

    public static Vec2f ORIGIN = new Vec2f(0, 0);

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public @NotNull Vec2f sub(@NotNull Vec2f pos) {
        return sub(pos.x, pos.y);
    }

    private @NotNull Vec2f sub(float x, float y) {
        return new Vec2f(this.x - x, this.y - y);
    }

    public @NotNull Vec2f add(@NotNull Vec2f pos) {
        return add(pos.x, pos.y);
    }

    public @NotNull Vec2f add(float x, float y) {
        return new Vec2f(this.x + x, this.y + y);
    }

    public @NotNull Vec2f times(float a) {
        return new Vec2f(this.x * a, this.y * a);
    }

    public @NotNull Vec2f div(float a) {
        return new Vec2f(this.x / a, this.y / a);
    }

    public @NotNull float norm() {
        return (float)Math.sqrt(x * x + y * y);
    }

    public @NotNull Vec2f normalized() {
        return div(norm());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (other.getClass() != Vec2f.class) return false;
        Vec2f that = (Vec2f) other;
        if (this.x != that.x) return false;
        if (this.y != that.y) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash * 31 + Float.hashCode(this.x);
        hash = hash * 31 + Float.hashCode(this.y);
        return hash;
    }

    @Override
    public String toString() {
        return "Vec2f(" + x + ", " + y + ")";
    }
}

