package com.anatawa12.bone.bonetest

import com.anatawa12.mbBones.*
import com.anatawa12.mbBones.math.Quot
import com.anatawa12.mbBones.math.Vec2f
import com.anatawa12.mbBones.math.Vec3f
import com.anatawa12.mbBones.model.IFileLoader
import com.anatawa12.mbBones.model.mqo.MqoFileReader
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.io.File
import java.io.InputStream
import kotlin.math.abs

object Renderer : TileEntitySpecialRenderer<TestTile>() {
    val model = Models.mqoModel()
    val boneTree = model.boneTree
    val bonedModel = model.objects.first()

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

        val time = abs((System.currentTimeMillis() / 1000.0) % 4 - 2).toFloat() - 1f
        val state = BoneTreeState(boneTree)
        for (i in 1 until boneTree.boneCount()) {
            state.getBone(i).rot = Quot.fromOiler(0f, time * 30, 0f)
            state.getBone(i).pos = Vec3f(0f, time / 4, time / 4)
        }
        BonedModelRenderer.drawStaticPart(bonedModel, )
        BonedModelRenderer.drawBonedPart(bonedModel, state, true)

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
