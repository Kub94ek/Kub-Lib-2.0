package me.kub94ek.kubLib.listeners

import me.kub94ek.kubLib.KubLib
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

/**
 * Listener for inventory click events in custom GUI menus.
 *
 * @param kubLib The instance of [KubLib]
 */
class MenuListener(kubLib: KubLib) : Listener {
    private val menuRegistry = kubLib.menuRegistry

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val item = event.currentItem ?: return
        val menuId = menuRegistry.getMenuId(item) ?: return
        event.isCancelled = true
        val itemChar = menuRegistry.getItemChar(item) ?: return
        if (itemChar.isEmpty()) return
        menuRegistry.callClickEvent(menuId, itemChar[0], event)
    }

}