package me.chriss99.minestom

import me.chriss99.lambda.lazyReduce
import me.chriss99.minestom.LambdaParser.parseBlocks
import me.chriss99.minestom.LambdaSymbolManager.createErrorSymbol
import me.chriss99.parse.ParsingException
import me.chriss99.parse.lex
import me.chriss99.parse.parse
import minestom.BASE_BLOCK
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.color.Color
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
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import net.minestom.server.timer.TaskSchedule
import java.util.*

class LambdaEventHandler(private val eventHandler: GlobalEventHandler, private val blockManager: LambdaBlockManager) {
    fun registerEvents() {
        eventHandler.addListener(PlayerUseItemEvent::class.java) { event ->
            event.isCancelled = true
            val blockPosition = event.player.getTargetBlockPosition(100) ?: return@addListener
            val instance = event.instance
            val item = event.player.itemInMainHand
            val block = instance.getBlock(blockPosition)

            if (block == BASE_BLOCK) {
                blockManager.setLambdaBlock(item, blockPosition, instance)
            } else {
                event.player.sendMessage(parseBlocks(blockPosition, instance))
            }
        }

        eventHandler.addListener(PlayerHandAnimationEvent::class.java) { event ->
            event.isCancelled = true
            val blockPosition = event.player.getTargetBlockPosition(100) ?: return@addListener
            val instance = event.instance
            val block = instance.getBlock(blockPosition)
            val tag = block.getTag(Tag.UUID("link")) ?: return@addListener

            instance.getEntityByUuid(tag)?.remove()
            instance.setBlock(blockPosition, BASE_BLOCK)
        }
    }
}

object LambdaParser {
    fun parseBlocks(start: Point, instance: Instance): String {
        var expression = ""
        var target = start
        repeat(10) {
            target = target.add(-1.0, 0.0, 0.0)
            when (instance.getBlock(target)) {
                BASE_BLOCK -> return@repeat
                Block.DIAMOND_BLOCK -> return "Hit another block"
                else -> expression += getParsableLambdaSymbol(instance.getBlock(target))
            }
        }
        return try {
            val parsed = parse(lex(expression))
            lazyReduce(parsed).toString()
        } catch (e: ParsingException) {
            e.toString()
        }
    }
}

class LambdaBlockManager {
    fun setLambdaBlock(item: ItemStack, clicked: Point, instance: Instance) {
        val block = getLambdaBlock(item)
        val symbol = LambdaSymbolManager.createLambdaSymbol(block, clicked, instance)
        val lambdaBlock = block.withTag(Tag.UUID("link"), symbol)
        instance.setBlock(clicked, lambdaBlock)
    }

    private fun getLambdaBlock(block: ItemStack): Block {
        return when (block.material()) {
            Material.STICKY_PISTON -> Block.STICKY_PISTON.withProperty("facing", "west")
            Material.PISTON -> Block.PISTON.withProperty("facing", "east")
            else -> Block.fromNamespaceId(block.material().namespace()) ?: Block.RED_CONCRETE
        }
    }
}

object LambdaSymbolManager {
    fun createLambdaSymbol(block: Block, clicked: Point, instance: Instance): UUID {
        val (meta, display) = createSymbol()
        meta.text = Component.text(getPrettyLambdaSymbol(block))
        display.setInstance(instance, clicked.add(0.65, 1.0, -0.01))
        display.setView(-180f, 0f)
        return display.uuid
    }

    fun createErrorSymbol(symbol: String, point: Point, instance: Instance): UUID {
        val (meta, display) = createSymbol()
        meta.text = Component.text(symbol)
        display.setInstance(instance, point.add(0.65, 1.0, -0.01))
        display.setView(-180f, 0f)

        MinecraftServer.getSchedulerManager().buildTask {
            display.remove()
        }.delay(TaskSchedule.seconds(5)).schedule()

        return display.uuid
    }

    private fun createSymbol(): Pair<TextDisplayMeta, Entity> {
        val display = Entity(EntityType.TEXT_DISPLAY)
        val meta = display.entityMeta as TextDisplayMeta
        meta.scale = Vec(6.0, 6.0, 0.01)
        meta.isShadow = true
        meta.backgroundColor = 0x00000000
        meta.isHasNoGravity = true
        meta.isSeeThrough = true
        return Pair(meta, display)
    }
}