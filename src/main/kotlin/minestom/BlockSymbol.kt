package me.chriss99.minestom

import net.minestom.server.instance.block.Block
import net.minestom.server.instance.block.Block.*

data class BlockSymbol(val block: Block, val symbol: String, val prettySymbol: String = symbol)

val blockSymbols = listOf(
    BlockSymbol(JIGSAW.withProperty("orientation", "west_up"), "!", ""),
    BlockSymbol(STICKY_PISTON.withProperty("facing", "west"), "("),
    BlockSymbol(PISTON.withProperty("facing", "east"), ")"),
    BlockSymbol(CRAFTING_TABLE, "\\", "λ"),
    BlockSymbol(LODESTONE, "."),
    BlockSymbol(RED_CONCRETE, "r"),
    BlockSymbol(BLUE_CONCRETE, "b"),
    BlockSymbol(GREEN_CONCRETE, "g"),
    BlockSymbol(CYAN_CONCRETE, "c"),
    BlockSymbol(BOOKSHELF, ":", ""),
)

private val blockToSymbolMap = blockSymbols.associate { it.block to it.symbol }
private val blockToPrettySymbolMap = blockSymbols.associate { it.block to it.prettySymbol }
private val symbolToBlockMap = blockSymbols.associate { it.symbol to it.block }

fun getParsableLambdaSymbol(block: Block): String {
    return blockToSymbolMap[block] ?: ""
}

fun getPrettyLambdaSymbol(block: Block): String {
    return blockToPrettySymbolMap[block] ?: ""
}

@SuppressWarnings
fun getBlockFromSymbol(symbol: String): Block? {
    return symbolToBlockMap[symbol]
}