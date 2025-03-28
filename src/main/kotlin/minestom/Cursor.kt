package me.chriss99.minestom

import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.Player
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.timer.TaskSchedule

fun createCursor(player: Player, instanceContainer: InstanceContainer) {
    val cursor = Entity(EntityType.TEXT_DISPLAY)
    var meta = cursor.entityMeta as TextDisplayMeta
    meta = createSquareDisplay(meta)
    meta.isHasNoGravity = true
    meta.isSeeThrough = true
    meta.textOpacity = 70
    meta.posRotInterpolationDuration = 1
    meta.viewRange = 1024f
    meta.text = Component.text("█")

    cursor.setInstance(instanceContainer, player.position)
    MinecraftServer.getSchedulerManager().scheduleTask({
        if (!player.isOnline) {
            cursor.remove()
            return@scheduleTask
        }
        val target = player.getTargetBlockPosition(100) ?: Pos(0.0,0.0,0.0)
        cursor.teleport(Pos(target).withYaw(180f).withPitch(0f))
    }, TaskSchedule.tick(1), TaskSchedule.tick(1))
}

fun createSquareDisplay(meta: TextDisplayMeta, size: Double = 1.0): TextDisplayMeta {
    meta.backgroundColor = 0x0
    meta.scale = Vec(size * 5,size * 5,0.0)
    meta.translation = Vec(size / -1.78, size / -8, 0.0)
    return meta
}