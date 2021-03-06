package com.anatawa12.mbBones;

import com.anatawa12.mbBones.math.Vec2f;
import com.anatawa12.mbBones.math.Vec3f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BonedObject {
    final BoneTree boneTree;
    /**
     * built buffer for static part
     */
    final @NotNull ByteBuffer staticPart;

    /**
     * built buffer for static part
     */
    final @NotNull String name;

    /**
     * boned triangles
     *
     * format of ByteBuffer (current, this will be changed)
     * 00..03: float: vertex x (relative from bone position)
     * 04..07: float: vertex y (relative from bone position)
     * 08..11: float: vertex z (relative from bone position)
     * 12..15: float: uv u
     * 16..19: float: uv v
     * 20..20: byte: normal x (signed)
     * 21..21: byte: normal y (signed)
     * 22..22: byte: normal z (signed)
     * 23..23: byte: bone index
     */
    final @NotNull ByteBuffer bonedPart;

    private BonedObject(Builder builder, @NotNull ByteBuffer staticPart, @NotNull ByteBuffer bonedPart) {
        this.boneTree = builder.boneTree;
        this.name = Objects.requireNonNull(builder.name, "name");
        this.staticPart = staticPart;
        this.bonedPart = bonedPart;
    }

    public static Builder builder(@NotNull BoneTree boneTree) {
        return new Builder(boneTree);
    }

    public static class Builder {
        private final BoneTree boneTree;
        private final List<Point> points = new ArrayList<>();
        private final List<Triangle> triangles = new ArrayList<>();
        private @Nullable String name;

        private Builder(BoneTree boneTree) {
            this.boneTree = Objects.requireNonNull(boneTree);
        }

        public static class Point {
            private final int id;
            private final int boneId;
            private final Vec3f relativePos;
            private final Vec3f normal;
            private final Vec2f uv;

            private Point(int id, int boneId, Vec3f relativePos, Vec3f normal, Vec2f uv) {
                this.id = id;
                this.boneId = boneId;
                this.relativePos = relativePos;
                this.normal = normal;
                this.uv = uv;
            }

            /**
             * POSITION_3F, TEX_2F, NORMAL_3B, BONE_1B
             * @param buffer the buffer this vertex will be added to
             */
            private void addTo(ByteBuffer buffer) {
                buffer.putFloat(relativePos.x);
                buffer.putFloat(relativePos.y);
                buffer.putFloat(relativePos.z);
                buffer.putFloat(uv.x);
                buffer.putFloat(uv.y);
                buffer.put((byte) (normal.x * 127));
                buffer.put((byte) (normal.y * 127));
                buffer.put((byte) (normal.z * 127));
                buffer.put((byte) boneId);
            }
        }

        public static class Triangle {
            public final int id;
            private final @NotNull Point point1;
            private final @NotNull Point point2;
            private final @NotNull Point point3;

            public Triangle(int id, @NotNull Point point1, @NotNull Point point2, @NotNull Point point3) {
                this.id = id;
                this.point1 = point1;
                this.point2 = point2;
                this.point3 = point3;
            }
        }

        /**
         * Adds a point to this BonedModel.
         *
         * @param bone the bone the point belongs to
         * @param position the global position of this point
         * @param normal the normal vector of this point
         * @param uv the texture UV of this point.
         * @return the object specifies this point.
         */
        public Point addPoint(@Nullable BoneTree.Bone bone, @NotNull Vec3f position, @NotNull Vec3f normal, @NotNull Vec2f uv) {
            if (bone == null) bone = boneTree.getRoot();
            if (!boneTree.isBoneInThisTree(bone)) throw new IllegalArgumentException("bone(" + bone + ") is not a bone of tree " + boneTree);
            Objects.requireNonNull(position, "position");
            Objects.requireNonNull(normal, "normal");
            Objects.requireNonNull(uv, "uv");
            normal = normal.normalized();
            Point point = new Point(points.size(), bone.id, bone.asRelative(position), bone.asRelativeDirection(normal), uv);
            points.add(point);
            return point;
        }

        private void checkPointBelongsTo(@NotNull Point point, String argumentName) {
            Objects.requireNonNull(point, argumentName);
            if (points.get(point.id) != point)
                throw new IllegalArgumentException(argumentName + " is not belongs to this BonedModel");
        }

        /**
         * Adds a triangle to this BonedModel
         * @param point1 the first point
         * @param point2 the second point
         * @param point3 the third point
         */
        public void addTriangle(@NotNull Point point1, @NotNull Point point2, @NotNull Point point3) {
            checkPointBelongsTo(point1, "point1");
            checkPointBelongsTo(point2, "point2");
            checkPointBelongsTo(point3, "point3");

            Triangle triangle = new Triangle(triangles.size(), point1, point2, point3);
            triangles.add(triangle);
        }

        public void setName(@NotNull String name) {
            this.name = name;
        }

        /**
         * Builds this BonedModel. this process is a little heavy.
         *
         * @return built boned model.
         */
        public BonedObject build() {
            List<Triangle> staticPart = new ArrayList<>();
            List<Triangle> bonedPart = new ArrayList<>();

            for (Triangle triangle : triangles) {
                if (triangle.point1.boneId == 0 && triangle.point2.boneId == 0 && triangle.point3.boneId == 0) {
                    staticPart.add(triangle);
                } else {
                    bonedPart.add(triangle);
                }
            }

            return new BonedObject(
                    this,
                    addTrianglesToBuffer(staticPart),
                    addTrianglesToBuffer(bonedPart)
            );
        }

        private ByteBuffer addTrianglesToBuffer(List<Triangle> bonedPart) {
            ByteBuffer buffer = ByteBuffer.allocate(24 * 3 * bonedPart.size());
            buffer.order(ByteOrder.nativeOrder());
            for (Triangle triangle : bonedPart) {
                triangle.point1.addTo(buffer);
                triangle.point2.addTo(buffer);
                triangle.point3.addTo(buffer);
            }
            buffer.flip();
            return buffer;
        }
    }
}
