private const val newFishTimer = 8
private const val fishTimerAfterGivesBirth = 6

fun main() {
    fun part1(input: List<String>): Int {
        var fishTimers = input.first().split(",").map { it.toInt() }

        repeat(80) {
            val newFishTimers = mutableListOf<Int>()

            fishTimers = fishTimers.map { timer ->
                if (timer == 0) {
                    newFishTimers.add(newFishTimer); fishTimerAfterGivesBirth
                } else timer - 1
            } + newFishTimers
        }

        return fishTimers.size
    }

    fun part2(input: List<String>): Long {
        var countByFishTimers = input.first().split(",")
            .map { it.toInt() }
            .groupBy { it }
            .mapValues { it.value.size.toLong() }

        repeat(256) {
            val updatedCountByFishTimers = mutableMapOf<Int, Long>()

            for ((timer, count) in countByFishTimers) {
                if (timer == 0) {
                    updatedCountByFishTimers.merge(newFishTimer, count, Long::plus)
                    updatedCountByFishTimers.merge(fishTimerAfterGivesBirth, count, Long::plus)
                } else {
                    updatedCountByFishTimers.merge(timer - 1, count, Long::plus)
                }
            }

            countByFishTimers = updatedCountByFishTimers
        }

        return countByFishTimers.values.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 5934)
    check(part2(testInput) == 26984457539)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
