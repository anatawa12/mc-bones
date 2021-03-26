package com.anatawa12.mbBones.math;

import org.junit.jupiter.api.Test;

import static com.anatawa12.mbBones.math.VecTestUtil.*;

class QuotTest {

    @Test
    void times() {
    }

    @Test
    void invert() {
    }

    @Test
    void rotate() {
    }

    @Test
    void fromOiler() {
        Vec4f quot = Quot.fromOiler(30, 30, 30);
        assertEquals(new Vec3f(+.875f, +.433f, -.2165f), Quot.rotate(quot, new Vec3f(1, 0, 0)), 0.001f);
        assertEquals(new Vec3f(-.875f, -.433f, +.2165f), Quot.rotate(quot, new Vec3f(-1, 0, 0)), 0.001f);
    }
}
