@file:Suppress("UnstableApiUsage")

package me.kub94ek.kubLib

import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap

class KubLibBootstrap : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        /*val manager = context.lifecycleManager
        manager.registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler {
            event ->
            event.registry().register(EnchantmentKeys.create(Key.key("kub_lib", "test")),) {
                it.description(Component.text("Test"))
                    .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.SWORDS))
            }
        })*/
    }
}