package me.kub94ek.kubLib.gui

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

/**
 * Interface representing a menu in the GUI.
 * Each menu has a unique ID and can be opened for a player.
 *
 * @property id The unique identifier for the menu.
 */
interface Menu {
    val id: String
    /**
     * Opens the menu for the specified player.
     *
     * @param player The player for whom the menu will be opened.
     */
    fun open(player: Player)
    /**
     * Handles the click event for a specific item in the menu.
     *
     * @param itemChar The character representing the item in the menu.
     * @param event The inventory click event.
     */
    fun callClickEvent(itemChar: Char, event: InventoryClickEvent)
}