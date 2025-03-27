package me.chriss99.minestom

import net.minestom.server.instance.block.Block

data class BlockSymbol(val namespace: String, val symbol: String)

val blockSymbols = listOf(
    BlockSymbol("minecraft:diamond_block", "!"),
    BlockSymbol("minecraft:sticky_piston", "("),
    BlockSymbol("minecraft:piston", ")"),
    BlockSymbol("minecraft:sea_lantern", "\\"),
    BlockSymbol("minecraft:stone", "."),
    BlockSymbol("minecraft:red_wool", "a"),
    BlockSymbol("minecraft:green_wool", "b"),
    BlockSymbol("minecraft:blue_wool", "c"),
)

private val blockToSymbolMap = blockSymbols.associate { it.namespace to it.symbol }
private val symbolToBlockMap = blockSymbols.associate { it.symbol to it.namespace }

fun getParsableLambdaSymbol(block: Block): String {
    return blockToSymbolMap[block.namespace().toString()] ?: ""
}

fun getPrettyLambdaSymbol(block: Block): String {
    return getParsableLambdaSymbol(block).replace("\\", "λ")
}

@SuppressWarnings
fun getBlockFromSymbol(symbol: String): Block? {
    return Block.fromNamespaceId(symbolToBlockMap[symbol] ?: "")
}