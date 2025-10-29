package me.kub94ek.kubLib.gui

import de.tr7zw.nbtapi.NBT
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

/**
 * Represents a chest gui menu with customizable contents and behavior.
 *
 * @property title The title of the chest menu.
 * @property id The unique ID of the menu.
 * @property contents The layout of the menu, defined as rows of characters.
 */
open class ChestMenu(private val title: Component, override val id: String, private vararg val contents: String) : Menu {
    init {
        // Validate that at least one row is provided and each row has exactly 9 items.
        require(contents.isNotEmpty()) { "At least one row of contents has to be specified" }
        for (row in contents) {
            require(row.length == 9) { "Each row has to have 9 items" }
        }
    }

    /** Maps characters to menu items. */
    val keyMap: HashMap<Char, MenuItem> = hashMapOf()

    /** Maps characters to functions that prepare [ItemStack]s for players. */
    val preparedItems: HashMap<Char, (Player) -> ItemStack> = hashMapOf()

    /**
     * Associates a character with a menu item.
     *
     * @param char The character to associate.
     * @param item The menu item to associate with the character.
     */
    fun setKey(char: Char, item: MenuItem) {
        keyMap[char] = item
    }

    /**
     * Associates a character with a menu item using a pair.
     *
     * @param pair The pair of character and menu item.
     */
    fun setKey(pair: Pair<Char, MenuItem>) {
        keyMap[pair.first] = pair.second
    }

    /**
     * Prepares a character with a function that generates an [ItemStack] for the player.
     *
     * @param char The character to prepare.
     * @param item A function that takes a [Player] and returns an [ItemStack].
     */
    fun prepareKey(char: Char, default: MenuItem, item: (Player) -> ItemStack) {
        keyMap[char] = default
        preparedItems[char] = item
    }

    /**
     * Opens the menu for a player.
     *
     * @param player The player to open the menu for.
     */
    override fun open(player: Player) {
        val inv = Bukkit.createInventory(player, contents.size * 9, title)

        // Flatten the contents into a list of characters.
        val invContents: MutableList<Char> = contents.flatMap { it.toList() }.toMutableList()

        // Fill the inventory with items based on the character mapping.
        for (i in 0..< invContents.size) {
            inv.setItem(i, createMenuItemStack(invContents[i], player))
        }

        player.openInventory(inv)
    }

    /**
     * Handles click events on the menu.
     *
     * @param itemChar The character representing the clicked item.
     * @param event The inventory click event.
     */
    override fun callClickEvent(itemChar: Char, event: InventoryClickEvent) {
        val menuItem = keyMap[itemChar] ?: return
        menuItem.onClick(MenuItem.ClickEvent(id, event))
    }

    /**
     * Creates an [ItemStack] for a menu item based on a character.
     *
     * @param char The character representing the menu item.
     * @return The created [ItemStack].
     */
    private fun createMenuItemStack(char: Char, player: Player): ItemStack {
        val menuItemStack = preparedItems[char]?.invoke(player) ?: keyMap[char]?.item ?: return emptyItem()
        val itemMeta = menuItemStack.itemMeta
        val dataContainer = itemMeta.persistentDataContainer
        menuItemStack.itemMeta = itemMeta

        NBT.modify(menuItemStack) {
            val menuCompound = it.resolveOrCreateCompound("kub_lib:data.menu")
            menuCompound.setString("id", id)
            menuCompound.setString("item_char", "$char")
        }

        return menuItemStack
    }

    /**
     * Creates an empty item to fill unused slots in the menu.
     *
     * @return The created empty [ItemStack].
     */
    private fun emptyItem(): ItemStack {
        val item = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
        val itemMeta = item.itemMeta
        itemMeta.itemName(Component.text(" "))
        item.itemMeta = itemMeta

        NBT.modify(item) {
            it.resolveOrCreateCompound("kub_lib:data.menu").setString("id", id)
        }

        return item
    }
}