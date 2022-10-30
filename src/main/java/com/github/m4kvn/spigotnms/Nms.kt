package com.github.m4kvn.spigotnms

import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface Nms {
    fun breakBlock(player: Player, block: Block)
    fun breakLeaves(player: Player, block: Block)
    fun dropItemStack(player: Player, itemStack: ItemStack)
    fun dropExperience(player: Player, amount: Int)
}