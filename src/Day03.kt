fun main() {
    fun part1(input: List<String>): Int {
        val bits = generateSequence { mutableMapOf<Char, Int>() }.take(input[0].length).toList()
        for (binary in input) {
            binary.toCharArray().forEachIndexed { index, char ->
                bits[index].merge(char, 1) { count, _ -> count + 1 }
            }
        }

        val gammaRate = bits.map { map -> if ((map['0'] ?: 0) > (map['1'] ?: 0)) '0' else '1' }
            .joinToString(separator = "")
        val epsilonRate = gammaRate.map { char -> if(char == '1') '0' else '1' }
            .joinToString(separator = "")

        return gammaRate.toInt(radix = 2) * epsilonRate.toInt(radix = 2)
    }

    fun part2(input: List<String>): Int {
        var oxygenRatingCandidates = input
        for (i in 0 until input[0].length) {
            val bitFrequency: Map<Char, Int> = oxygenRatingCandidates.map { it[i] }
                .groupingBy { it }
                .eachCount()
            val mostCommonBit = if ((bitFrequency['1'] ?: 0) >= (bitFrequency['0'] ?: 0)) '1' else '0'
            oxygenRatingCandidates = oxygenRatingCandidates.filter { candidate -> candidate[i] == mostCommonBit }

            if (oxygenRatingCandidates.size <= 1) {
                break
            }
        }
        val oxygenRating: Int = oxygenRatingCandidates.first().toInt(radix = 2)

        var coScrubberRatingCandidates = input
        for (i in 0 until input[0].length) {
            val bitFrequency: Map<Char, Int> = coScrubberRatingCandidates.map { it[i] }
                .groupingBy { it }
                .eachCount()
            val leastCommonBit = if ((bitFrequency['0'] ?: 0) <= (bitFrequency['1'] ?: 0)) '0' else '1'

            coScrubberRatingCandidates = coScrubberRatingCandidates.filter { candidate -> candidate[i] == leastCommonBit }

            if (coScrubberRatingCandidates.size <= 1) {
                break
            }
        }
        val coScrubberRating: Int = coScrubberRatingCandidates.first().toInt(radix = 2)

        return oxygenRating * coScrubberRating
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
