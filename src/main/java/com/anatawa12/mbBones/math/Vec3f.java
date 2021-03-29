package com.anatawa12.mbBones.math;

import org.jetbrains.annotations.NotNull;

/*
 * geneated by vecgen.kts
 */

/**
 * simple vector of 3 floats.
 * for rotation, x:pitch y:heading z: bank
 * Rotate heading first, then pitch and bank.
 */
public class Vec3f {
    public final float x;
    public final float y;
    public final float z;

    public static Vec3f ORIGIN = new Vec3f(0, 0, 0);

    public Vec3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public @NotNull Vec3f sub(@NotNull Vec3f pos) {
        return sub(pos.x, pos.y, pos.z);
    }

    private @NotNull Vec3f sub(float x, float y, float z) {
        return new Vec3f(this.x - x, this.y - y, this.z - z);
    }

    public @NotNull Vec3f add(@NotNull Vec3f pos) {
        return add(pos.x, pos.y, pos.z);
    }

    public @NotNull Vec3f add(float x, float y, float z) {
        return new Vec3f(this.x + x, this.y + y, this.z + z);
    }

    public @NotNull Vec3f times(float a) {
        return new Vec3f(this.x * a, this.y * a, this.z * a);
    }

    public @NotNull Vec3f div(float a) {
        return new Vec3f(this.x / a, this.y / a, this.z / a);
    }

    public @NotNull float norm() {
        return (float)Math.sqrt(x * x + y * y + z * z);
    }

    public @NotNull Vec3f normalized() {
        return div(norm());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (other.getClass() != Vec3f.class) return false;
        Vec3f that = (Vec3f) other;
        if (this.x != that.x) return false;
        if (this.y != that.y) return false;
        if (this.z != that.z) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash * 31 + Float.hashCode(this.x);
        hash = hash * 31 + Float.hashCode(this.y);
        hash = hash * 31 + Float.hashCode(this.z);
        return hash;
    }

    @Override
    public String toString() {
        return "Vec3f(" + x + ", " + y + ", " + z + ")";
    }
}

