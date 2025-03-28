package me.chriss99.minestom

import me.chriss99.lambda.lazyReduce
import me.chriss99.parse.lex
import me.chriss99.parse.parse
import minestom.BASE_BLOCK
import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.event.GlobalEventHandler
import net.minestom.server.event.player.PlayerHandAnimationEvent
import net.minestom.server.event.player.PlayerUseItemEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag
import java.util.*

fun lambdaDisplayLogic(eventHandler: GlobalEventHandler) {
    eventHandler.addListener(PlayerUseItemEvent::class.java) { event ->
        event.isCancelled = true
        val blockPosition = event.player.getTargetBlockPosition(100)
        if (blockPosition != null) {
            val instance = event.instance
            val item = event.player.itemInMainHand
            val block = instance.getBlock(blockPosition)
            when (block) {
                BASE_BLOCK -> setLambdaBlock(item, blockPosition, instance)
                else -> event.player.sendMessage(parseBlocks(blockPosition, instance))
            }
        }
    }
    eventHandler.addListener(PlayerHandAnimationEvent::class.java) { event ->
        event.isCancelled = true
        val blockPosition = event.player.getTargetBlockPosition(100)
        if (blockPosition != null) {
            val instance = event.instance
            val block = instance.getBlock(blockPosition)
            val tag = block.getTag(Tag.UUID("link"))
            if (tag != null) {
                instance.getEntityByUuid(tag)?.remove()
                instance.setBlock(blockPosition, BASE_BLOCK)
            }
        }
    }
}

fun parseBlocks(start: Point, instance: Instance): String {
    var expression = ""
    var i = 0
    var target = start
    while (i < 10) {
        target = target.add(-1.0, 0.0, 0.0)
        when (instance.getBlock(target)) {
            BASE_BLOCK -> {
                i++
                continue
            }
            Block.DIAMOND_BLOCK -> return "Hit another block"
            else -> i = 0
        }
        expression += getParsableLambdaSymbol(instance.getBlock(target))
    }
    val reduced = lazyReduce(parse(lex(expression)))
    return reduced.toString()
}

fun setLambdaBlock(item: ItemStack, clicked: Point, instance: Instance) {
    val block = getLambdaBlock(item)
    val symbol = createLambdaSymbol(block, clicked, instance)
    val lambdaBlock = block
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
    meta.scale = Vec(7.0,7.0,0.01)
    meta.isShadow = true
    meta.backgroundColor = 0x0000000
    meta.isHasNoGravity = true
    meta.isSeeThrough = true
    display.setInstance(instance, clicked.add(1.0,1.0,-0.01))
    display.setView(-180f, 0f)
    return display.uuid
}

fun getLambdaBlock(block: ItemStack): Block {
    return when (block) {
        Block.STICKY_PISTON -> Block.STICKY_PISTON.withProperty("facing", "west")
        Block.PISTON -> Block.PISTON.withProperty("facing", "east")
        else -> Block.fromNamespaceId(block.material().namespace()) ?: Block.RED_CONCRETE
    }
}
