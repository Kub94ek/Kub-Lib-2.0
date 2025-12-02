package me.kub94ek.kubLib.gui

import de.tr7zw.nbtapi.NBT
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * Manages the registration and interaction of menus.
 */
class MenuRegistry {

    /** Maps menu IDs to their corresponding [Menu] instances. */
    private val menus: HashMap<String, Menu> = hashMapOf()

    /**
     * Retrieves the menu ID from an [ItemStack].
     *
     * @param item The item to retrieve the menu ID from.
     * @return The menu ID, or `null` if not found.
     */
    fun getMenuId(item: ItemStack): String? {
        var id: String? = null
        if (item.type.isAir || item.amount <= 0) return null
        NBT.get(item) {
            id = it.resolveCompound("kub_lib:data.menu")?.getString("id")
        }
        return id
    }

    /**
     * Retrieves the character representing a menu item from an [ItemStack].
     *
     * @param item The item to retrieve the character from.
     * @return The character, or `null` if not found.
     */
    fun getItemChar(item: ItemStack): String? {
        var itemChar: String? = null
        NBT.get(item) {
            itemChar = it.resolveCompound("kub_lib:data.menu")?.getString("item_char")
        }
        return itemChar
    }

    /**
     * Registers a menu.
     *
     * @param menu The menu to register.
     */
    fun registerMenu(menu: Menu) {
        menus[menu.id] = menu
    }

    /**
     * Opens a menu for a player.
     *
     * @param id The ID of the menu to open.
     * @param player The player to open the menu for.
     */
    fun openMenu(id: String, player: Player) {
        val menu = menus[id] ?: return
        menu.open(player)
    }

    /**
     * Handles click events for a menu.
     *
     * @param menuId The ID of the menu.
     * @param itemChar The character representing the clicked item.
     * @param event The inventory click event.
     */
    fun callClickEvent(menuId: String, itemChar: Char, event: InventoryClickEvent) {
        val menu = menus[menuId] ?: return
        menu.callClickEvent(itemChar, event)
    }
}