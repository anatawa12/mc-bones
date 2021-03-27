package com.anatawa12.mbBones;

import com.anatawa12.mbBones.math.Quot;
import com.anatawa12.mbBones.math.Vec3f;
import com.anatawa12.mbBones.math.Vec4f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.anatawa12.mbBones.math.Quot.rotate;
import static com.anatawa12.mbBones.math.Quot.times;

public class BoneTreeState {
    public final @NotNull BoneTree target;
    private @NotNull Bone @NotNull[] bones;

    public BoneTreeState(@NotNull BoneTree target) {
        this.target = target;
        this.bones = new Bone[target.boneCount()];
        for (int i = 0; i < this.bones.length; i++) {
            bones[i] = new Bone();
        }
    }

    public Bone getBone(int id) {
        return bones[id];
    }

    public @NotNull ComputedBone @NotNull[] compute() {
        ComputedBone[] computed = new ComputedBone[bones.length];
        compute(target.getRoot(), computed, null);
        return computed;
    }

    private void compute(BoneTree.Bone bone, ComputedBone[] computed, @Nullable ComputedBone parent) {
        Bone stat = getBone(bone.id);
        ComputedBone newComputed;
        if (parent != null) {
            newComputed= new ComputedBone(
                    parent.asGlobal(bone.relativePos.add(rotate(bone.rot, stat.pos))),
                    parent.asGlobal(times(bone.relativeRot, stat.rot))
            );
        } else {
            newComputed= new ComputedBone(
                    bone.relativePos.add(rotate(bone.rot, stat.pos)),
                    times(bone.relativeRot, stat.rot)
            );
        }
        computed[bone.id] = newComputed;
        for (BoneTree.Bone child : bone.children) {
            compute(child, computed, newComputed);
        }
    }

    public static class Bone {
        private @NotNull Vec3f pos = Vec3f.ORIGIN;
        private @NotNull Vec4f rot = Quot.ORIGIN;

        private Bone() {
        }

        public @NotNull Vec3f getPos() {
            return pos;
        }

        public void setPos(@NotNull Vec3f pos) {
            this.pos = Objects.requireNonNull(pos, "pos");
        }

        public @NotNull Vec4f getRot() {
            return rot;
        }

        public void setRot(@NotNull Vec4f rot) {
            this.rot = Objects.requireNonNull(rot, "rot");
        }
    }

    static class ComputedBone {
        public final @NotNull Vec3f pos;
        public final @NotNull Vec4f rot;

        private ComputedBone(@NotNull Vec3f pos, @NotNull Vec4f rot) {
            this.pos = pos;
            this.rot = rot;
        }

        /**
         * convert the relative position to global position to this bone.
         * @param relative the relative positon
         * @return relative position to this bone
         */
        public Vec3f asGlobal(Vec3f relative) {
            return asGlobalDirection(relative).add(pos);
        }

        /**
         * convert the relative direction vector to global direction vector to this bone.
         * @param relative the relative direction vector
         * @return direction vector to this bone
         */
        public Vec3f asGlobalDirection(Vec3f relative) {
            return rotate(rot, relative);
        }

        /**
         * convert the relative rotation quaternion to global rotation quaternion to this bone.
         * @param relative the relative rotation quaternion
         * @return rotation quaternion to this bone
         */
        public Vec4f asGlobal(Vec4f relative) {
            return times(rot, relative);
        }
    }
}
