fun main() {
    fun part1(input: List<String>): Int {
        val inputQueue = input.single().toCharArray()
            .map { char -> char.digitToInt(radix = 16).toString(radix = 2).let { extendToFourBits(it) } }
            .flatMap { it.toCharArray().toList() }
            .toCollection(ArrayDeque())
            .also { println(it) }

        return parsePackage(inputQueue).sumPacketVersions.also { println(it) }
    }

    fun part2(input: List<String>): Int {
        return -1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput) == 31)
    check(part2(testInput) == -1)

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

fun parsePackage(inputQueue: ArrayDeque<Char>): PackageParseResult {
    val packetVersion: Int = inputQueue.removeFirst(3).joinToString(separator = "").toInt(radix = 2)
    val typeId: Int = inputQueue.removeFirst(3).joinToString(separator = "").toInt(radix = 2)

    when (typeId) {
        4 -> {
            while (true) {
                val group = inputQueue.removeFirst(5).joinToString(separator = "")
                if (group[0] == '0') {
                    break
                }
            }
            return PackageParseResult(packetVersion, 0)
        }
        else -> { // operator
            val lengthTypeId = inputQueue.removeFirst()
            when (lengthTypeId) {
                '0' -> {
                    val totalLengthInBits =
                        inputQueue.removeFirst(15).joinToString(separator = "").toInt(radix = 2)
                    val expectedSizeAfterProcessing = inputQueue.size - totalLengthInBits
                    var packetVersions = packetVersion
                    while (inputQueue.size > expectedSizeAfterProcessing) {
                        val packageParseResult = parsePackage(inputQueue)
                        packetVersions += packageParseResult.sumPacketVersions
                    }
                    return PackageParseResult(packetVersions, 0)
                }
                '1' -> {
                    val subPackets =
                        inputQueue.removeFirst(11).joinToString(separator = "").toInt(radix = 2)

                    var packetVersions = packetVersion
                    for (i in 1..subPackets) {
                        val packageParseResult = parsePackage(inputQueue)
                        packetVersions += packageParseResult.sumPacketVersions
                    }
                    return PackageParseResult(packetVersions, 0)
                }
                else -> error("Invalid length type ID.")
            }
        }
    }
}

data class PackageParseResult(val sumPacketVersions: Int, val packetValue: Int)