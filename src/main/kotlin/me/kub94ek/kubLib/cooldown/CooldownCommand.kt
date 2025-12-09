package me.kub94ek.kubLib.cooldown

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.kub94ek.kubLib.KubLib
import org.bukkit.Bukkit
import java.util.UUID

class CooldownCommand(private val permissionBase: String) {
    private val cooldownManager = KubLib.getInstance().cooldownManager

    fun createCommand(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("cooldown")
            .then(
                Commands.literal("reset")
                    .requires { it.sender.hasPermission("$permissionBase.cooldown.reset") }
                    .executes { ctx ->
                        cooldownManager.clearAllCooldowns()
                        ctx.source.sender.sendMessage("Cooldowns have been reset.")
                        1
                    }
                    .then(
                        Commands.argument("player", ArgumentTypes.player())
                            .executes { ctx ->
                                val player = ctx.getArgument("player", PlayerSelectorArgumentResolver::class.java)
                                    .resolve(ctx.source).first()
                                executeReset(ctx, player.uniqueId)
                                1
                            }
                            .then(
                                Commands.argument("cooldown_id", StringArgumentType.word())
                                    .executes { ctx ->
                                        val player =
                                            ctx.getArgument("player", PlayerSelectorArgumentResolver::class.java)
                                                .resolve(ctx.source).first()
                                        val cooldownId = StringArgumentType.getString(ctx, "cooldown_id")
                                        executeReset(ctx, player.uniqueId, cooldownId)
                                        1
                                    }
                            )
                    )
                    .then(
                        Commands.argument("uuid", StringArgumentType.string())
                            .executes { ctx ->
                                val uuidString = StringArgumentType.getString(ctx, "uuid")
                                val uuid = try {
                                    UUID.fromString(uuidString)
                                } catch (e: IllegalArgumentException) {
                                    ctx.source.sender.sendMessage("Invalid UUID format.")
                                    return@executes 1
                                }
                                executeReset(ctx, uuid)
                                1
                            }
                            .then(
                                Commands.argument("cooldown_id", StringArgumentType.word())
                                    .executes { ctx ->
                                        val uuidString = StringArgumentType.getString(ctx, "uuid")
                                        val uuid = try {
                                            UUID.fromString(uuidString)
                                        } catch (e: IllegalArgumentException) {
                                            ctx.source.sender.sendMessage("Invalid UUID format.")
                                            return@executes 1
                                        }
                                        val cooldownId = StringArgumentType.getString(ctx, "cooldown_id")
                                        executeReset(ctx, uuid, cooldownId)
                                        1
                                    }
                            )
                    )
            )
            .then(
                Commands.literal("set")
                    .requires { it.sender.hasPermission("$permissionBase.cooldown.set") }
                    .then(
                        Commands.argument("player", ArgumentTypes.player())
                            .then(
                                Commands.argument("cooldown_id", StringArgumentType.word())
                                    .then(
                                        Commands.argument("seconds", IntegerArgumentType.integer(0))
                                            .executes { ctx ->
                                                val player = ctx.getArgument("player", PlayerSelectorArgumentResolver::class.java)
                                                    .resolve(ctx.source).first()
                                                val cooldownId = StringArgumentType.getString(ctx, "cooldown_id")
                                                val seconds = IntegerArgumentType.getInteger(ctx, "seconds")

                                                cooldownManager.setCooldown(player.uniqueId, cooldownId, seconds)

                                                ctx.source.sender.sendMessage("Set cooldown \"$cooldownId\" for player ${player.name} to $seconds seconds.")
                                                1
                                            }
                                    )
                            )
                    )
                    .then(
                        Commands.argument("uuid", StringArgumentType.string())
                            .then(
                                Commands.argument("cooldown_id", StringArgumentType.word())
                                    .then(
                                        Commands.argument("seconds", IntegerArgumentType.integer(0))
                                            .executes { ctx ->
                                                val uuidString = StringArgumentType.getString(ctx, "uuid")
                                                val uuid = try {
                                                    UUID.fromString(uuidString)
                                                } catch (e: IllegalArgumentException) {
                                                    ctx.source.sender.sendMessage("Invalid UUID format.")
                                                    return@executes 1
                                                }
                                                val cooldownId = StringArgumentType.getString(ctx, "cooldown_id")
                                                val seconds = IntegerArgumentType.getInteger(ctx, "seconds")

                                                cooldownManager.setCooldown(uuid, cooldownId, seconds)

                                                ctx.source.sender.sendMessage("Set cooldown \"$cooldownId\" for player ${Bukkit.getPlayer(uuid)?.name ?: "unknown player (uuid: ${uuid})"} to $seconds seconds.")
                                                1
                                            }
                                    )
                            )
                    )
            )
            .then(
                Commands.literal("get")
                    .requires { it.sender.hasPermission("$permissionBase.cooldown.get") }
                    .executes { ctx ->
                        ctx.source.sender.sendMessage("${cooldownManager.getCooldowns()}")

                        1
                    }
                    .then(
                        Commands.argument("player", ArgumentTypes.player())
                            .executes { ctx ->
                                val player = ctx.getArgument("player", PlayerSelectorArgumentResolver::class.java)
                                    .resolve(ctx.source).first()
                                ctx.source.sender.sendMessage("Player ${player.name} cooldowns: ${cooldownManager.getCooldowns(player.uniqueId) ?: "No cooldowns"}")

                                1
                            }
                            .then(
                                Commands.argument("cooldown_id", StringArgumentType.word())
                                    .executes { ctx ->
                                        val player =
                                            ctx.getArgument("player", PlayerSelectorArgumentResolver::class.java)
                                                .resolve(ctx.source).first()
                                        val cooldownId = StringArgumentType.getString(ctx, "cooldown_id")
                                        ctx.source.sender.sendMessage(
                                            "Player ${player.name} " +
                                                    "cooldown with id $cooldownId: " +
                                                    "${cooldownManager.getRemainingCooldown(player.uniqueId, cooldownId) ?: "None"}"
                                        )
                                        1
                                    }
                            )
                    )
                    .then(
                        Commands.argument("uuid", StringArgumentType.string())
                            .executes { ctx ->
                                val uuidString = StringArgumentType.getString(ctx, "uuid")
                                val uuid = try {
                                    UUID.fromString(uuidString)
                                } catch (e: IllegalArgumentException) {
                                    ctx.source.sender.sendMessage("Invalid UUID format.")
                                    return@executes 1
                                }

                                ctx.source.sender.sendMessage("Player ${Bukkit.getPlayer(uuid)?.name ?: "unknown player (uuid: ${uuid})"} cooldowns: ${cooldownManager.getCooldowns(uuid) ?: "No cooldowns"}")

                                1
                            }
                            .then(
                                Commands.argument("cooldown_id", StringArgumentType.word())
                                    .executes { ctx ->
                                        val uuidString = StringArgumentType.getString(ctx, "uuid")
                                        val uuid = try {
                                            UUID.fromString(uuidString)
                                        } catch (e: IllegalArgumentException) {
                                            ctx.source.sender.sendMessage("Invalid UUID format.")
                                            return@executes 1
                                        }
                                        val cooldownId = StringArgumentType.getString(ctx, "cooldown_id")
                                        ctx.source.sender.sendMessage(
                                            "Player ${Bukkit.getPlayer(uuid)?.name ?: "unknown player (uuid: ${uuid})"} " +
                                                    "cooldown with id $cooldownId: " +
                                                    "${cooldownManager.getRemainingCooldown(uuid, cooldownId) ?: "None"}"
                                        )
                                        1
                                    }
                            )
                    )
            )
    }

    private fun executeReset(ctx: CommandContext<CommandSourceStack>, uuid: UUID) {
        cooldownManager.clearCooldowns(uuid)
        ctx.source.sender.sendMessage("Cooldowns for player ${Bukkit.getPlayer(uuid)?.name ?: "unknown player (uuid: ${uuid})"} have been reset.")
    }

    private fun executeReset(ctx: CommandContext<CommandSourceStack>, uuid: UUID, cooldownId: String) {
        val cooldowns = cooldownManager.getRemainingCooldown(uuid, cooldownId)
        if (cooldowns == null) {
            ctx.source.sender.sendMessage("Player ${Bukkit.getPlayer(uuid)?.name ?: "unknown player (uuid: ${uuid})"} does not have a cooldown with id \"$cooldownId\".")
            return
        }
        cooldownManager.clearCooldown(uuid, cooldownId)
        ctx.source.sender.sendMessage("Cooldown \"$cooldownId\" for player ${Bukkit.getPlayer(uuid)?.name ?: "unknown player (uuid: ${uuid})"} has been reset.")
    }

}