package com.anatawa12.mbBones.math;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * The utilities for quotation
 */
public class Quot {
    public static final @NotNull Vec4f ORIGIN = new Vec4f(0, 0, 0, 1);

    private Quot() {
    }

    @Contract(pure = true)
    public static @NotNull Vec4f times(@NotNull Vec4f a, @NotNull Vec4f b) {
        return new Vec4f(
                +a.w * b.x - a.z * b.y + a.y * b.z + a.x * b.w,
                +a.z * b.x + a.w * b.y - a.x * b.z + a.y * b.w,
                -a.y * b.x + a.x * b.y + a.w * b.z + a.z * b.w,
                -a.x * b.x - a.y * b.y - a.z * b.z + a.w * b.w
        );
    }

    @Contract(pure = true)
    public static @NotNull Vec4f invert(@NotNull Vec4f a) {
        return new Vec4f(-a.x, -a.y, -a.z, a.w);
    }

    @Contract(pure = true)
    public static Vec3f rotate(@NotNull Vec4f a, @NotNull Vec3f b) {
        float x1 = +a.w * b.x - a.z * b.y + a.y * b.z;
        float y1 = +a.z * b.x + a.w * b.y - a.x * b.z;
        float z1 = -a.y * b.x + a.x * b.y + a.w * b.z;
        float w1 = -a.x * b.x - a.y * b.y - a.z * b.z;

        float x2 = +w1 * -a.x - z1 * -a.y + y1 * -a.z + x1 * a.w;
        float y2 = +z1 * -a.x + w1 * -a.y - x1 * -a.z + y1 * a.w;
        float z2 = -y1 * -a.x + x1 * -a.y + w1 * -a.z + z1 * a.w;
        //float w2 = 0; always zero
        return new Vec3f(x2, y2, z2);
    }

    @Contract(pure = true)
    public static @NotNull Vec4f fromOiler(float heading, float pitch, float bank) {
        float hRad = heading * MathF.deg2Rad;
        float pRad = pitch * MathF.deg2Rad;
        float bRad = bank * MathF.deg2Rad;

        float hSin = MathF.sin(hRad / 2);
        float hCos = MathF.cos(hRad / 2);
        float pSin = MathF.sin(pRad / 2);
        float pCos = MathF.cos(pRad / 2);
        float bSin = MathF.sin(bRad / 2);
        float bCos = MathF.cos(bRad / 2);

        // equals to this code. rotate hed>pit>bnk locally so bnk>pit>hed globally
        // @NotNull Vec4f current = ORIGIN
        // current = times(new Vec4f(0, 0, bSin, bCos), current)
        // current = times(new Vec4f(pSin, 0, 0, pCos), current)
        // current = times(new Vec4f(0, hSin, 0, hCos), current)
        // return current

        float x = +hSin * pCos * bSin + hCos * pSin * bCos;
        float y = -hCos * pSin * bSin + hSin * pCos * bCos;
        float z = -hSin * pSin * bCos + hCos * pCos * bSin;
        float w = +hSin * pSin * bSin + hCos * pCos * bCos;
        return new Vec4f(x, y, z, w);
    }
}
