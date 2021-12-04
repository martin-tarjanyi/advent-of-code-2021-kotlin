fun main() {
    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 7)
    check(part2(testInput) == 5)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    return input.windowed(2).count { (previous, next) -> next > previous}
}

fun part2(input: List<String>): Int {
    return input.windowed(size = 3)
        .windowed(size = 2)
        .count {(previousGroup, nextGroup) -> nextGroup.sumOf { it.toInt() } > previousGroup.sumOf { it.toInt() }}
}
