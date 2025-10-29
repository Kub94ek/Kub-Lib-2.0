package me.kub94ek.kubLib.listeners

import me.kub94ek.kubLib.KubLib
import me.kub94ek.kubLib.items.custom.CustomItem
import me.kub94ek.kubLib.items.custom.CustomItemInteractEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

/**
 * Listener that handles interactions with custom items.
 *
 * @param kubLib The KubLib instance.
 */
class CustomItemListener(kubLib: KubLib) : Listener {
     private val itemRegistry = kubLib.itemRegistry

     @EventHandler
     fun onPlayerInteraction(event: PlayerInteractEvent) {
          val item = event.item ?: return
          val itemId = itemRegistry.getItemId(item) ?: return
          val customItem = itemRegistry.getItem(itemId) ?: return
          if (customItem !is CustomItem) return

          val customItemInteractEvent = CustomItemInteractEvent(customItem, itemId, event)
          customItem.onCustomItemInteract(customItemInteractEvent)
     }

}