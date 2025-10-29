package me.kub94ek.kubLib.listeners

import me.kub94ek.kubLib.KubLib
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

/**
 * Listener that handles custom drops for entities.
 *
 * @param kubLib The [KubLib] instance.
 */
class CustomDropsListener(kubLib: KubLib) : Listener {
    private val itemRegistry = kubLib.itemRegistry

    @EventHandler
    fun onEntityDeath(e: EntityDeathEvent) {
        if (itemRegistry.customDrops.isEmpty()) return

        val customDropsList = itemRegistry.customDrops[e.entityType] ?: return

        for (drops in customDropsList) {
            if (drops.shouldDrop.test(e)) {
                for (drop in drops.drops) {
                    if (Math.random() < drop.chance) {
                        e.drops.add(drop.itemSupplier.get().asQuantity(drop.count.random()))
                    }
                }
            }
        }

    }

}