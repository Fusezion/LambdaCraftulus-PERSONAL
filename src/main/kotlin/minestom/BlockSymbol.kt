package me.chriss99.minestom

import net.minestom.server.instance.block.Block

data class BlockSymbol(val namespace: String, val symbol: String, val prettySymbol: String = symbol)

val blockSymbols = listOf(
    BlockSymbol("minecraft:jigsaw", "!", ""),
    BlockSymbol("minecraft:sticky_piston", "("),
    BlockSymbol("minecraft:piston", ")"),
    BlockSymbol("minecraft:crafting_table", "\\", "λ"),
    BlockSymbol("minecraft:lodestone", "."),
    BlockSymbol("minecraft:red_concrete", "r"),
    BlockSymbol("minecraft:blue_concrete", "b"),
    BlockSymbol("minecraft:green_concrete", "g"),
    BlockSymbol("minecraft:cyan_concrete", "c"),
    BlockSymbol("minecraft:bookshelf", ":", ""),
)

private val blockToSymbolMap = blockSymbols.associate { it.namespace to it.symbol }
private val blockToPrettySymbolMap = blockSymbols.associate { it.namespace to it.prettySymbol }
private val symbolToBlockMap = blockSymbols.associate { it.symbol to it.namespace }

fun getParsableLambdaSymbol(block: Block): String {
    return blockToSymbolMap[block.namespace().toString()] ?: ""
}

fun getPrettyLambdaSymbol(block: Block): String {
    return blockToPrettySymbolMap[block.namespace().toString()] ?: ""
}

@SuppressWarnings
fun getBlockFromSymbol(symbol: String): Block? {
    return Block.fromNamespaceId(symbolToBlockMap[symbol] ?: "")
}