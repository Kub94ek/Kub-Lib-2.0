package me.kub94ek.kubLib.items

import org.bukkit.inventory.ItemStack

open class Item(val id: String, open var itemStack: ItemStack, val flags: Map<Flags, Any> = emptyMap())
