package me.chriss99.minestom

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.timer.TaskSchedule

fun createBasePlate(player: Player, instanceContainer: InstanceContainer) {
    val baseplate = Entity(EntityType.TEXT_DISPLAY)
    val meta = baseplate.entityMeta as TextDisplayMeta
    meta.isHasNoGravity = true
    meta.text = Component.text("█").color(TextColor.color(44, 47, 50))
    meta.scale = Vec(20000.0,20000.0,1.0)
    meta.translation = Vec(0.0, -1200.0, 0.0)
    meta.posRotInterpolationDuration = 1
    meta.viewRange = 1024f
    baseplate.setInstance(instanceContainer, player.position.withYaw(180f))
    MinecraftServer.getSchedulerManager().scheduleTask({
        if (!player.isOnline) {
            baseplate.remove()
            return@scheduleTask
        }
        baseplate.teleport(player.position.withYaw(180f).withPitch(0f).withZ(14.99))
    }, TaskSchedule.tick(1), TaskSchedule.tick(1))
}