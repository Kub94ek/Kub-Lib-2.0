package me.kub94ek.kubLib.items.custom

import de.tr7zw.nbtapi.NBT
import me.kub94ek.kubLib.items.Flags
import me.kub94ek.kubLib.items.Item
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

abstract class CustomItem(
    id: String,
    type: Material,
    name: Component?,
    lore: List<Component>? = null,
    model: String? = id,
    flags: Map<Flags, Any> = emptyMap()
): Item(id, ItemStack(type), flags) {
    init {
        createCustomItem(id, type, name, lore, model)
    }


    open fun onCustomItemInteract(event: CustomItemInteractEvent) {}

    private fun createCustomItem(id: String, type: Material, name: Component?,
                                        lore: List<Component>?, model: String? = id) {
        val itemMeta = this.itemStack.itemMeta
        if (name != null) itemMeta.itemName(name)
        if (model != null) itemMeta.itemModel = NamespacedKey.fromString(model)
        if (lore != null) itemMeta.lore(lore)
        itemStack.itemMeta = itemMeta

        NBT.modify(itemStack) { nbt ->
            nbt.getOrCreateCompound("kub_lib:data")
                .setString("item_id", id)
        }

    }

}