package com.anatawa12.mbBones;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;

public class BonedMultiObjectModel {
    private final ImmutableMap<String, BonedObject> map;
    public final @NotNull BoneTree boneTree;

    public BonedMultiObjectModel(Builder builder) {
        this.map = builder.builder.build();
        this.boneTree = builder.tree;
    }

    public static Builder builder(BoneTree tree) {
        return new Builder(tree);
    }

    public BonedObject getByName(String name) {
        return map.get(name);
    }

    public ImmutableCollection<BonedObject> getObjects() {
        return map.values();
    }

    public static class Builder {
        public final BoneTree tree;
        private final ImmutableMap.Builder<String, BonedObject> builder = ImmutableMap.builder();

        private Builder(BoneTree tree) {
            this.tree = tree;
        }

        public Builder addObject(BonedObject obj) {
            builder.put(obj.name, obj);
            return this;
        }

        public BonedMultiObjectModel build() {
            return new BonedMultiObjectModel(this);
        }
    }
}
