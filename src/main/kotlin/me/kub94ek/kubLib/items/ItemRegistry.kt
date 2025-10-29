package me.kub94ek.kubLib.items

import de.tr7zw.nbtapi.NBT
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import java.util.EnumMap
import java.util.function.Predicate

class ItemRegistry {

    private val items: HashMap<String, Item> = hashMapOf()

    val customDrops: EnumMap<EntityType, MutableList<CustomDrops>> = EnumMap(EntityType::class.java)


    fun registerItem(
        id: String,
        type: Material, name: Component,
        lore: List<Component> = listOf(),
        model: String = id,
        flags: Map<Flags, Any> = emptyMap()) {
        registerItem(id, createItem(id, type, name, lore, model), flags)
    }

    fun registerItem(id: String, item: ItemStack, flags: Map<Flags, Any> = emptyMap()) {
        registerItem(Item(id, item, flags))
    }

    fun registerItem(item: Item, id: String = item.id) {
        if (item.id != id) throw IllegalArgumentException("Item ID mismatch: expected ${item.id}, got $id")
        items[id] = item
    }

    /**
     * Registers custom drops for a specific entity type.
     * @param mob The entity type.
     * @param itemId The ID of the item to drop.
     * @param shouldDrop A predicate to determine if the item should drop.
     */
    fun registerCustomDrops(mob: EntityType, itemId: String, shouldDrop: Predicate<EntityDeathEvent>) {
        registerCustomDrops(
            CustomDrops.Builder(mob, this)
                .drop(itemId, 1.0, 1..1)
                .dropPredicate(shouldDrop)
                .build()
        )
    }

    /**
     * Registers custom drops for a specific entity type.
     * @param mob The entity type.
     * @param item The [ItemStack] to drop.
     * @param shouldDrop A predicate to determine if the item should drop.
     */
    fun registerCustomDrops(mob: EntityType, item: ItemStack, shouldDrop: Predicate<EntityDeathEvent>) {
        registerCustomDrops(
            CustomDrops.Builder(mob, this)
                .drop(item, 1.0, 1..1)
                .dropPredicate(shouldDrop)
                .build()
        )
    }

    /**
     * Registers the specified custom drops.
     * @param drops The CustomDrops to register.
     */
    fun registerCustomDrops(drops: CustomDrops) {
        val mobCustomDrops = customDrops[drops.mobType]
        if (mobCustomDrops != null) {
            mobCustomDrops.add(drops)
            customDrops[drops.mobType] = mobCustomDrops
        } else {
            customDrops[drops.mobType] = mutableListOf(drops)
        }
    }


    fun getItemStack(id: String, clone: Boolean = true): ItemStack? {
        if (!clone) {
            return items[id]?.itemStack
        }
        return items[id]?.itemStack?.clone()
    }

    fun getItem(id: String): Item? {
        return items[id]
    }

    fun hasItemId(item: ItemStack): Boolean {
        var hasId: Boolean = false
        NBT.get(item) {
            hasId = it.getCompound("kub_lib:data")?.hasTag("item_id") ?: false
        }
        return hasId
    }

    fun getItemId(item: ItemStack): String? {
        var id: String? = null
        NBT.get(item) {
            id = it.getCompound("kub_lib:data")?.getString("item_id")
        }
        return id
    }


    fun getItemIds(namespace: String? = null): List<String> {
        if (namespace == null) return items.keys.toList()
        return items.keys.filter { it.startsWith("$namespace:") }
    }

    fun getItems(namespace: String? = null): List<Item> {
        if (namespace == null) return items.values.toList()
        return items.values.filter { it.id.startsWith("$namespace:") }
    }

    fun isUsableInCrafting(item: ItemStack): Boolean {
        val id = getItemId(item) ?: return true
        return isUsableInCrafting(id)
    }

    fun isUsableInCrafting(id: String?): Boolean {
        if (id == null) return true
        return items[id]?.flags?.containsKey(Flags.CRAFTING_USABLE) ?: false
    }

    fun createItem(
        id: String,
        type: Material, name: Component,
        lore: List<Component> = listOf(),
        model: String = id
    ): ItemStack {
        val item = ItemStack(type)
        val meta = item.itemMeta!!
        meta.itemName(name)
        meta.itemModel = NamespacedKey.fromString(model)
        if (lore.isNotEmpty()) {
            meta.lore(lore)
        }
        item.itemMeta = meta

        NBT.modify(item) {
            it.getOrCreateCompound("kub_lib:data").setString("item_id", id)
        }

        return item

    }

}