import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val numbers = input.first().split(",").map { it.toInt() }
        val median = numbers.median()
        return numbers.sumOf { abs(median - it) }
    }

    fun part2(input: List<String>): Int {
        val numbers = input.first().split(",").map { it.toInt() }
        val max = numbers.maxOrNull() ?: throw IllegalArgumentException("Empty input.")

        return (0..max).minOf { alignPosition ->
            numbers.sumOf { num -> exponentialFuelCost(num, alignPosition) }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}

fun exponentialFuelCost(num1: Int, num2: Int): Int {
    val distance = abs(num1 - num2)
    return ((distance + 1) / 2.0 * distance).toInt()
}