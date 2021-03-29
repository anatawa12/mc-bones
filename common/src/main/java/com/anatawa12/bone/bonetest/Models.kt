package com.anatawa12.bone.bonetest

import com.anatawa12.mbBones.BoneTree
import com.anatawa12.mbBones.BonedMultiObjectModel
import com.anatawa12.mbBones.BonedObject
import com.anatawa12.mbBones.math.Vec2f
import com.anatawa12.mbBones.math.Vec3f
import com.anatawa12.mbBones.model.IFileLoader
import com.anatawa12.mbBones.model.mqo.MqoFileReader
import java.io.File
import java.io.InputStream

object Models {
    @Suppress("unused")
    fun buildHardCodedModel(): BonedMultiObjectModel {
        val boneTreeBuilder = BoneTree.builder(0)
        BoneTree.builder(1).apply {
            setPos(0.0f, 1.5f, 1.0f)
            setRot(0f, 0f, 0f)
            BoneTree.builder(2).apply {
                setPos(0.0f, 1.5f, 2.0f)
                setRot(0f, 0f, 0f)
                BoneTree.builder(3).apply {
                    setPos(0.0f, 1.5f, 3.0f)
                    setRot(0f, 0f, 0f)
                    BoneTree.builder(4).apply {
                        setPos(0.0f, 1.5f, 4.0f)
                        setRot(0f, 0f, 0f)
                    }.setParent(this)
                }.setParent(this)
            }.setParent(this)
        }.setParent(boneTreeBuilder)
        val boneTree = boneTreeBuilder.build()

        val bonedObject = BonedObject.builder(boneTree).apply {
            setName("obj1")

            val p0 = addPoint(boneTree.getById(0), Vec3f(0.0f, 1.5f, 0.0f), Vec3f(0f, 1f, 0f), Vec2f(0.00f, 0.0f))
            val p1 = addPoint(boneTree.getById(0), Vec3f(1.0f, 1.5f, 0.0f), Vec3f(0f, 1f, 0f), Vec2f(0.00f, 1.0f))

            val p2 = addPoint(boneTree.getById(1), Vec3f(0.0f, 1.5f, 1.0f), Vec3f(0f, 1f, 0f), Vec2f(0.25f, 0.0f))
            val p3 = addPoint(boneTree.getById(1), Vec3f(1.0f, 1.5f, 1.0f), Vec3f(0f, 1f, 0f), Vec2f(0.25f, 1.0f))

            val p4 = addPoint(boneTree.getById(2), Vec3f(0.0f, 1.5f, 2.0f), Vec3f(0f, 1f, 0f), Vec2f(0.50f, 0.0f))
            val p5 = addPoint(boneTree.getById(2), Vec3f(1.0f, 1.5f, 2.0f), Vec3f(0f, 1f, 0f), Vec2f(0.50f, 1.0f))

            val p6 = addPoint(boneTree.getById(3), Vec3f(0.0f, 1.5f, 3.0f), Vec3f(0f, 1f, 0f), Vec2f(0.75f, 0.0f))
            val p7 = addPoint(boneTree.getById(3), Vec3f(1.0f, 1.5f, 3.0f), Vec3f(0f, 1f, 0f), Vec2f(0.75f, 1.0f))

            val p8 = addPoint(boneTree.getById(4), Vec3f(0.0f, 1.5f, 4.0f), Vec3f(0f, 1f, 0f), Vec2f(1.00f, 0.0f))
            val p9 = addPoint(boneTree.getById(4), Vec3f(1.0f, 1.5f, 4.0f), Vec3f(0f, 1f, 0f), Vec2f(1.00f, 1.0f))

            addTriangle(p2, p1, p0)
            addTriangle(p2, p3, p1)

            addTriangle(p4, p3, p2)
            addTriangle(p4, p5, p3)

            addTriangle(p6, p5, p4)
            addTriangle(p6, p7, p5)

            addTriangle(p8, p7, p6)
            addTriangle(p8, p9, p7)
        }.build()

        return BonedMultiObjectModel.builder(boneTree).addObject(bonedObject).build()
    }

    fun mqoModel(): BonedMultiObjectModel {
        class SimpleFileLoader(val inFile: File) : IFileLoader {
            override fun getStream(name: String): InputStream = inFile.resolve(name).inputStream().buffered()

            override fun getStream(relativeFromFile: String, name: String): InputStream =
                inFile.resolve(relativeFromFile).resolveSibling(name).inputStream().buffered()
        }
        val loader = SimpleFileLoader(File("../../anatawa12-simple-bone"))
        return MqoFileReader.INSTANCE.read(loader, "test.mqo")
    }
}
