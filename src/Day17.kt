import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.math.sqrt

fun main() {
    fun part1(input: List<String>): Int {
        val (_, yRange) = parseRanges(input)
        // this logic only works in specific scenarios where target is below y axis and target x coordinate is not too big
        val startYVelocity = abs(yRange.first) - 1
        val highestPoint = ((startYVelocity + 1) / 2.0 * startYVelocity).roundToInt()
        return highestPoint
    }

    fun part2(input: List<String>): Int {
        val (xRange, yRange) = parseRanges(input)

        // Quadratic equation formula for the least x velocity to reach target
        val minXVelocity = ceil((-1 + sqrt(1 - 4.0 * -2 * xRange.first)) / 2).toInt()
        val maxXVelocity = xRange.last

        val minYVelocity = yRange.first
        val maxYVelocity = abs(yRange.first) - 1

        val validVelocityPairs: MutableList<Pair<Int, Int>> = mutableListOf()
        for (xV in minXVelocity..maxXVelocity) {
            for (yV in minYVelocity..maxYVelocity) {
                if (doesReachTarget(xV, yV, xRange, yRange)) {
                    validVelocityPairs.add(xV to yV)
                }
            }
        }

        return validVelocityPairs.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 45)
    check(part2(testInput) == 112)

    val input = readInput("Day17")
    println(part1(input))
    println(part2(input))
}

fun parseRanges(input: List<String>): Pair<IntRange, IntRange> {
    val (xRangeString, yRangeString) = input.single().removePrefix("target area: ").split(", ")

    val xRange = xRangeString.removePrefix("x=").let {
        val (first, second) = it.split("..").map { it.toInt() }
        first..second
    }

    val yRange = yRangeString.removePrefix("y=").let {
        val (first, second) = it.split("..").map { it.toInt() }
        first..second
    }
    return Pair(xRange, yRange)
}

fun doesReachTarget(initialXVelocity: Int, initialYVelocity: Int, xTargetRange: IntRange, yTargetRange: IntRange): Boolean {
    var currentXPosition = 0
    var currentYPosition = 0
    var currentXVelocity = initialXVelocity
    var currentYVelocity = initialYVelocity

    while (currentXPosition <= xTargetRange.last && currentYPosition >= yTargetRange.first) {
        currentXPosition += currentXVelocity
        currentYPosition += currentYVelocity
        currentXVelocity = (currentXVelocity - 1).coerceAtLeast(0)
        currentYVelocity--

        if (currentXPosition in xTargetRange && currentYPosition in yTargetRange) {
            return true
        }
    }

    return false
}