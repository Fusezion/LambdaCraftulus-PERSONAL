package minestom

import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.BlockVec
import net.minestom.server.instance.InstanceContainer
import net.minestom.server.instance.Weather
import net.minestom.server.instance.block.Block
import net.minestom.server.particle.Particle
import net.minestom.server.registry.DynamicRegistry
import net.minestom.server.world.DimensionType
import net.minestom.server.world.biome.Biome
import net.minestom.server.world.biome.BiomeEffects
import net.minestom.server.world.biome.BiomeParticle

val BASE_BLOCK: Block = Block.GRAY_CONCRETE

fun initInstance(): InstanceContainer {
     val dimension = DimensionType.builder()
        .height(400)
        .minY(0)
        .logicalHeight(400)
        .build()
    val dimKey = MinecraftServer.getDimensionTypeRegistry().register("void", dimension)
    val instanceContainer = MinecraftServer.getInstanceManager().createInstanceContainer(dimKey)
    instanceContainer.timeRate = 0
    instanceContainer.time = 6000
    instanceContainer.weather = Weather.RAIN
    instanceContainer.setGenerator { unit ->
        unit.modifier().fillBiome(DynamicRegistry.Key.of("void"))
        if (unit.absoluteStart().z() != 0.0) return@setGenerator
        unit.modifier().fill(
            BlockVec(0, 0, 13).add(unit.absoluteStart()),
            BlockVec(16, 328, 14).add(unit.absoluteStart()),
            BASE_BLOCK
        )
    }
    return instanceContainer
}

fun createVoidBiome() {
    val biome = Biome.builder().effects(
        BiomeEffects.builder()
            .skyColor(0x000000)
            .fogColor(0x000000)
            .biomeParticle(
                BiomeParticle(
                    0.001F,
                    Particle.BLOCK_CRUMBLE.withBlock(Block.BLACK_CONCRETE)
                )
            )
            .build()
        )
        .precipitation(Biome.Precipitation.NONE)
        .build()
    val biomeRegistry = MinecraftServer.getBiomeRegistry()
    biomeRegistry.register("void", biome)
}