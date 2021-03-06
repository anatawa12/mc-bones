package com.anatawa12.mbBones;

import com.anatawa12.mbBones.math.Vec3f;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

public class BonedModelRenderer {
    private BonedModelRenderer() {
    }

    public static void drawStaticPart(BonedObject model) {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, VERTEX_FORMAT);
        buffer.putBulkData(model.staticPart);
        model.staticPart.position(0);
        Tessellator.getInstance().draw();
    }

    public static void drawBonedPart(BonedObject model, BoneTreeState state) {
        drawBonedPart(model, state, false);
    }

    public static void drawBonedPart(BonedObject model, BoneTreeState state, boolean debug) {
        if (state.target != model.boneTree)
            throw new IllegalArgumentException("the state is not for tree of model");
        BoneTreeState.ComputedBone[] computed = state.compute();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, VERTEX_FORMAT);
        ByteBuffer bonedPart = model.bonedPart;
        while (model.bonedPart.remaining() != 0) {
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
                buffer.pos(vertex_x, vertex_y, vertex_z)
                        .tex(uv_u, uv_v)
                        .normal(normal_x, normal_y, normal_z)
                        .endVertex();
            } else {
                // currently use global one
                BoneTreeState.ComputedBone bone = computed[boneIndex];
                Vec3f vertex = bone.asGlobal(new Vec3f(vertex_x, vertex_y, vertex_z));
                Vec3f normal = bone.asGlobalDirection(new Vec3f(normal_x, normal_y, normal_z));
                buffer.pos(vertex.x, vertex.y, vertex.z)
                        .tex(uv_u, uv_v)
                        .normal(normal.x, normal.y, normal.z)
                        .endVertex();
            }
        }
        bonedPart.position(0);
        Tessellator.getInstance().draw();
        if (debug) {
            drawBoneSkeleton(model, computed);
            drawNormalVector(model, computed);
        }
    }

    private static void drawBoneSkeleton(BonedObject model, BoneTreeState.ComputedBone[] computed) {
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.glLineWidth(2);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        for (BoneTree.Bone child : model.boneTree.getRoot().children) {
            drawBoneSkeleton(buffer, computed, child, null);
        }

        Tessellator.getInstance().draw();

        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

    private static void drawBoneSkeleton(BufferBuilder buffer, BoneTreeState.ComputedBone[] computedList,
                                  BoneTree.Bone bone, BoneTreeState.ComputedBone parent) {
        BoneTreeState.ComputedBone computed = computedList[bone.id];
        float size = 0.25f;

        Vec3f o = computed.asGlobal(Vec3f.ORIGIN);
        Vec3f x = computed.asGlobal(new Vec3f(size, 0, 0));
        Vec3f y = computed.asGlobal(new Vec3f(0, size, 0));
        Vec3f z = computed.asGlobal(new Vec3f(0, 0, size));
        if (parent != null) {
            Vec3f p = parent.asGlobal(Vec3f.ORIGIN);

            buffer.pos(o.x, o.y, o.z).color(0f, 1f, 1f, 1f).endVertex();
            buffer.pos(p.x, p.y, p.z).color(0f, 1f, 1f, 1f).endVertex();
        }
        buffer.pos(o.x, o.y, o.z).color(1f, 0f, 0f, 1f).endVertex();
        buffer.pos(x.x, x.y, x.z).color(1f, 0f, 0f, 1f).endVertex();
        buffer.pos(o.x, o.y, o.z).color(0f, 1f, 0f, 1f).endVertex();
        buffer.pos(y.x, y.y, y.z).color(0f, 1f, 0f, 1f).endVertex();
        buffer.pos(o.x, o.y, o.z).color(0f, 1f, 0f, 1f).endVertex();
        buffer.pos(z.x, z.y, z.z).color(0f, 0f, 1f, 1f).endVertex();

        for (BoneTree.Bone child : bone.children) {
            drawBoneSkeleton(buffer, computedList, child, computed);
        }
    }

    private static void drawNormalVector(BonedObject model, BoneTreeState.ComputedBone[] computed) {
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.glLineWidth(2);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        ByteBuffer bonedPart = model.bonedPart;
        while (model.bonedPart.remaining() != 0) {
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
                float normal_x1 = normal_x / 8;
                float normal_y1 = normal_y / 8;
                float normal_z1 = normal_z / 8;
                // fast path: global
                buffer.pos(vertex_x, vertex_y, vertex_z)
                        .color(0f, 1f, 1f, 1f).endVertex();
                buffer.pos(vertex_x + normal_x1, vertex_y + normal_y1, vertex_z + normal_z1)
                        .color(0f, 1f, 1f, 1f).endVertex();
            } else {
                // currently use global one
                BoneTreeState.ComputedBone bone = computed[boneIndex];
                Vec3f vertex = bone.asGlobal(new Vec3f(vertex_x, vertex_y, vertex_z));
                Vec3f vertex1 = vertex.add(bone.asGlobalDirection(new Vec3f(normal_x, normal_y, normal_z)).div(8));
                buffer.pos(vertex.x, vertex.y, vertex.z).color(0f, 1f, 1f, 1f).endVertex();
                buffer.pos(vertex1.x, vertex1.y, vertex1.z).color(0f, 1f, 1f, 1f).endVertex();
            }
        }
        bonedPart.position(0);
        Tessellator.getInstance().draw();

        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
    }

    static VertexFormat VERTEX_FORMAT = DefaultVertexFormats.POSITION_TEX_NORMAL;
}
