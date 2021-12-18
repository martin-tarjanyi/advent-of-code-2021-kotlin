import java.util.*

val openingChars = setOf('{', '(', '[', '<')

val syntaxErrorScore = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137,
)

val openToClose = mapOf(
    '{' to '}',
    '(' to ')',
    '[' to ']',
    '<' to '>',
)

val completionScore = mapOf(
    ')' to 1,
    ']' to 2,
    '}' to 3,
    '>' to 4,
)

fun main() {
    fun part1(input: List<String>): Int {
        return input
            .mapNotNull { findFirstIllegalCharacter(it) }
            .sumOf { syntaxErrorScore[it]!! }
    }

    fun part2(input: List<String>): Long {
        return input
            .filter { !isCorruptedLine(it) }
            .map { createCompletionString(it) }
            .map { calculateScore(it) }
            .median()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}

fun isCorruptedLine(line: String): Boolean = findFirstIllegalCharacter(line) != null

fun findFirstIllegalCharacter(line: String): Char? {
    val currentOpenChars = LinkedList<Char>()

    for (currentChar in line.toCharArray()) {
        if (currentChar in openingChars) {
            currentOpenChars.add(currentChar)
        } else {
            val lastOpenChar = currentOpenChars.pollLast()
            val expectedCloseChar = openToClose[lastOpenChar]
            if (currentChar != expectedCloseChar) {
                return currentChar
            }
        }
    }

    return null
}

fun createCompletionString(line: String): String {
    val currentOpenChars = LinkedList<Char>()
    for (currentChar in line.toCharArray()) {
        if (currentChar in openingChars) {
            currentOpenChars.add(currentChar)
        } else {
            currentOpenChars.pollLast()
        }
    }

    return currentOpenChars.reversed().map { openToClose[it] }.joinToString(separator = "")
}

fun calculateScore(completionString: String): Long {
    return completionString.toCharArray()
        .map { completionScore[it]!! }
        .fold(0L) { acc, charScore -> acc * 5 + charScore }
}
