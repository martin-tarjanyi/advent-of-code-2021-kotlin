fun main() {
    fun part1(input: List<String>): Int {
        val inputQueue = parseInput(input)
        return parsePackage(inputQueue).sumPacketVersions
    }

    fun part2(input: List<String>): Long {
        val inputQueue = parseInput(input)
        return parsePackage(inputQueue).packetValue
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    // check(part1(testInput) == 31)
    check(part2(testInput) == 1L)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}

fun extendToFourBits(bit: String): String {
    if (bit.length == 4) {
        return bit
    }

    val prefix = "0".repeat(4 - bit.length)
    return "$prefix$bit"
}

fun <T> ArrayDeque<T>.removeFirst(n: Int): List<T> {
    return generateSequence { this.removeFirst() }.take(n).toList()
}

fun parseInput(input: List<String>): ArrayDeque<Char> {
    return input.single().toCharArray()
        .map { char -> char.digitToInt(radix = 16).toString(radix = 2).let { extendToFourBits(it) } }
        .flatMap { it.toCharArray().toList() }
        .toCollection(ArrayDeque())
        .also { println(it) }
}

fun parsePackage(inputQueue: ArrayDeque<Char>): PackageParseResult {
    val packetVersion: Int = inputQueue.removeFirst(3).joinToString(separator = "").toInt(radix = 2)
    val typeId: Int = inputQueue.removeFirst(3).joinToString(separator = "").toInt(radix = 2)

    when (typeId) {
        4 -> {
            val binaryStringBuilder = StringBuilder()
            while (true) {
                val group = inputQueue.removeFirst(5).joinToString(separator = "")
                binaryStringBuilder.append(group.substring(1))
                if (group[0] == '0') {
                    break
                }
            }
            return PackageParseResult(packetVersion, binaryStringBuilder.toString().toLong(radix = 2))
        }
        else -> { // operator
            val lengthTypeId = inputQueue.removeFirst()
            val packageParseResults: List<PackageParseResult> = when (lengthTypeId) {
                '0' -> {
                    val totalLengthInBits =
                        inputQueue.removeFirst(15).joinToString(separator = "").toInt(radix = 2)
                    val expectedSizeAfterProcessing = inputQueue.size - totalLengthInBits
                    val packageParseResults = mutableListOf<PackageParseResult>()
                    while (inputQueue.size != expectedSizeAfterProcessing) {
                        packageParseResults.add(parsePackage(inputQueue))
                    }
                    packageParseResults
                }
                '1' -> {
                    val subPackets =
                        inputQueue.removeFirst(11).joinToString(separator = "").toInt(radix = 2)
                    val packageParseResults = mutableListOf<PackageParseResult>()
                    repeat (subPackets) {
                        packageParseResults.add(parsePackage(inputQueue))
                    }
                    packageParseResults
                }
                else -> error("Invalid length type ID.")
            }
            val mergedPackageParseResult: PackageParseResult = when (typeId) {
                0 -> packageParseResults.reduce { acc, packageParseResult ->
                    PackageParseResult(
                        acc.sumPacketVersions + packageParseResult.sumPacketVersions,
                        acc.packetValue + packageParseResult.packetValue
                    ) }
                1 -> packageParseResults.reduce { acc, packageParseResult ->
                    PackageParseResult(
                        acc.sumPacketVersions + packageParseResult.sumPacketVersions,
                        acc.packetValue * packageParseResult.packetValue
                    ) }
                2 -> {
                    val packetVersions = packageParseResults.sumOf { it.sumPacketVersions }
                    val packetValue = packageParseResults.minOf { it.packetValue }
                    PackageParseResult(packetVersions, packetValue)
                }
                3 -> {
                    val packetVersions = packageParseResults.sumOf { it.sumPacketVersions }
                    val packetValue = packageParseResults.maxOf { it.packetValue }
                    PackageParseResult(packetVersions, packetValue)
                }
                5 -> {
                    val packetVersions = packageParseResults.sumOf { it.sumPacketVersions }
                    val packetValue =
                        if (packageParseResults[0].packetValue > packageParseResults[1].packetValue) 1L else 0L
                    PackageParseResult(packetVersions, packetValue)
                }
                6 -> {
                    val packetVersions = packageParseResults.sumOf { it.sumPacketVersions }
                    val packetValue =
                        if (packageParseResults[0].packetValue < packageParseResults[1].packetValue) 1L else 0L
                    PackageParseResult(packetVersions, packetValue)
                }
                7 -> {
                    val packetVersions = packageParseResults.sumOf { it.sumPacketVersions }
                    val packetValue =
                        if (packageParseResults[0].packetValue == packageParseResults[1].packetValue) 1L else 0L
                    PackageParseResult(packetVersions, packetValue)
                }
                else -> error("Unexpected type Id.")
            }
            return mergedPackageParseResult.copy(sumPacketVersions = mergedPackageParseResult.sumPacketVersions + packetVersion)
        }
    }
}

data class PackageParseResult(val sumPacketVersions: Int, val packetValue: Long)