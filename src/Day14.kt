fun main() {
    fun part1(input: List<String>): Int {
        val result = generatePolymer(input, repeat = 10)
        val charFrequency = result.groupingBy { it }.eachCount()
        return charFrequency.values.maxOf { it } - charFrequency.values.minOf { it }
    }

    fun part2(input: List<String>): Long {
        val template = input.first()
        val insertionRules = input.drop(2).associate {
            val (adjacent, insertion) = it.split(" -> ")
            Pair(adjacent, insertion)
        }

        var adjacentFrequency: MutableMap<String, Long> = mutableMapOf()
        val charFrequency: MutableMap<Char, Long> = mutableMapOf()
        template.windowed(2).forEach { adjacentFrequency.merge(it, 1, Long::plus) }
        template.forEach { charFrequency.merge(it, 1, Long::plus) }

        repeat(40) {
            val newAdjacentFrequency = adjacentFrequency.toMutableMap()
            adjacentFrequency.forEach { (adjacent, frequency) ->
                newAdjacentFrequency.merge(adjacent, -frequency, Long::plus)
                insertionRules[adjacent]!!.let {
                    newAdjacentFrequency.merge(adjacent[0] + it, frequency, Long::plus)
                    newAdjacentFrequency.merge(it + adjacent[1], frequency, Long::plus)
                    charFrequency.merge(it.single(), frequency, Long::plus)
                }
            }
            adjacentFrequency = newAdjacentFrequency
        }

        return charFrequency.values.maxOf { it } - charFrequency.values.minOf { it }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588)
    check(part2(testInput) == 2188189693529L)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}

fun generatePolymer(input: List<String>, repeat: Int): String {
    val template = input.first()
    val insertionRules = input.drop(2).associate {
        val (adjacent, insertion) = it.split(" -> ")
        Pair(adjacent, insertion)
    }
    var result = template
    repeat(repeat) {
        result = result.windowed(2)
            .map { pair -> "${pair[0]}${insertionRules[pair]}${pair[1]}" }
            .reduce { acc, s -> acc.dropLast(1) + s }
    }
    return result
}
