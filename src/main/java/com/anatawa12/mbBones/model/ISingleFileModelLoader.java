package com.anatawa12.mbBones.model;

import com.anatawa12.mbBones.BonedMultiObjectModel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public interface ISingleFileModelLoader extends IMultiFileModelLoader {
    @NotNull BonedMultiObjectModel read(InputStream file) throws IOException;

    default @NotNull BonedMultiObjectModel read(@NotNull IFileLoader loader, @NotNull String fileName) throws IOException {
        return read(loader.getStream(fileName));
    }
}
