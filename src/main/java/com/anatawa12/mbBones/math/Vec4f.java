package com.anatawa12.mbBones.math;

import org.jetbrains.annotations.NotNull;

/*
 * geneated by vecgen.kts
 */

/**
 * simple vector of 4 floats.
 * as a quotanion, w + ix + jy + kz.
 */
public class Vec4f {
    public final float x;
    public final float y;
    public final float z;
    public final float w;

    public static Vec4f ORIGIN = new Vec4f(0, 0, 0, 0);

    public Vec4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public @NotNull Vec4f sub(@NotNull Vec4f pos) {
        return sub(pos.x, pos.y, pos.z, pos.w);
    }

    private @NotNull Vec4f sub(float x, float y, float z, float w) {
        return new Vec4f(this.x - x, this.y - y, this.z - z, this.w - w);
    }

    public @NotNull Vec4f add(@NotNull Vec4f pos) {
        return add(pos.x, pos.y, pos.z, pos.w);
    }

    public @NotNull Vec4f add(float x, float y, float z, float w) {
        return new Vec4f(this.x + x, this.y + y, this.z + z, this.w + w);
    }

    public @NotNull Vec4f times(float a) {
        return new Vec4f(this.x * a, this.y * a, this.z * a, this.w * a);
    }

    public @NotNull Vec4f div(float a) {
        return new Vec4f(this.x / a, this.y / a, this.z / a, this.w / a);
    }

    public @NotNull float norm() {
        return (float)Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public @NotNull Vec4f normalized() {
        return div(norm());
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (other.getClass() != Vec4f.class) return false;
        Vec4f that = (Vec4f) other;
        if (this.x != that.x) return false;
        if (this.y != that.y) return false;
        if (this.z != that.z) return false;
        if (this.w != that.w) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash = hash * 31 + Float.hashCode(this.x);
        hash = hash * 31 + Float.hashCode(this.y);
        hash = hash * 31 + Float.hashCode(this.z);
        hash = hash * 31 + Float.hashCode(this.w);
        return hash;
    }

    @Override
    public String toString() {
        return "Vec4f(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
}

