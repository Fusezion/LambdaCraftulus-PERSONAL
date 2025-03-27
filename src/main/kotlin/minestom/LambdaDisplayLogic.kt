package me.chriss99.minestom

import me.chriss99.lambda.lazyReduce
import me.chriss99.parse.lex
import me.chriss99.parse.parse
import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.tag.Tag
import java.util.*

fun lambdaDisplayLogic(eventHandler: GlobalEventHandler) {
    eventHandler.addListener(PlayerBlockPlaceEvent::class.java) { event ->
        event.isCancelled = true
        event.blockFace
        val instance = event.instance
        val clicked = event.blockPosition.sub(event.blockFace.toDirection().vec())
        if (instance.getBlock(clicked) != Block.BLACK_STAINED_GLASS) {
            return@addListener
        }
        setLambdaBlock(event.block, clicked, instance)
    }

    eventHandler.addListener(PlayerBlockBreakEvent::class.java) { event ->
        event.isCancelled = true
        val instance = event.instance
        instance.getEntityByUuid(event.block.getTag(Tag.UUID("link")))?.remove()

        instance.setBlock(event.blockPosition, Block.BLACK_STAINED_GLASS)
    }

    eventHandler.addListener(PlayerBlockInteractEvent::class.java) { event ->
        event.isCancelled = true
        val instance = event.instance
        var target = event.blockPosition
        if (event.block.name() == "minecraft:diamond_block") {

            var expression = ""
            var i = 0
            while (instance.getBlock(target) != Block.BLACK_STAINED_GLASS) {
                i++
                if (i > 100) {
                    return@addListener
                }
                target = target.add(1, 0, 0)
                expression += getParsableLambdaSymbol(instance.getBlock(target))
            }
            println(expression)
            val reduced = lazyReduce(parse(lex(expression)))
            event.player.sendMessage(reduced.toString())
        }
    }
}

fun setLambdaBlock(block: Block, clicked: Point, instance: Instance) {

    val symbol = createLambdaSymbol(block, clicked, instance)
    val lambdaBlock = getLambdaBlock(block)
        .withTag(
            Tag.UUID("link"),
            symbol
        )

    instance.setBlock(clicked, lambdaBlock)
}

fun createLambdaSymbol(block: Block, clicked: Point, instance: Instance): UUID {
    val display = Entity(EntityType.TEXT_DISPLAY)
    val meta = display.entityMeta as TextDisplayMeta
    meta.text = Component.text(getPrettyLambdaSymbol(block))
    meta.scale = Vec(7.0,7.0,7.0)
    meta.isShadow = true
    meta.backgroundColor = 0x0000000
    meta.isHasNoGravity = true

    display.setInstance(instance, clicked.add(1.0,1.0,0.0))
    display.setView(-180f, 0f)
    return display.uuid
}

fun getLambdaBlock(block: Block): Block {
    return when (block) {
        Block.STICKY_PISTON -> Block.STICKY_PISTON.withProperty("facing", "east")
        Block.PISTON -> Block.PISTON.withProperty("facing", "west")
        else -> block
    }
}
