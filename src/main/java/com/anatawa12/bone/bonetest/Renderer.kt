package com.anatawa12.bone.bonetest

import com.anatawa12.mbBones.BoneTree
import com.anatawa12.mbBones.BonedModel
import com.anatawa12.mbBones.math.Vec2f
import com.anatawa12.mbBones.math.Vec3f
import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

object Renderer : TileEntitySpecialRenderer<TestTile>() {
    val bonedModel = kotlin.run {
        val root = BoneTree.builder(0).apply {
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
        BonedModel.builder(root).apply {

            val p0 = addPoint(root.getById(0)!!, Vec3f(0.0f, 1.5f, 0.0f), Vec3f(0f, 1f, 0f), Vec2f(0.00f, 0.0f))
            val p1 = addPoint(root.getById(0)!!, Vec3f(1.0f, 1.5f, 0.0f), Vec3f(0f, 1f, 0f), Vec2f(0.00f, 1.0f))

            val p2 = addPoint(root.getById(1)!!, Vec3f(0.0f, 1.5f, 1.0f), Vec3f(0f, 1f, 0f), Vec2f(0.25f, 0.0f))
            val p3 = addPoint(root.getById(1)!!, Vec3f(1.0f, 1.5f, 1.0f), Vec3f(0f, 1f, 0f), Vec2f(0.25f, 1.0f))

            val p4 = addPoint(root.getById(2)!!, Vec3f(0.0f, 1.5f, 2.0f), Vec3f(0f, 1f, 0f), Vec2f(0.50f, 0.0f))
            val p5 = addPoint(root.getById(2)!!, Vec3f(1.0f, 1.5f, 2.0f), Vec3f(0f, 1f, 0f), Vec2f(0.50f, 1.0f))

            val p6 = addPoint(root.getById(3)!!, Vec3f(0.0f, 1.5f, 3.0f), Vec3f(0f, 1f, 0f), Vec2f(0.75f, 0.0f))
            val p7 = addPoint(root.getById(3)!!, Vec3f(1.0f, 1.5f, 3.0f), Vec3f(0f, 1f, 0f), Vec2f(0.75f, 1.0f))

            val p8 = addPoint(root.getById(4)!!, Vec3f(0.0f, 1.5f, 4.0f), Vec3f(0f, 1f, 0f), Vec2f(1.00f, 0.0f))
            val p9 = addPoint(root.getById(4)!!, Vec3f(1.0f, 1.5f, 4.0f), Vec3f(0f, 1f, 0f), Vec2f(1.00f, 1.0f))

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

    override fun render(
        te: TestTile,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
        destroyStage: Int,
        alpha: Float,
    ) {
        bindTexture(ResourceLocation("textures/blocks/bedrock.png"))

        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)

        val bufferbuilder = Tessellator.getInstance().buffer

        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL)

        bufferbuilder.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).normal(0f, 0f, -1f).endVertex()
        bufferbuilder.pos(0.0, 1.0, 0.0).tex(0.0, 1.0).normal(0f, 0f, -1f).endVertex()
        bufferbuilder.pos(1.0, 1.0, 0.0).tex(1.0, 1.0).normal(0f, 0f, -1f).endVertex()
        bufferbuilder.pos(1.0, 0.0, 0.0).tex(1.0, 0.0).normal(0f, 0f, -1f).endVertex()

        bufferbuilder.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).normal(-1f, 0f, 0f).endVertex()
        bufferbuilder.pos(0.0, 0.0, 1.0).tex(0.0, 1.0).normal(-1f, 0f, 0f).endVertex()
        bufferbuilder.pos(0.0, 1.0, 1.0).tex(1.0, 1.0).normal(-1f, 0f, 0f).endVertex()
        bufferbuilder.pos(0.0, 1.0, 0.0).tex(1.0, 0.0).normal(-1f, 0f, 0f).endVertex()

        bufferbuilder.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).normal(0f, -1f, 0f).endVertex()
        bufferbuilder.pos(1.0, 0.0, 0.0).tex(0.0, 1.0).normal(0f, -1f, 0f).endVertex()
        bufferbuilder.pos(1.0, 0.0, 1.0).tex(1.0, 1.0).normal(0f, -1f, 0f).endVertex()
        bufferbuilder.pos(0.0, 0.0, 1.0).tex(1.0, 0.0).normal(0f, -1f, 0f).endVertex()

        bufferbuilder.pos(0.0, 0.0, 1.0).tex(0.0, 0.0).normal(0f, 0f, 1f).endVertex()
        bufferbuilder.pos(1.0, 0.0, 1.0).tex(1.0, 0.0).normal(0f, 0f, 1f).endVertex()
        bufferbuilder.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).normal(0f, 0f, 1f).endVertex()
        bufferbuilder.pos(0.0, 1.0, 1.0).tex(0.0, 1.0).normal(0f, 0f, 1f).endVertex()

        bufferbuilder.pos(1.0, 0.0, 0.0).tex(0.0, 0.0).normal(1f, 0f, 0f).endVertex()
        bufferbuilder.pos(1.0, 1.0, 0.0).tex(1.0, 0.0).normal(1f, 0f, 0f).endVertex()
        bufferbuilder.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).normal(1f, 0f, 0f).endVertex()
        bufferbuilder.pos(1.0, 0.0, 1.0).tex(0.0, 1.0).normal(1f, 0f, 0f).endVertex()

        bufferbuilder.pos(0.0, 1.0, 0.0).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
        bufferbuilder.pos(0.0, 1.0, 1.0).tex(1.0, 0.0).normal(0f, 1f, 0f).endVertex()
        bufferbuilder.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).normal(0f, 1f, 0f).endVertex()
        bufferbuilder.pos(1.0, 1.0, 0.0).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()
        Tessellator.getInstance().draw()

        bonedModel.drawStaticPart()
        bonedModel.drawBonedPart()

        /*
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL)

        bufferbuilder.pos(1.0, 1.5, 0.0).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()
        bufferbuilder.pos(0.0, 1.5, 0.0).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
        bufferbuilder.pos(0.0, 1.5, 1.0).tex(1.0, 0.0).normal(0f, 1f, 0f).endVertex()
        bufferbuilder.pos(1.0, 1.5, 1.0).tex(1.0, 1.0).normal(0f, 1f, 0f).endVertex()

        bufferbuilder.pos(1.0, 1.5, 1.0).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()
        bufferbuilder.pos(0.0, 1.5, 1.0).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
        bufferbuilder.pos(0.0, 1.5, 2.0).tex(1.0, 0.0).normal(0f, 1f, 0f).endVertex()
        bufferbuilder.pos(1.0, 1.5, 2.0).tex(1.0, 1.0).normal(0f, 1f, 0f).endVertex()

        bufferbuilder.pos(1.0, 1.5, 2.0).tex(0.0, 1.0).normal(0f, 1f, 0f).endVertex()
        bufferbuilder.pos(0.0, 1.5, 2.0).tex(0.0, 0.0).normal(0f, 1f, 0f).endVertex()
        bufferbuilder.pos(0.0, 1.5, 3.0).tex(1.0, 0.0).normal(0f, 1f, 0f).endVertex()
        bufferbuilder.pos(1.0, 1.5, 3.0).tex(1.0, 1.0).normal(0f, 1f, 0f).endVertex()

        Tessellator.getInstance().draw()
         */

        GlStateManager.popMatrix()
    }
}
