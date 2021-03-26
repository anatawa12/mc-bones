package com.anatawa12.mbBones

import com.anatawa12.mbBones.BoneTree.TestAccessor.newBone
import com.anatawa12.mbBones.math.Quot
import com.anatawa12.mbBones.math.Vec3f
import com.anatawa12.mbBones.math.VecTestUtil.assertEquals
import org.junit.jupiter.api.Test

internal class BoneTreeTest {
    @Test
    fun boneRelatives() {
        val root = newBone(
            BoneTree.builder(1)
                .setPos(0f, 1f, 0f)
                .setRot(0f, 90f, 0f), mutableSetOf(), null)
        val child = newBone(
            BoneTree.builder(2)
                .setPos(0f, 1f, 1f)
                .setRot(Quot.ORIGIN), mutableSetOf(), root)
        assertEquals(Vec3f(0f, 1f, 0f), child.relativePos, .001f)
        assertEquals(Quot.fromOiler(0f, -90f, 0f), child.relativeRot, .001f)
    }
}
