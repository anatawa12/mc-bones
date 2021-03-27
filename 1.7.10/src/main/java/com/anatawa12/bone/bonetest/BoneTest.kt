package com.anatawa12.bone.bonetest

import cpw.mods.fml.client.registry.ClientRegistry
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLInitializationEvent
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraftforge.common.MinecraftForge

@Mod(modid = BoneTest.MOD_ID, name = BoneTest.MOD_NAME, version = BoneTest.VERSION)
object BoneTest {
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent?) {
        MinecraftForge.EVENT_BUS.register(this)
        GameRegistry.registerTileEntity(TestTile::class.java, "$MOD_ID:test")
        GameRegistry.registerBlock(TestBlock, "test")
        ClientRegistry.bindTileEntitySpecialRenderer(TestTile::class.java, Renderer)
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
    }

    @Mod.InstanceFactory
    @JvmStatic
    fun factory() = this

    const val MOD_ID = "bone-test"
    const val MOD_NAME = "Bone Test"
    const val VERSION = "1.0-SNAPSHOT"
}
