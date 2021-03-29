package com.anatawa12.mbBones.model.mqo;

import com.anatawa12.mbBones.BoneTree;
import com.anatawa12.mbBones.BonedMultiObjectModel;
import com.anatawa12.mbBones.model.IFileLoader;

import java.io.IOException;

public class MqoFileReader {
    public static MqoFileReader INSTANCE = new MqoFileReader();

    private MqoFileReader() {
    }

    public BonedMultiObjectModel read(IFileLoader loader, String fileName) throws IOException {
        MqoFile file = new MqoFile();
        file.read(loader, fileName);
        BoneTree tree = file.buildTree();
        return file.buildModel(tree);
    }
}
