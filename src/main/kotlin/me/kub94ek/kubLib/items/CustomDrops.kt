package me.kub94ek.kubLib.items

import org.bukkit.entity.EntityType
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack
import java.util.function.Predicate
import java.util.function.Supplier

/**
 * Represents custom drops for a specific mob type.
 *
 * @property mobType The [EntityType] of the mob this drop is associated with.
 */
class CustomDrops private constructor(val mobType: EntityType) {

    /** List of custom drops for the mob. */
    val drops: MutableList<CustomDrop> = mutableListOf()

    /** Predicate to determine if the drops should occur. */
    var shouldDrop: Predicate<EntityDeathEvent> = Predicate { true }

    /**
     * Represents a single custom drop.
     *
     * @property itemSupplier The Supplier of the item to drop.
     * @property itemId The ID of the item, if applicable.
     * @property chance The chance of the item dropping.
     * @property count The range of item counts to drop.
     */
    data class CustomDrop(
        val itemSupplier: Supplier<ItemStack>,
        val itemId: String?,
        val chance: Double,
        val count: IntRange
    )

    /**
     * Builder class for constructing [CustomDrops].
     *
     * @param mob The [EntityType] of the mob for which the drops are being defined.
     * @param itemRegistry The [ItemRegistry].
     */
    class Builder(mob: EntityType, private val itemRegistry: ItemRegistry) {

        private val customDrops = CustomDrops(mob)

        /**
         * Adds a custom drop from an [ItemStack].
         *
         * @param item The item to drop.
         * @param chance The chance of the item dropping.
         * @param count The range of item counts to drop.
         * @return The builder instance.
         */
        fun drop(item: ItemStack, chance: Double, count: IntRange): Builder {
            customDrops.drops.add(CustomDrop({ item.clone() }, null, chance, count))
            return this
        }

        /**
         * Adds a custom drop from an [ItemStack].
         *
         * @param itemSupplier The item to drop.
         * @param chance The chance of the item dropping.
         * @param count The range of item counts to drop.
         * @return The builder instance.
         */
        fun drop(itemSupplier: Supplier<ItemStack>, chance: Double, count: IntRange): Builder {
            customDrops.drops.add(CustomDrop(itemSupplier, null, chance, count))
            return this
        }

        /**
         * Adds a custom drop using an item ID.
         *
         * @param itemId The ID of the item to drop.
         * @param chance The chance of the item dropping.
         * @param count The range of item counts to drop.
         * @return The builder instance.
         * @throws IllegalArgumentException If the item ID is unknown.
         */
        fun drop(itemId: String, chance: Double, count: IntRange): Builder {
            val item = itemRegistry.getItem(itemId) ?: throw IllegalArgumentException("Unknown item id: $itemId")
            customDrops.drops.add(CustomDrop({ item.itemStack }, itemId, chance, count))
            return this
        }

        /**
         * Sets a predicate to determine if the drops should occur.
         *
         * @param shouldDrop The predicate to evaluate.
         * @return The builder instance.
         */
        fun dropPredicate(shouldDrop: Predicate<EntityDeathEvent>): Builder {
            customDrops.shouldDrop = shouldDrop
            return this
        }

        /**
         * Builds and returns the [CustomDrops] instance.
         *
         * @return The constructed [CustomDrops].
         */
        fun build(): CustomDrops {
            return customDrops
        }

    }
}