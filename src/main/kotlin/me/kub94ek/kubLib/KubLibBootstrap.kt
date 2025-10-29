@file:Suppress("UnstableApiUsage")

package me.kub94ek.kubLib

import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import java.io.File
import java.io.IOException
import java.net.URISyntaxException

class KubLibBootstrap : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        val manager = context.lifecycleManager
        manager.registerEventHandler(LifecycleEvents.DATAPACK_DISCOVERY) {
            val registrar = it.registrar()

            try {
                val folder = File(".kub_lib/datapacks/")
                if (!folder.exists()) {
                    return@registerEventHandler
                }

                for (file in folder.listFiles() ?: return@registerEventHandler) {
                    if (file.isDirectory) {
                        registrar.discoverPack(file.toURI(), file.name.lowercase())
                    }
                }
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}