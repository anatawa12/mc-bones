package com.anatawa12.mbBones.model.mqo;

import com.anatawa12.mbBones.BoneTree;
import com.anatawa12.mbBones.BonedMultiObjectModel;
import com.anatawa12.mbBones.model.IFileLoader;
import com.anatawa12.mbBones.model.IMultiFileModelLoader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MqoFileReader implements IMultiFileModelLoader {
    public static MqoFileReader INSTANCE = new MqoFileReader();

    private MqoFileReader() {
    }

    @Override
    public @NotNull BonedMultiObjectModel read(@NotNull IFileLoader loader, @NotNull String fileName) throws IOException {
        MqoFile file = new MqoFile();
        file.read(loader, fileName);
        BoneTree tree = file.buildTree();
        return file.buildModel(tree);
    }
}
