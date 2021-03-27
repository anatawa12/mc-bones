package com.anatawa12.bone.bonetest

import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

object TestBlock : BlockContainer(Material.iron) {
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity = TestTile()

    override fun isOpaqueCube(): Boolean = false
    override fun isNormalCube(): Boolean = false
}
