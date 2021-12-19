fun main() {
    fun part1(input: List<String>): Int {
        val (coordinatesStringList, foldStringList) = input.bufferUntil { it.isBlank() }
        val coordinates = parseCoordinates(coordinatesStringList)
        val folds = parseFolds(foldStringList)
        val maxX = coordinates.maxOf { it.x }
        val maxY = coordinates.maxOf { it.y }
        var paper = createPaper(maxX, maxY, coordinates)
        paper = paper.fold(folds.first())
        return paper.countMarked()
    }

    fun part2(input: List<String>): Paper {
        val (coordinatesStringList, foldStringList) = input.bufferUntil { it.isBlank() }
        val coordinates = parseCoordinates(coordinatesStringList)
        val folds = parseFolds(foldStringList)
        val maxX = coordinates.maxOf { it.x }
        val maxY = coordinates.maxOf { it.y }

        var paper = createPaper(maxX, maxY, coordinates)
        for (fold in folds) {
            paper = paper.fold(fold)
        }

        return paper
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 17)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}

fun parseCoordinates(coordinatesStringList: List<String>) =
    coordinatesStringList.takeWhile { it.isNotBlank() }.map { line ->
        val (x, y) = line.split(",").map { it.toInt() }
        PaperCoordinate(x, y)
    }

fun parseFolds(foldStringList: List<String>) = foldStringList
    .map {
        val matchResult = Regex(""".*([xy])=(\d+)""").matchEntire(it)!!
        val (_, axisString, valueString) = matchResult.groupValues
        Fold(Axis.valueOf(axisString.uppercase()), valueString.toInt())
    }

data class PaperCoordinate(val x: Int, val y: Int)

data class Fold(val axis: Axis, val value: Int)

enum class Axis {
    X, Y
}

data class Paper(val positions: List<MutableList<Boolean>>) {
    fun countMarked(): Int = positions.flatten().count { marked -> marked }
    fun fold(fold: Fold): Paper {
        val positionsAfterCut = cut(fold)

        when (fold.axis) {
            Axis.X -> {
                for (x in (fold.value + 1) until positions.size) {
                    for (y in 0 until positions[0].size) {
                        positionsAfterCut[fold.value - (x - fold.value)][y] =
                            positions[x][y] || positionsAfterCut[fold.value - (x - fold.value)][y]
                    }
                }
            }
            Axis.Y -> {
                for (x in 0 until positions.size) {
                    for (y in (fold.value + 1) until positions[0].size) {
                        positionsAfterCut[x][fold.value - (y - fold.value)] =
                            positions[x][y] || positionsAfterCut[x][fold.value - (y - fold.value)]
                    }
                }
            }
        }

        return Paper(positionsAfterCut)
    }

    private fun cut(fold: Fold): List<MutableList<Boolean>> {
        return when (fold.axis) {
            Axis.X -> positions.subList(0, fold.value)
            Axis.Y -> positions.map { it.subList(0, fold.value) }
        }
    }

    override fun toString(): String {
        val paperString = StringBuilder().appendLine("Paper:")
        for (y in positions[0].indices) {
            for (x in positions.indices) {
                paperString.append(if (positions[x][y]) '#' else '.')
            }
            paperString.appendLine()
        }
        paperString.appendLine()
        return paperString.toString()
    }
}

fun createPaper(maxX: Int, maxY: Int, coordinates: List<PaperCoordinate>): Paper {
    val positions = createPositions(maxX, maxY)
    coordinates.forEach { positions[it.x][it.y] = true }
    return Paper(positions)
}

private fun createPositions(
    maxX: Int,
    maxY: Int
): List<MutableList<Boolean>> {
    val positions = buildList {
        repeat(maxX + 1) {
            add(
                buildList {
                    repeat(maxY + 1) { add(false) }
                }.toMutableList()
            )
        }
    }
    return positions
}
