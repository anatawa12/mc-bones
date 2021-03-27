package com.anatawa12.bone.bonetest

import com.anatawa12.bone.bonetest.BoneTest.MOD_ID
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

object TestBlock : BlockContainer(Material.IRON) {
    init {
        setRegistryName(ResourceLocation(MOD_ID, "test"))
    }
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity = TestTile()

    override fun isOpaqueCube(state: IBlockState): Boolean = false
    override fun isNormalCube(state: IBlockState, world: IBlockAccess, pos: BlockPos): Boolean = false
}
