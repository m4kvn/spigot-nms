package com.github.m4kvn.spigotnms

import net.minecraft.core.BlockPosition
import net.minecraft.world.entity.EntityExperienceOrb
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.ItemStack

internal class NmsImpl : Nms {

    override fun breakBlock(player: Player, block: Block) {
        val craftPlayer = player as CraftPlayer
        val blockPosition = BlockPosition(block.x, block.y, block.z)
        craftPlayer.handle.playerInteractManager.breakBlock(blockPosition)
    }

    override fun breakLeaves(player: Player, block: Block) {
        val craftBlock = block as CraftBlock
        val worldServer = craftBlock.craftWorld.handle
        val iBlockData = craftBlock.nms
        if (craftBlock.type != Material.AIR && (!iBlockData.isRequiresSpecialTool)) {
            val drops = NMSBlock.getDrops(
                iBlockData = iBlockData,
                world = craftBlock.craftWorld.handle,
                position = player.position,
                tileEntity = worldServer.getBlockEntity(craftBlock.position),
            )
            drops
                .filter { worldServer.canDrop(it) }
                .forEach { itemStack ->
                    val entityItem = worldServer.createEntityItem(player.position, itemStack)
                    if (worldServer.captureDrops != null) {
                        worldServer.captureDrops.add(entityItem)
                    } else {
                        worldServer.addFreshEntity(entityItem, CreatureSpawnEvent.SpawnReason.DEFAULT)
                    }
                }
        }
        craftBlock.setType(Material.AIR, true)
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
        val craftItemStack = CraftItemStack.asNMSCopy(itemStack)
        val world = (player.world as CraftWorld).handle
        if (world.canDrop(craftItemStack).not()) return
        val entityItem = world.createEntityItem(player.position, craftItemStack)
        world.addFreshEntity(entityItem, CreatureSpawnEvent.SpawnReason.DEFAULT)
    }
}