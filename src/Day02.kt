fun main() {
    fun part1(input: List<String>): Int {
        var horizontal = 0
        var depth = 0

        input.map { it.split(" ") }
            .forEach { (direction, increase) ->
                when (direction) {
                    "forward" -> horizontal += increase.toInt()
                    "up" -> depth -= increase.toInt()
                    "down" -> depth += increase.toInt()
                }
            }

        return horizontal * depth
    }

    fun part2(input: List<String>): Int {
        var horizontal = 0
        var depth = 0
        var aim = 0

        input.map { it.split(" ") }
            .forEach { (direction, increase) ->
                when (direction) {
                    "up" -> aim -= increase.toInt()
                    "down" -> aim += increase.toInt()
                    "forward" -> {
                        horizontal += increase.toInt()
                        depth += aim * increase.toInt()
                    }
                }
            }

        return horizontal * depth
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}

