package com.anatawa12.mbBones.math;

import org.jetbrains.annotations.Contract;

/**
 * mathematical utils for floats
 */
public class MathF {
    private MathF() {
    }

    public static final float PI = 3.14159265f;
    public static final float deg2Rad = PI / 180;

    @Contract(pure=true)
    public static float sin(float x) {
        return (float) Math.sin(x);
    }

    @Contract(pure=true)
    public static float cos(float x) {
        return (float) Math.cos(x);
    }
}
