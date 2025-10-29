package me.kub94ek.kubLib.items.custom

import org.bukkit.event.player.PlayerInteractEvent

/**
 * This event is called when a player interacts with a custom item.
 *
 * @param item The custom item that was interacted with.
 * @param itemId The ID of the custom item.
 * @param interactEvent The original [PlayerInteractEvent] that triggered this event.
 */
data class CustomItemInteractEvent(
    val item: CustomItem,
    val itemId: String,
    val interactEvent: PlayerInteractEvent
)