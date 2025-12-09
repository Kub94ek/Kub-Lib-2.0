package me.kub94ek.kubLib.items.effects

import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory

interface ItemEffect {
    val periodInTicks: Long
    fun applyEffect(player: Player)
    fun shouldApply(inventory: PlayerInventory): Boolean
}