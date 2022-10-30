package com.github.m4kvn.spigotnms

import net.minecraft.core.BlockPosition
import net.minecraft.server.level.EntityPlayer
import net.minecraft.server.level.PlayerInteractManager
import net.minecraft.server.level.WorldServer
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.EntityItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.World
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.TileEntity
import net.minecraft.world.level.block.state.IBlockData
import org.bukkit.entity.Player

fun nms(): Lazy<Nms> = lazy { NmsImpl() }

internal val Player.position: BlockPosition get() = BlockPosition(location.x, location.y, location.z)

internal val EntityPlayer.playerInteractManager: PlayerInteractManager get() = d
internal val BlockPosition.x: Int get() = u()
internal val BlockPosition.y: Int get() = v()
internal val BlockPosition.z: Int get() = w()

internal val WorldServer.isClientSide: Boolean get() = y
internal val WorldServer.gameRules: GameRules get() = W()
internal val WorldServer.random: RandomSource get() = w
internal val RandomSource.nextFloat: Float get() = i()
internal val ItemStack.isEmpty: Boolean get() = b()

internal fun EntityItem.resetPickupDelay() = o()
internal fun PlayerInteractManager.breakBlock(blockPosition: BlockPosition) = a(blockPosition)
internal fun GameRules.getBoolean(gameRuleKey: GameRules.GameRuleKey<GameRules.GameRuleBoolean>) = b(gameRuleKey)
internal fun WorldServer.getBlockEntity(position: BlockPosition) = (this as World).getBlockEntity(position, true)

internal fun WorldServer.canDrop(itemStack: ItemStack): Boolean {
    if (isClientSide) return false
    if (itemStack.isEmpty) return false
    if (gameRules.getBoolean(GameRules.g).not()) return false
    return true
}

internal fun WorldServer.createEntityItem(
    dropPosition: BlockPosition,
    itemStack: ItemStack,
): EntityItem {
    val d0 = dropPosition.x + (random.nextFloat * 0.5f).toDouble() + 0.25
    val d1 = dropPosition.y + (random.nextFloat * 0.5f).toDouble() + 0.25
    val d2 = dropPosition.z + (random.nextFloat * 0.5f).toDouble() + 0.25
    val entityItem = EntityItem(this, d0, d1, d2, itemStack)
    entityItem.resetPickupDelay()
    return entityItem
}

internal val IBlockData.isRequiresSpecialTool: Boolean get() = t()

internal object NMSBlock {

    fun getDrops(
        iBlockData: IBlockData,
        world: WorldServer,
        position: BlockPosition,
        tileEntity: TileEntity?,
        entity: Entity? = null,
        itemStack: ItemStack = ItemStack.b,
    ): List<ItemStack> = Block.a(iBlockData, world, position, tileEntity, entity, itemStack)
}
