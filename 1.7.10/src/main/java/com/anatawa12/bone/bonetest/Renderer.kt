package com.anatawa12.bone.bonetest

import com.anatawa12.mbBones.BoneTreeState
import com.anatawa12.mbBones.BonedModelRenderer
import com.anatawa12.mbBones.math.Quot
import com.anatawa12.mbBones.math.Vec3f
import com.anatawa12.mbBones.model.IFileLoader
import com.anatawa12.mbBones.model.mqo.MqoFileReader
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.io.File
import java.io.InputStream
import kotlin.math.abs

object Renderer : TileEntitySpecialRenderer() {
    val model = Models.mqoModel()

    val boneTree = model.boneTree
    val bonedModel = model.objects.first()

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
