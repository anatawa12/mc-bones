package com.anatawa12.mbBones.model;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

public interface IFileLoader {
    @NotNull InputStream getStream(@NotNull String name);
    @NotNull InputStream getStream(@NotNull String relativeFromFile, @NotNull String name);
}
