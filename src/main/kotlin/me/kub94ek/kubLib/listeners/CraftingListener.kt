package me.kub94ek.kubLib.listeners

import me.kub94ek.kubLib.KubLib
import io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Crafter
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.CrafterCraftEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.event.inventory.PrepareSmithingEvent
import org.bukkit.event.inventory.SmithItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.StonecutterInventory


private val EMPTY = ItemStack(Material.AIR)

/**
 * Listener class for handling crafting-related events.
 * This class ensures that custom items are used correctly in crafting, smithing, and other inventory interactions.
 *
 * @param kubLib The main plugin instance.
 */
class CraftingListener(kubLib: KubLib) : Listener {
    private val itemRegistry = kubLib.itemRegistry
    private val recipeRegistry = kubLib.recipeRegistry

    /**
     * Handles the preparation of crafting items.
     * If a custom item is used in crafting, it checks if the resulting item is valid.
     */
    @EventHandler
    fun onPreCraft(event: PrepareItemCraftEvent) {
        val inv = event.inventory
        val result = inv.result ?: return

        if (shouldCancelCraft(inv.matrix, result)) {
            inv.result = EMPTY
        }
    }

    /**
     * Handles the crafting of items.
     * If a custom item is used in crafting, it checks if the resulting item is valid.
     */
    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        val inv = event.inventory
        val result = inv.result ?: return

        if (shouldCancelCraft(inv.matrix, result)) {
            inv.result = EMPTY
            event.isCancelled = true
        }
    }

    /**
     * Handles the crafting of items by a crafter.
     * If a custom item is used in crafting, it checks if the resulting item is valid.
     */
    @EventHandler
    fun onCrafterCraft(event: CrafterCraftEvent) {
        val result = event.result

        val inv = (event.block.state as? Crafter)?.inventory ?: return
        /*println(result)
        println(inv.contents)*/

        if (shouldCancelCraft(inv.contents, result)) {
            event.result = EMPTY
            event.isCancelled = true
        }
    }

    /**
     * Handles the preparation of smithing items.
     * If a custom item is used in smithing, it checks if the resulting item is valid.
     */
    @EventHandler
    fun onPreSmithing(event: PrepareSmithingEvent) {
        val inv = event.inventory
        val result = inv.result ?: return

        if (shouldCancelCraft(inv.storageContents, result)) {
            inv.result = EMPTY
        }
    }

    /**
     * Handles the smithing of items.
     * If a custom item is used in smithing, it checks if the resulting item is valid.
     */
    @EventHandler(ignoreCancelled = true)
    fun onSmith(event: SmithItemEvent) {
        val whoClicked = event.whoClicked
        if (whoClicked !is Player) return

        val inv = event.inventory
        val result = inv.result ?: return

        if (shouldCancelCraft(inv.storageContents, result)) {
            val materialId = itemRegistry.getItemId(inv.inputMineral ?: return)
            val templateId = itemRegistry.getItemId(inv.inputTemplate ?: return)

            if (materialId == null && templateId == null && Tag.ITEMS_TRIMMABLE_ARMOR.isTagged(result.type)) {
                return
            }

            event.result = Event.Result.DENY
            event.whoClicked.sendMessage(Component.translatable(
                "kub_lib.messages.crafting.smithing_table.denied",
                "You can't do this action.",
                NamedTextColor.RED
            ))
        }
    }

    /**
     * Handles inventory click events.
     * If a custom item is used in an anvil, a grindstone or a stonecutter, it checks if the resulting item is valid.
     */
    @EventHandler(ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked
        if (player !is Player) return

        if (event.rawSlot != 2) return

        val inv = event.clickedInventory ?: return

        if (inv.type == InventoryType.ANVIL) {
            val item1 = inv.contents[0] ?: return
            val item2 = inv.contents[1] ?: return

            if ((itemRegistry.hasItemId(item1) && !itemRegistry.hasItemId(item2) && item2.type != Material.ENCHANTED_BOOK)
                || (!itemRegistry.hasItemId(item1) && itemRegistry.hasItemId(item2))) {
                event.result = Event.Result.DENY
                player.sendMessage(
                    Component.translatable(
                        "kub_lib.messages.crafting.anvil.denied",
                        "Operation denied! Hover for more info",
                        NamedTextColor.RED
                    ).hoverEvent(
                        Component.translatable(
                            "kub_lib.messages.crafting.anvil.denied.hover",
                            "Custom items can only be combined with other custom items " +
                                    "or enchanted books, not regular vanilla items.",
                            NamedTextColor.RED
                        )
                    )
                )
            }

        } else if (inv.type == InventoryType.GRINDSTONE) {
            val item1 = inv.contents[0] ?: return
            val item2 = inv.contents[1] ?: return

            if ((itemRegistry.hasItemId(item1) && !itemRegistry.hasItemId(item2))
                || (!itemRegistry.hasItemId(item1) && itemRegistry.hasItemId(item2))) {
                event.result = Event.Result.DENY
                player.sendMessage(
                    Component.translatable(
                        "kub_lib.messages.crafting.grindstone.denied",
                        "You can't combine custom items with vanilla items.",
                        NamedTextColor.RED
                    )
                )
            }
        } else if (inv.type == InventoryType.STONECUTTER) {
            val inv = inv as StonecutterInventory

            val input = inv.inputItem ?: return
            val result = inv.result ?: return

            if (!itemRegistry.hasItemId(input)) {
                return
            }

            if (shouldCancelCraft(arrayOf(input), result)) {
                event.isCancelled = true
                inv.result = EMPTY
            }
        }
    }

    /**
     * Handles the selection of stonecutter recipes.
     * If a custom item is used in a stonecutter, it checks if the resulting item is valid.
     */
    @EventHandler
    fun onStonecutterRecipeSelection(event: PlayerStonecutterRecipeSelectEvent) {
        val inv = event.stonecutterInventory
        val input = inv.inputItem ?: return
        val result = inv.result ?: return

        if (!itemRegistry.hasItemId(input)) {
            return
        }

        if (shouldCancelCraft(arrayOf(input), result)) {
            event.isCancelled = true
            inv.result = EMPTY
        }

    }

    /**
     * Checks if the crafting should be canceled based on the items and the result.
     * @param items The items used in crafting.
     * @param result The resulting item.
     * @return `true` if the crafting should be canceled, `false` otherwise.
     */
    private fun shouldCancelCraft(items: Array<ItemStack?>, result: ItemStack): Boolean {
        if (!itemRegistry.hasItemId(result)) {
            for (item in items) {
                if (item != null) {
                    val itemId = itemRegistry.getItemId(item) ?: continue
                    if (itemRegistry.isUsableInCrafting(itemId)) continue
                    val usedIn = recipeRegistry.usedIn[itemId] ?: return true
                    if (!usedIn.contains("minecraft:${result.type.translationKey()}")) return true
                }
            }
        } else {
            for (item in items) {
                if (item == null) continue
                val itemId = itemRegistry.getItemId(item) ?: continue
                if (itemRegistry.isUsableInCrafting(itemId)) continue

                val usedIn = recipeRegistry.usedIn[itemId] ?: return true
                if (!usedIn.contains(itemRegistry.getItemId(result))) return true
            }
        }

        return false
    }
}