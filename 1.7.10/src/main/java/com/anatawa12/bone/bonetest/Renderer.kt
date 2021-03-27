package com.anatawa12.bone.bonetest

import com.anatawa12.mbBones.BoneTree
import com.anatawa12.mbBones.BoneTreeState
import com.anatawa12.mbBones.BonedModel
import com.anatawa12.mbBones.BonedModelRenderer
import com.anatawa12.mbBones.math.Quot
import com.anatawa12.mbBones.math.Vec2f
import com.anatawa12.mbBones.math.Vec3f
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import kotlin.math.abs

object Renderer : TileEntitySpecialRenderer() {
    val boneTree = BoneTree.builder(0).apply {
        BoneTree.builder(1).apply {
            setPos(0.0f, 1.5f, 1.0f)
            setRot(0f, 0f, 0f)
            BoneTree.builder(2).apply {
                setPos(0.0f, 1.5f, 2.0f)
                setRot(0f, 0f, 0f)
                BoneTree.builder(3).apply {
                    setPos(0.0f, 1.5f, 3.0f)
                    setRot(0f, 0f, 0f)
                    BoneTree.builder(4).apply {
                        setPos(0.0f, 1.5f, 4.0f)
                        setRot(0f, 0f, 0f)
                    }.setParent(this)
                }.setParent(this)
            }.setParent(this)
        }.setParent(this)
    }.build()
    val bonedModel = kotlin.run {
        BonedModel.builder(boneTree).apply {

            val p0 = addPoint(boneTree.getById(0), Vec3f(0.0f, 1.5f, 0.0f), Vec3f(0f, 1f, 0f), Vec2f(0.00f, 0.0f))
            val p1 = addPoint(boneTree.getById(0), Vec3f(1.0f, 1.5f, 0.0f), Vec3f(0f, 1f, 0f), Vec2f(0.00f, 1.0f))

            val p2 = addPoint(boneTree.getById(1), Vec3f(0.0f, 1.5f, 1.0f), Vec3f(0f, 1f, 0f), Vec2f(0.25f, 0.0f))
            val p3 = addPoint(boneTree.getById(1), Vec3f(1.0f, 1.5f, 1.0f), Vec3f(0f, 1f, 0f), Vec2f(0.25f, 1.0f))

            val p4 = addPoint(boneTree.getById(2), Vec3f(0.0f, 1.5f, 2.0f), Vec3f(0f, 1f, 0f), Vec2f(0.50f, 0.0f))
            val p5 = addPoint(boneTree.getById(2), Vec3f(1.0f, 1.5f, 2.0f), Vec3f(0f, 1f, 0f), Vec2f(0.50f, 1.0f))

            val p6 = addPoint(boneTree.getById(3), Vec3f(0.0f, 1.5f, 3.0f), Vec3f(0f, 1f, 0f), Vec2f(0.75f, 0.0f))
            val p7 = addPoint(boneTree.getById(3), Vec3f(1.0f, 1.5f, 3.0f), Vec3f(0f, 1f, 0f), Vec2f(0.75f, 1.0f))

            val p8 = addPoint(boneTree.getById(4), Vec3f(0.0f, 1.5f, 4.0f), Vec3f(0f, 1f, 0f), Vec2f(1.00f, 0.0f))
            val p9 = addPoint(boneTree.getById(4), Vec3f(1.0f, 1.5f, 4.0f), Vec3f(0f, 1f, 0f), Vec2f(1.00f, 1.0f))

            addTriangle(p2, p1, p0)
            addTriangle(p2, p3, p1)

            addTriangle(p4, p3, p2)
            addTriangle(p4, p5, p3)

            addTriangle(p6, p5, p4)
            addTriangle(p6, p7, p5)

            addTriangle(p8, p7, p6)
            addTriangle(p8, p9, p7)
        }.build()
    }

    override fun renderTileEntityAt(
        te: TileEntity,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
    ) {
        te as TestTile
        bindTexture(ResourceLocation("textures/blocks/bedrock.png"))

        GL11.glPushMatrix()
        GL11.glTranslated(x, y, z)

        val time = abs((System.currentTimeMillis() / 1000.0) % 4 - 2).toFloat() - 1f
        val state = BoneTreeState(boneTree)
        for (i in 1 until boneTree.boneCount()) {
            state.getBone(i).rot = Quot.fromOiler(0f, time * 30, 0f)
            state.getBone(i).pos = Vec3f(0f, time / 4, time / 4)
        }
        BonedModelRenderer.drawStaticPart(bonedModel)
        BonedModelRenderer.drawBonedPart(bonedModel, state, true)

        GL11.glPopMatrix()
    }
}
