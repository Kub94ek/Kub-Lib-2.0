package me.kub94ek.kubLib.items.effects

import me.kub94ek.kubLib.KubLib
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.PlayerInventory

abstract class SlotEffect(private val itemId: String, override val periodInTicks: Long = 5, private val slot: EquipmentSlot) : ItemEffect {
    private val itemRegistry = KubLib.getInstance().itemRegistry
    override fun shouldApply(inventory: PlayerInventory): Boolean {
        val item = inventory.getItem(slot)
        if (item.isEmpty) return false
        return itemRegistry.getItemId(item) == itemId
    }
}