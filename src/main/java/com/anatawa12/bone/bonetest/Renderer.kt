package com.anatawa12.bone.bonetest

import net.minecraft.client.model.ModelBase
import net.minecraft.client.model.ModelRenderer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

object Renderer : TileEntitySpecialRenderer<TestTile>() {
    override fun render(
        te: TestTile,
        x: Double,
        y: Double,
        z: Double,
        partialTicks: Float,
        destroyStage: Int,
        alpha: Float,
    ) {
        GlStateManager.disableLighting()

        bindTexture(ResourceLocation("textures/blocks/bedrock.png"))

        GlStateManager.pushMatrix()
        GlStateManager.translate(x, y, z)

        val bufferbuilder = Tessellator.getInstance().buffer

        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)

        bufferbuilder.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).endVertex()
        bufferbuilder.pos(0.0, 1.0, 0.0).tex(0.0, 1.0).endVertex()
        bufferbuilder.pos(1.0, 1.0, 0.0).tex(1.0, 1.0).endVertex()
        bufferbuilder.pos(1.0, 0.0, 0.0).tex(1.0, 0.0).endVertex()

        bufferbuilder.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).endVertex()
        bufferbuilder.pos(0.0, 0.0, 1.0).tex(0.0, 1.0).endVertex()
        bufferbuilder.pos(0.0, 1.0, 1.0).tex(1.0, 1.0).endVertex()
        bufferbuilder.pos(0.0, 1.0, 0.0).tex(1.0, 0.0).endVertex()

        bufferbuilder.pos(0.0, 0.0, 0.0).tex(0.0, 0.0).endVertex()
        bufferbuilder.pos(1.0, 0.0, 0.0).tex(0.0, 1.0).endVertex()
        bufferbuilder.pos(1.0, 0.0, 1.0).tex(1.0, 1.0).endVertex()
        bufferbuilder.pos(0.0, 0.0, 1.0).tex(1.0, 0.0).endVertex()

        bufferbuilder.pos(0.0, 0.0, 1.0).tex(0.0, 0.0).endVertex()
        bufferbuilder.pos(1.0, 0.0, 1.0).tex(1.0, 0.0).endVertex()
        bufferbuilder.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).endVertex()
        bufferbuilder.pos(0.0, 1.0, 1.0).tex(0.0, 1.0).endVertex()

        bufferbuilder.pos(1.0, 0.0, 0.0).tex(0.0, 0.0).endVertex()
        bufferbuilder.pos(1.0, 1.0, 0.0).tex(1.0, 0.0).endVertex()
        bufferbuilder.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).endVertex()
        bufferbuilder.pos(1.0, 0.0, 1.0).tex(0.0, 1.0).endVertex()

        bufferbuilder.pos(0.0, 1.0, 0.0).tex(0.0, 0.0).endVertex()
        bufferbuilder.pos(0.0, 1.0, 1.0).tex(1.0, 0.0).endVertex()
        bufferbuilder.pos(1.0, 1.0, 1.0).tex(1.0, 1.0).endVertex()
        bufferbuilder.pos(1.0, 1.0, 0.0).tex(0.0, 1.0).endVertex()
        Tessellator.getInstance().draw()

        GlStateManager.popMatrix()
        GlStateManager.enableLighting()
    }
}
