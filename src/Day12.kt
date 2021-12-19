fun main() {
    fun part1(input: List<String>): Int {
        val caveConnections = parseCaveConnections(input)
        val isValidNextCave: (Cave, Path) -> Boolean = { cave, path -> cave.isBig() || cave !in path }
        val paths = searchPaths(caveConnections, Path(listOf(Cave.START)), isValidNextCave)
        return paths.size
    }

    fun part2(input: List<String>): Int {
        val caveConnections = parseCaveConnections(input)
        val isValidNextCave: (Cave, Path) -> Boolean = { cave, path ->
            cave.isBig() || cave !in path || path.doesNotHaveSameSmallCaveTwice()
        }
        val paths = searchPaths(caveConnections, Path(listOf(Cave.START)), isValidNextCave).onEach { println(it) }
        return paths.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 10)
    check(part2(testInput) == 36)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

private fun searchPaths(
    connections: Map<Cave, Set<Cave>>,
    path: Path,
    isValidNextCave: (Cave, Path) -> Boolean
): List<Path> {
    if (path.last() == Cave.END) {
        return listOf(path)
    }
    val validNextCaves = connections[path.last()]!!.filter { isValidNextCave(it, path) }
    return validNextCaves.map { cave -> searchPaths(connections, path.extend(cave), isValidNextCave) }.flatten()
}

private fun parseCaveConnections(input: List<String>): Map<Cave, Set<Cave>> {
    val paths = mutableMapOf<Cave, Set<Cave>>()

    for (line in input) {
        val (cave1, cave2) = line.split("-").map { Cave(it) }
        when {
            cave1 == Cave.START -> paths.merge(cave1, setOf(cave2)) { old, new -> old + new }
            cave2 == Cave.START -> paths.merge(cave2, setOf(cave1)) { old, new -> old + new }
            cave1 == Cave.END -> paths.merge(cave2, setOf(cave1)) { old, new -> old + new }
            cave2 == Cave.END -> paths.merge(cave1, setOf(cave2)) { old, new -> old + new }
            else -> {
                paths.merge(cave1, setOf(cave2)) { old, new -> old + new }
                paths.merge(cave2, setOf(cave1)) { old, new -> old + new }
            }
        }
    }

    return paths.toMap()
}

@JvmInline
value class Cave(val value: String) {
    companion object {
        val START = Cave("start")
        val END = Cave("end")
    }

    fun isBig(): Boolean = value.all { it.isUpperCase() }
    fun isSmall(): Boolean = !isBig()
}

data class Path(val caves: List<Cave>) {
    operator fun contains(cave: Cave): Boolean = cave in caves
    fun last(): Cave = caves.last()
    fun extend(cave: Cave): Path = Path(caves + cave)

    fun doesNotHaveSameSmallCaveTwice(): Boolean {
        return caves.filter { it.isSmall() }
            .groupingBy { it }
            .eachCount()
            .values
            .none { it > 1 }
    }
}
