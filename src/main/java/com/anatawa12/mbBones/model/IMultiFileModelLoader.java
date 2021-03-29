package com.anatawa12.mbBones.model;

import com.anatawa12.mbBones.BonedMultiObjectModel;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public interface IMultiFileModelLoader {
    @NotNull BonedMultiObjectModel read(@NotNull IFileLoader loader, @NotNull String fileName) throws IOException;
}
