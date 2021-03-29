package com.anatawa12.mbBones;

import com.anatawa12.mbBones.math.Quot;
import com.anatawa12.mbBones.math.Vec3f;
import com.anatawa12.mbBones.math.Vec4f;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.anatawa12.mbBones.math.Quot.invert;
import static com.anatawa12.mbBones.math.Quot.rotate;
import static com.anatawa12.mbBones.math.Quot.times;

public class BoneTree {
    private final @NotNull @Unmodifiable Map<@NotNull String, @NotNull Bone> byName;
    private final @NotNull @Unmodifiable Map<@NotNull Integer, @NotNull Bone> byExternalId;
    private final @NotNull Bone root;
    private final @NotNull Bone @NotNull[] boneList;

    private BoneTree(@NotNull Builder builder) {
        Set<@NotNull Bone> bones = new HashSet<>();
        this.root = new Bone(builder, bones, null);
        if (bones.size() > 256)
            throw new IllegalArgumentException("too many bones are exists. maximum: 256, currently: " + bones.size());

        @NotNull Bone @NotNull[] boneList = new Bone[bones.size()];
        ImmutableMap.Builder<@NotNull String, @NotNull Bone> byName = ImmutableMap.builder();
        ImmutableMap.Builder<@NotNull Integer, @NotNull Bone> byExternalId = ImmutableMap.builder();
        for (Bone bone : bones) {
            if (bone.id >= boneList.length)
                throw new IllegalArgumentException("bone id out of range: 0..<" + boneList.length + ": " + bone.id);
            boneList[bone.id] = bone;
            if (bone.name != null)
                byName.put(bone.name, bone);
            if (bone.externalId != Integer.MIN_VALUE)
                byExternalId.put(bone.externalId, bone);
        }

        this.boneList = boneList;
        this.byName = byName.build();
        this.byExternalId = byExternalId.build();
    }

    public @NotNull @Unmodifiable Map<@NotNull String, @NotNull Bone> getBoneByName() {
        return byName;
    }

    public @NotNull Bone getById(int id) {
        return boneList[id];
    }

    public @Nullable Bone getByExternalId(int id) {
        return byExternalId.get(id);
    }

    public @Nullable Bone getByName(String name) {
        return byName.get(name);
    }

    public @NotNull Bone getRoot() {
        return root;
    }

    public boolean isBoneInThisTree(Bone bone) {
        return getById(bone.id) == bone;
    }

    public int boneCount() {
        return boneList.length;
    }

    public static class Bone {
        public final int id;
        public final int externalId;
        public final @Nullable String name;
        public final @NotNull Vec3f pos;
        public final @NotNull Vec4f rot;
        public final @NotNull Vec4f rotInverted;
        public final @NotNull Vec3f relativePos;
        public final @NotNull Vec4f relativeRot;
        final ImmutableList<Bone> children;

        private Bone(
                @NotNull Builder builder,
                Set<@NotNull Bone> allNodes,
                @Nullable Bone parent
        ) {
            Objects.requireNonNull(builder.pos, "pos of bone #" + builder.id);
            Objects.requireNonNull(builder.rot, "rot of bone #" + builder.id);
            this.id = builder.id;
            this.externalId = builder.externalId;
            this.name = builder.name;
            this.pos = builder.pos;
            this.rot = builder.rot;
            this.rotInverted = invert(rot);
            if (parent == null) {
                this.relativeRot = rot;
                this.relativePos = pos;
            } else {
                this.relativeRot = parent.asRelative(rot);
                this.relativePos = parent.asRelative(pos);
            }
            allNodes.add(this);
            ImmutableList.Builder<Bone> children = ImmutableList.builder();
            for (Builder child : builder.children) {
                children.add(new Bone(child, allNodes, this));
            }
            this.children = children.build();
        }

        /**
         * convert the global position to relative position to this bone.
         * @param global the global posiiton
         * @return relative position to this bone
         */
        public Vec3f asRelative(Vec3f global) {
            return asRelativeDirection(global.sub(pos));
        }

        /**
         * convert the global direction vector to relative direction vector to this bone.
         * @param global the global direction vector
         * @return direction vector to this bone
         */
        public Vec3f asRelativeDirection(Vec3f global) {
            return rotate(rotInverted, global);
        }

        /**
         * convert the global rotation quaternion to relative rotation quaternion to this bone.
         * @param global the global rotation quaternion
         * @return rotation quaternion to this bone
         */
        public Vec4f asRelative(Vec4f global) {
            return times(global, rotInverted);
        }
    }

    public static Builder builder(int id) {
        return new Builder(id);
    }

    public static class Builder {
        private final @NotNull List<@NotNull Builder> children;
        private final @NotNull Set<@NotNull Integer> ids = Sets.newHashSet();
        private @Nullable Builder parent;
        private @Nullable String name;
        private @Nullable Vec3f pos;
        private @Nullable Vec4f rot;
        private int externalId = Integer.MIN_VALUE;

        public final int id;

        private Builder(int id) {
            if (id < 0 || id > 255)
                throw new IllegalArgumentException("bone id out of range. id must be in 0..255: " + id);
            this.children = new ArrayList<>();
            this.id = id;
            ids.add(id);
            if (id == 0) {
                pos = Vec3f.ORIGIN;
                rot = Quot.ORIGIN;
            }
        }

        private Builder getRoot() {
            @NotNull Builder root = this;
            while (root.parent != null)
                root = root.parent;
            return root;
        }

        public Builder setPos(float x, float y, float z) {
            return setPos(new Vec3f(x, y, z));
        }

        public Builder setPos(@NotNull Vec3f pos) {
            Objects.requireNonNull(pos, "pos");
            if (this.id == 0) throw new IllegalStateException("root not cannot set position.");
            this.pos = pos;
            return this;
        }

        public Builder setRot(float heading, float pitch, float bank) {
            return setRot(Quot.fromOiler(heading, pitch, bank));
        }

        public Builder setRot(@NotNull Vec4f rot) {
            Objects.requireNonNull(rot, "rot");
            if (this.id == 0) throw new IllegalStateException("root not cannot set rotation.");
            this.rot = rot;
            return this;
        }

        public Builder setName(@NotNull String name) {
            Objects.requireNonNull(name, "name");
            this.name = name;
            return this;
        }

        public Builder setParent(@NotNull Builder parent) {
            Objects.requireNonNull(parent, "parent");
            if (id == 0) throw new IllegalStateException("id 0 must be root node.");
            Set<Integer> ids = this.getRoot().ids;
            Set<Integer> parentIds = parent.getRoot().ids;
            for (int parentId : parentIds) {
                if (ids.contains(parentId))
                    throw new IllegalArgumentException("id conflict " + parentIds);
            }
            this.parent = parent;
            parent.children.add(this);
            return this;
        }

        public Builder setExternalId(int externalId) {
            this.externalId = externalId;
            return this;
        }

        public BoneTree build() {
            if (id != 0) throw new IllegalStateException("root node must be node#0");
            return new BoneTree(this);
        }

        @Override
        public String toString() {
            return "BoneBuilder{#id=" + id + ", name=" + name + '}';
        }
    }

    @VisibleForTesting
    static class TestAccessor {
        public static Bone newBone(@NotNull Builder builder, Set<@NotNull Bone> allNodes, @Nullable Bone parent) {
            return new Bone(builder, allNodes, parent);
        }
    }
}
