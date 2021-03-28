package com.anatawa12.mbBones.model.mqo

import com.anatawa12.mbBones.model.IFileLoader
import org.junit.jupiter.api.Test
import java.io.File
import java.io.InputStream

internal class MqoFileTest {
    @Test
    fun read() {
        val loader = SimpleFileLoader(File("anatawa12-simple-bone"))
        val mqo = MqoFile()
        mqo.read(loader, "test.mqo")
    }

    internal inner class SimpleFileLoader(val inFile: File) : IFileLoader {
        override fun getStream(name: String): InputStream = inFile.resolve(name).inputStream().buffered()

        override fun getStream(relativeFromFile: String, name: String): InputStream =
            inFile.resolve(relativeFromFile).resolveSibling(name).inputStream().buffered()
    }
}
