package com.github.m4kvn.spigotnms

import net.minecraft.core.BlockPosition
import net.minecraft.server.level.EntityPlayer
import net.minecraft.server.level.PlayerInteractManager
import net.minecraft.server.level.WorldServer
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.EntityExperienceOrb
import net.minecraft.world.entity.item.EntityItem
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.GameRules.g as DO_ITEM_DROPS
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.ItemStack
import net.minecraft.world.item.ItemStack as NmsItemStack

interface Nms {
    fun breakBlock(player: Player, block: Block)
    fun dropItemStack(player: Player, itemStack: ItemStack)
    fun dropExperience(player: Player, amount: Int)
}

fun nms(): Lazy<Nms> = lazy { NmsImpl() }

private class NmsImpl : Nms {

    override fun breakBlock(player: Player, block: Block) {
        val craftPlayer = player as CraftPlayer
        val blockPosition = BlockPosition(block.x, block.y, block.z)
        craftPlayer.handle.playerInteractManager.breakBlock(blockPosition)
    }

    override fun dropExperience(player: Player, amount: Int) {
        val position = player.position
        val world = (player.world as CraftWorld).handle
        val expEntity = EntityExperienceOrb(
            world,
            position.x + 0.5,
            position.y + 0.5,
            position.z + 0.5,
            amount,
        )
        world.addFreshEntity(expEntity, CreatureSpawnEvent.SpawnReason.DEFAULT)
    }

    override fun dropItemStack(player: Player, itemStack: ItemStack) {
        val craftItem = CraftItemStack.asNMSCopy(itemStack)
        val world = (player.world as CraftWorld).handle
        if (world.isClientSide) return
        if (craftItem.isEmpty) return
        if (world.gameRules.getBoolean(DO_ITEM_DROPS).not()) return
        val entityItem = createEntityItem(world, player.position, craftItem)
        world.addFreshEntity(entityItem, CreatureSpawnEvent.SpawnReason.DEFAULT)
    }

    private fun createEntityItem(
        world: WorldServer,
        dropPosition: BlockPosition,
        itemStack: net.minecraft.world.item.ItemStack,
    ): EntityItem {
        val d0 = (world.random.nextFloat * 0.5f).toDouble() + 0.25
        val d1 = (world.random.nextFloat * 0.5f).toDouble() + 0.25
        val d2 = (world.random.nextFloat * 0.5f).toDouble() + 0.25
        val entityItem = EntityItem(
            world,
            dropPosition.x + d0,
            dropPosition.y + d1,
            dropPosition.z + d2,
            itemStack,
        )
        entityItem.resetPickupDelay()
        return entityItem
    }

    val Player.position: BlockPosition get() = BlockPosition(location.x, location.y, location.z)

    val EntityPlayer.playerInteractManager: PlayerInteractManager get() = d
    val BlockPosition.x: Int get() = u()
    val BlockPosition.y: Int get() = v()
    val BlockPosition.z: Int get() = w()

    val WorldServer.isClientSide: Boolean get() = y
    val WorldServer.gameRules: GameRules get() = W()
    val WorldServer.random: RandomSource get() = w
    val RandomSource.nextFloat: Float get() = i()
    val NmsItemStack.isEmpty: Boolean get() = b()

    fun EntityItem.resetPickupDelay() = o()
    fun PlayerInteractManager.breakBlock(blockPosition: BlockPosition) = a(blockPosition)
    fun GameRules.getBoolean(gameRuleKey: GameRules.GameRuleKey<GameRules.GameRuleBoolean>) = b(gameRuleKey)
}
