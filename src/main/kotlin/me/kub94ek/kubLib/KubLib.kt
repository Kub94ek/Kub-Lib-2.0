package me.kub94ek.kubLib

import de.tr7zw.nbtapi.NBT
import me.kub94ek.kubLib.cooldown.CooldownManager
import me.kub94ek.kubLib.gui.MenuRegistry
import me.kub94ek.kubLib.items.ItemRegistry
import me.kub94ek.kubLib.listeners.CraftingListener
import me.kub94ek.kubLib.listeners.CustomDropsListener
import me.kub94ek.kubLib.listeners.CustomItemListener
import me.kub94ek.kubLib.listeners.MenuListener
import me.kub94ek.kubLib.recipes.RecipeRegistry
import org.bukkit.plugin.java.JavaPlugin

class KubLib : JavaPlugin() {

    /** Registry for custom items and drops */
    val itemRegistry = ItemRegistry()

    /** Registry for custom recipes */
    val recipeRegistry = RecipeRegistry(this)

    /** Registry for custom menus */
    val menuRegistry = MenuRegistry()

    /** Manager for handling cooldowns */
    val cooldownManager = CooldownManager()

    override fun onEnable() {
        kubLib = this

        val pluginManager = server.pluginManager
        pluginManager.registerEvents(CraftingListener(this), this)
        pluginManager.registerEvents(CustomDropsListener(this), this)
        pluginManager.registerEvents(CustomItemListener(this), this)
        pluginManager.registerEvents(MenuListener(this), this)

        if (!NBT.preloadApi()) {
            logger.warning("NBT-API wasn't initialized properly, disabling the plugin")
            server.pluginManager.disablePlugin(this)
            return
        }
    }


    companion object {
        private var kubLib: KubLib? = null

        /**
         * Retrieves the instance of the KubLib plugin.
         * @return The instance of the KubLib plugin.
         * @throws IllegalStateException if the plugin is not loaded.
         */
        fun getInstance(): KubLib {
            if (kubLib != null) {
                return kubLib!!
            }
            throw IllegalStateException("Plugin isn't loaded!")
        }
    }
}
