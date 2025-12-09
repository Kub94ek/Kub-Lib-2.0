package me.kub94ek.kubLib.cooldown

import me.kub94ek.kubLib.KubLib
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import java.util.UUID

class CooldownManager {
    private data class Cooldown(val defaultLength: Int?, val endMessage: Component)

    private val registered = mutableMapOf<String, Cooldown>()
    private val cooldowns = mutableMapOf<UUID, MutableMap<String, Int>>()


    fun registerCooldownType(id: String, endMessage: Component, defaultLength: Int? = null) {
        if (registered.containsKey(id)) {
            throw IllegalArgumentException("Cooldown with id $id is already registered")
        }
        if (defaultLength != null && defaultLength <= 0) {
            throw IllegalArgumentException("Default length must be positive or null")
        }
        if (registered.isEmpty()) {
            object : BukkitRunnable() {
                override fun run() {
                    val cooldownsForRemoval = mutableListOf<Pair<UUID, String>>()
                    for ((playerId, playerCooldowns) in cooldowns) {
                        for ((id, timeLeft) in playerCooldowns) {
                            if (timeLeft <= 0) {
                                val cooldown = registered[id] ?: continue
                                cooldownsForRemoval.add(Pair(playerId, id))

                                val player = Bukkit.getPlayer(playerId) ?: continue
                                player.sendMessage(cooldown.endMessage)
                            } else {
                                playerCooldowns[id] = timeLeft - 1
                            }
                        }
                    }
                    for ((playerId, id) in cooldownsForRemoval) {
                        cooldowns[playerId]?.remove(id)
                        if (cooldowns[playerId]?.isEmpty() == true) {
                            cooldowns.remove(playerId)
                        }
                    }

                }
            }.runTaskTimerAsynchronously(KubLib.getInstance(), 0, 20)
        }

        registered[id] = Cooldown(defaultLength, endMessage)
    }

    fun startCooldown(playerId: UUID, id: String, length: Int? = null) {
        val cooldown = registered[id] ?: return
        val cooldownLength = length ?: cooldown.defaultLength ?: return
        val playerCooldowns = cooldowns.getOrPut(playerId) { mutableMapOf() }
        playerCooldowns[id] = cooldownLength
    }

    fun isOnCooldown(playerId: UUID, id: String): Boolean {
        val playerCooldowns = cooldowns[playerId] ?: return false
        playerCooldowns[id] ?: return false
        return true
    }

    fun getRemainingCooldown(playerId: UUID, id: String): Int? {
        val playerCooldowns = cooldowns[playerId] ?: return null
        return playerCooldowns[id]
    }

    fun clearCooldown(playerId: UUID, id: String) {
        val playerCooldowns = cooldowns[playerId] ?: return
        playerCooldowns.remove(id)
        if (playerCooldowns.isEmpty()) {
            cooldowns.remove(playerId)
        }
    }

    fun setCooldown(playerId: UUID, id: String, length: Int) {
        val playerCooldowns = cooldowns.getOrPut(playerId) { mutableMapOf() }
        playerCooldowns[id] = length
    }

    fun clearAllCooldowns() {
        cooldowns.clear()
    }

    fun clearCooldowns(playerId: UUID) {
        cooldowns.remove(playerId)
    }

    fun getCooldowns(playerId: UUID): Map<String, Int>? {
        return cooldowns[playerId]?.toMap()
    }

    fun getCooldowns(): Map<UUID, Map<String, Int>> {
        return cooldowns
    }


}