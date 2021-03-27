package com.anatawa12.bone.bonetest

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry

@Mod(modid = BoneTest.MOD_ID, name = BoneTest.MOD_NAME, version = BoneTest.VERSION)
object BoneTest {
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent?) {
        MinecraftForge.EVENT_BUS.register(this)
        GameRegistry.registerTileEntity(TestTile::class.java, ResourceLocation(MOD_ID, "test"))
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
    }

    @SubscribeEvent
    fun addItems(event: RegistryEvent.Register<Item>) {
        event.registry.register(ItemBlock(TestBlock).setRegistryName(TestBlock.registryName))
    }

    @SubscribeEvent
    fun addBlocks(event: RegistryEvent.Register<Block>) {
        event.registry.register(TestBlock)
        //Renderer
    }

    @SubscribeEvent
    fun addModels(event: ModelRegistryEvent) {
        ClientRegistry.bindTileEntitySpecialRenderer(TestTile::class.java, Renderer)
    }

    @Mod.InstanceFactory
    @JvmStatic
    fun factory() = this

    const val MOD_ID = "bone-test"
    const val MOD_NAME = "Bone Test"
    const val VERSION = "1.0-SNAPSHOT"
}
