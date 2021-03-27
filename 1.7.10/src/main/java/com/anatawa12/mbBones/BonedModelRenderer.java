package com.anatawa12.mbBones;

import com.anatawa12.mbBones.math.Vec3f;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

public class BonedModelRenderer {
    private BonedModelRenderer() {
    }

    public static void drawStaticPart(BonedModel model) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_TRIANGLES);
        ByteBuffer staticPart = model.staticPart;
        while (model.staticPart.remaining() != 0) {
            //noinspection DuplicatedCode
            float vertex_x = staticPart.getFloat();
            float vertex_y = staticPart.getFloat();
            float vertex_z = staticPart.getFloat();
            float uv_u = staticPart.getFloat();
            float uv_v = staticPart.getFloat();
            float normal_x = staticPart.get() / 127f;
            float normal_y = staticPart.get() / 127f;
            float normal_z = staticPart.get() / 127f;
            staticPart.get(); // skip padding
            // fast path: global
            tessellator.setTextureUV(uv_u, uv_v);
            tessellator.setNormal(normal_x, normal_y, normal_z);
            tessellator.addVertex(vertex_x, vertex_y, vertex_z);
        }
        staticPart.position(0);
        tessellator.draw();
    }

    public static void drawBonedPart(BonedModel model, BoneTreeState state) {
        drawBonedPart(model, state, false);
    }

    public static void drawBonedPart(BonedModel model, BoneTreeState state, boolean boneSkeleton) {
        if (state.target != model.boneTree)
            throw new IllegalArgumentException("the state is not for tree of model");
        BoneTreeState.ComputedBone[] computed = state.compute();
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_TRIANGLES);
        ByteBuffer bonedPart = model.bonedPart;
        while (model.bonedPart.remaining() != 0) {
            //noinspection DuplicatedCode
            float vertex_x = bonedPart.getFloat();
            float vertex_y = bonedPart.getFloat();
            float vertex_z = bonedPart.getFloat();
            float uv_u = bonedPart.getFloat();
            float uv_v = bonedPart.getFloat();
            float normal_x = bonedPart.get() / 127f;
            float normal_y = bonedPart.get() / 127f;
            float normal_z = bonedPart.get() / 127f;
            int boneIndex = (int)bonedPart.get() & 0xFF;
            if (boneIndex == 0) {
                // fast path: global
                tessellator.setTextureUV(uv_u, uv_v);
                tessellator.setNormal(normal_x, normal_y, normal_z);
                tessellator.addVertex(vertex_x, vertex_y, vertex_z);
            } else {
                // currently use global one
                BoneTreeState.ComputedBone bone = computed[boneIndex];
                Vec3f vertex = bone.asGlobal(new Vec3f(vertex_x, vertex_y, vertex_z));
                Vec3f normal = bone.asGlobalDirection(new Vec3f(normal_x, normal_y, normal_z));
                tessellator.setTextureUV(uv_u, uv_v);
                tessellator.setNormal(normal.x, normal.y, normal.z);
                tessellator.addVertex(vertex.x, vertex.y, vertex.z);
            }
        }
        bonedPart.position(0);
        tessellator.draw();

        if (boneSkeleton)
            drawBoneSkeleton(model, computed);
    }

    private static void drawBoneSkeleton(BonedModel model, BoneTreeState.ComputedBone[] computed) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glLineWidth(2);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(GL11.GL_LINES);

        for (BoneTree.Bone child : model.boneTree.getRoot().children) {
            drawBoneSkeleton(tessellator, computed, child, null);
        }

        tessellator.draw();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private static void drawBoneSkeleton(Tessellator tessellator, BoneTreeState.ComputedBone[] computedList,
                                  BoneTree.Bone bone, BoneTreeState.ComputedBone parent) {
        BoneTreeState.ComputedBone computed = computedList[bone.id];
        float size = 0.25f;

        Vec3f o = computed.asGlobal(Vec3f.ORIGIN);
        Vec3f x = computed.asGlobal(new Vec3f(size, 0, 0));
        Vec3f y = computed.asGlobal(new Vec3f(0, size, 0));
        Vec3f z = computed.asGlobal(new Vec3f(0, 0, size));
        if (parent != null) {
            Vec3f p = parent.asGlobal(Vec3f.ORIGIN);

            tessellator.setColorRGBA(0, 255, 255, 255);
            tessellator.addVertex(o.x, o.y, o.z);
            tessellator.addVertex(p.x, p.y, p.z);
        }
        tessellator.setColorRGBA(255, 0, 0, 255);
        tessellator.addVertex(o.x, o.y, o.z);
        tessellator.addVertex(x.x, x.y, x.z);
        tessellator.setColorRGBA(0, 255, 0, 255);
        tessellator.addVertex(o.x, o.y, o.z);
        tessellator.addVertex(y.x, y.y, y.z);
        tessellator.setColorRGBA(0, 0, 255, 255);
        tessellator.addVertex(o.x, o.y, o.z);
        tessellator.addVertex(z.x, z.y, z.z);

        for (BoneTree.Bone child : bone.children) {
            drawBoneSkeleton(tessellator, computedList, child, computed);
        }
    }
}
