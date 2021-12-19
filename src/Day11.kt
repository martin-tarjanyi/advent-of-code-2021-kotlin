fun main() {
    fun part1(input: List<String>): Int {
        var octopusMap = input.toOctopusMap()

        repeat(100) {
            octopusMap = octopusMap.increaseEnergyLevel().flash()
        }

        return octopusMap.flashes
    }

    fun part2(input: List<String>): Int {
        var octopusMap = input.toOctopusMap()
        var steps = 0

        while (!octopusMap.hasAllJustFlashed()) {
            octopusMap = octopusMap.increaseEnergyLevel().flash()
            steps++
        }

        return steps
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 1656)
    check(part2(testInput) == 195)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}

private fun List<String>.toOctopusMap(): OctopusMap {
    return this.flatMapIndexed { rowIndex, line ->
        line.toCharArray().mapIndexed { columnIndex, string ->
            OctopusMap.Octopus(
                OctopusMap.Coordinate(rowIndex, columnIndex),
                OctopusMap.EnergyLevel(string.digitToInt())
            )
        }
    }.associateBy { it.coordinate }.let { OctopusMap(it.toMutableMap()) }
}

data class OctopusMap(val map: Map<Coordinate, Octopus>, val flashes: Int = 0) {
    fun increaseEnergyLevel(): OctopusMap {
        return this.copy(map = map.mapValues { (_, octopus) -> octopus.increaseEnergy() })
    }

    fun flash(): OctopusMap {
        var newFlashes = 0
        val newMap = map.toMutableMap()
        val alreadyFlashed = mutableSetOf<Coordinate>()

        while (newMap.any { (coordinate, octopus) -> octopus.energyLevel > 9 && coordinate !in alreadyFlashed }) {
            newMap.filter { (coordinate, octopus) -> octopus.energyLevel > 9 && coordinate !in alreadyFlashed }
                .forEach { (coordinate, octopus) ->
                    newFlashes++
                    alreadyFlashed.add(coordinate)
                    newMap[coordinate] = octopus.resetEnergy()
                    coordinate.neighbors().filter { it !in alreadyFlashed }.forEach { neighborCoordinate ->
                        newMap.computeIfPresent(neighborCoordinate) { _, octopus -> octopus.increaseEnergy() }
                    }
                }
        }

        return OctopusMap(newMap, flashes = flashes + newFlashes)
    }

    override fun toString(): String {
        val string = StringBuilder()
        string.append("flashes: $flashes").appendLine()
        for (i in 0..map.keys.size) {
            for (j in 0..map.keys.size) {
                map[Coordinate(i, j)]?.also { string.append(it.energyLevel.value).append(" ") }
            }
            string.appendLine()
        }
        return string.toString().trim()
    }

    fun hasAllJustFlashed(): Boolean = map.values.all { octopus -> octopus.energyLevel == EnergyLevel.ZERO }

    data class Coordinate(val row: Int, val column: Int) {
        fun neighbors(): Set<Coordinate> {
            return setOf(
                Coordinate(row, column - 1),
                Coordinate(row, column + 1),
                Coordinate(row - 1, column),
                Coordinate(row + 1, column),
                Coordinate(row - 1, column - 1),
                Coordinate(row - 1, column + 1),
                Coordinate(row + 1, column + 1),
                Coordinate(row + 1, column - 1),
            )
        }
    }

    data class Octopus(val coordinate: Coordinate, val energyLevel: EnergyLevel) {
        fun increaseEnergy(): Octopus = this.copy(energyLevel = energyLevel + 1)
        fun resetEnergy(): Octopus = this.copy(energyLevel = EnergyLevel.ZERO)
    }

    @JvmInline
    value class EnergyLevel(val value: Int) {
        companion object {
            val ZERO = EnergyLevel(0)
        }

        operator fun plus(increase: Int): EnergyLevel = EnergyLevel(value + increase)
        operator fun compareTo(other: Int): Int {
            return value.compareTo(other)
        }
    }
}


