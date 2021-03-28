package com.anatawa12.mbBones.model;

import java.io.IOException;

public class ModelFormatException extends IOException {
    public ModelFormatException() {
    }

    public ModelFormatException(String message) {
        super(message);
    }

    public ModelFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModelFormatException(Throwable cause) {
        super(cause);
    }
}
