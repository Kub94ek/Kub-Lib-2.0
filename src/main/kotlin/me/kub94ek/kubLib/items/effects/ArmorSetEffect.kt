package me.kub94ek.kubLib.items.effects

import me.kub94ek.kubLib.KubLib
import org.bukkit.inventory.PlayerInventory

abstract class ArmorSetEffect(
    private val bootsId: String, private val leggingsId: String,
    private val chestplateId: String, private val helmetId: String,
    override val periodInTicks: Long = 5
) : ItemEffect {
    private val itemRegistry = KubLib.getInstance().itemRegistry

    override fun shouldApply(inventory: PlayerInventory): Boolean {
        val armor = inventory.armorContents
        if (armor.contains(null)) return false
        if (armor[0]!!.isEmpty || armor[1]!!.isEmpty || armor[2]!!.isEmpty || armor[3]!!.isEmpty) return false
        if (itemRegistry.getItemId(armor[0]!!) != bootsId || itemRegistry.getItemId(armor[1]!!) != leggingsId
            || itemRegistry.getItemId(armor[2]!!) != chestplateId || itemRegistry.getItemId(armor[3]!!) != helmetId) return false
        return true
    }
}