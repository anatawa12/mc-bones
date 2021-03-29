package com.anatawa12.mbBones.math;

import org.jetbrains.annotations.NotNull;

public class Vec3Util {
    private Vec3Util() {
    }

    public static @NotNull Vec3f cross(@NotNull Vec3f a, @NotNull Vec3f b) {
        return new Vec3f(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x
        );
    }

    public static float dot(@NotNull Vec3f a, @NotNull Vec3f b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }
}

