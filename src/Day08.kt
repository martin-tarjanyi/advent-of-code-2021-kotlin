fun main() {
    fun part1(input: List<String>): Int {
        val uniqueSegments = setOf(2, 3, 4, 7)
        return input.map { parseDisplayEntries(it) }
            .flatMap { it.outputPatterns }
            .count { it.chars.size in uniqueSegments }
    }

    fun part2(input: List<String>): Int {
        val displays = input.map { parseDisplayEntries(it) }

        return displays.sumOf { display ->
            val digitPatternsToDecode = display.digitPatterns.toMutableSet()
            val digitToPattern = mutableMapOf<Int, DigitPattern>()
            digitToPattern[1] = digitPatternsToDecode.single { it.chars.size == 2 }
                .also { digitPatternsToDecode.remove(it) }
            digitToPattern[7] = digitPatternsToDecode.single { it.chars.size == 3 }
                .also { digitPatternsToDecode.remove(it) }
            digitToPattern[4] = digitPatternsToDecode.single { it.chars.size == 4 }
                .also { digitPatternsToDecode.remove(it) }
            digitToPattern[8] = digitPatternsToDecode.single { it.chars.size == 7 }
                .also { digitPatternsToDecode.remove(it) }
            digitToPattern[9] = digitPatternsToDecode.single { it.chars.containsAll(digitToPattern[4]!!.chars) }
                .also { digitPatternsToDecode.remove(it) }
            digitToPattern[0] =
                digitPatternsToDecode.single { it.chars.size == 6 && it.chars.containsAll(digitToPattern[7]!!.chars) }
                    .also { digitPatternsToDecode.remove(it) }
            digitToPattern[6] = digitPatternsToDecode.single { it.chars.size == 6 }
                .also { digitPatternsToDecode.remove(it) }
            digitToPattern[3] = digitPatternsToDecode.single { it.chars.containsAll(digitToPattern[1]!!.chars) }
                .also { digitPatternsToDecode.remove(it) }
            digitToPattern[5] = digitPatternsToDecode.single { (digitToPattern[6]!!.chars - it.chars).size == 1 }
                .also { digitPatternsToDecode.remove(it) }
            digitToPattern[2] = digitPatternsToDecode.single()

            val patternToDigit = digitToPattern.entries.associate { it.value to it.key }

            return@sumOf display.outputPatterns
                .map { digitPattern -> patternToDigit[digitPattern] }
                .joinToString(separator = "")
                .toInt()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61_229)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}

fun parseDisplayEntries(entry: String): Display {
    val (signalPatterns, output) = entry.split("|").map { it.trim() }
    val digitPatterns = signalPatterns.split(" ").map { DigitPattern(it.toCharArray().toSet()) }
    val outputPatterns = output.split(" ").map { DigitPattern(it.toCharArray().toSet()) }
    return Display(digitPatterns, outputPatterns)
}

data class Display(val digitPatterns: List<DigitPattern>, val outputPatterns: List<DigitPattern>)
data class DigitPattern(val chars: Set<Char>)
