package me.kub94ek.kubLib.gui

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * Represents a menu item in a custom GUI.
 *
 * @property item The ItemStack representing the menu item.
 * @property onClick The function to be called when the item is clicked.
 */
data class MenuItem(val item: ItemStack, val onClick: (ClickEvent) -> Unit) {
    /**
     * Represents a click event in the menu.
     *
     * @property menuId The ID of the menu where the click event occurred.
     * @property event The InventoryClickEvent associated with the click.
     */
    data class ClickEvent(val menuId: String, val event: InventoryClickEvent)
}