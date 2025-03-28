package me.chriss99

import me.chriss99.minestom.lambdaDisplayLogic
import minestom.createVoidBiome
import minestom.initInstance
import minestom.onJoin
import net.minestom.server.MinecraftServer
import net.minestom.server.extras.MojangAuth

fun main() {
    val minecraftServer = MinecraftServer.init()
    val eventHandler = MinecraftServer.getGlobalEventHandler()
    createVoidBiome()
    val instanceContainer = initInstance()
    onJoin(eventHandler, instanceContainer)
    lambdaDisplayLogic(eventHandler)
    MojangAuth.init()
    minecraftServer.start("0.0.0.0", 25565)
}