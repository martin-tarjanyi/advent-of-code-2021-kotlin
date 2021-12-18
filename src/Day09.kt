import java.util.*

fun main() {
    fun part1(input: List<String>): Int {
        val heightMap = input.toHeightMap()
        return heightMap.findLowPoints().sumOf { point -> point.riskLevel() }
    }

    fun part2(input: List<String>): Int {
        val heightMap = input.toHeightMap()
        return heightMap.findLowPoints()
            .asSequence()
            .map { lowPoint -> heightMap.findBasin(lowPoint) }
            .sortedByDescending { basin -> basin.size }
            .take(3)
            .map { it.size }
            .reduce(Int::times)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}

private fun List<String>.toHeightMap(): HeightMap {
    return this.flatMapIndexed { rowIndex, line ->
        line.toCharArray().mapIndexed { columnIndex, string ->
            HeightMap.Point(
                HeightMap.Coordinate(rowIndex, columnIndex),
                HeightMap.Height(string.digitToInt())
            )
        }
    }.associateBy { it.coordinate }.let { HeightMap(it) }
}

data class HeightMap(val map: Map<Coordinate, Point>) {
    fun findLowPoints(): List<Point> {
        return points().filter { point ->
            point.coordinate.neighbours()
                .mapNotNull { neighborCoordinate -> pointByCoordinate(neighborCoordinate) }
                .all { neighborPoint -> point.height < neighborPoint.height }
        }
    }

    fun findBasin(lowPoint: Point): Basin {
        val basinPoints = mutableSetOf<Point>()
        basinPoints.add(lowPoint)
        val referenceBasinPoints = LinkedList<Point>()
        referenceBasinPoints.add(lowPoint)

        while (referenceBasinPoints.size != 0) {
            val referenceBasinPoint = referenceBasinPoints.pop()
            referenceBasinPoint.coordinate.neighbours()
                .mapNotNull { neighborCoordinate -> pointByCoordinate(neighborCoordinate) }
                .filter { it.height.value != 9 }
                .filter { point -> referenceBasinPoint.height < point.height }
                .forEach {
                    basinPoints.add(it)
                    referenceBasinPoints.add(it)
                }
        }

        return Basin(basinPoints)
    }

    private fun points() = this.map.values
    private fun pointByCoordinate(neighborCoordinate: Coordinate) = this.map[neighborCoordinate]

    data class Coordinate(val row: Int, val column: Int) {
        fun neighbours(): Set<Coordinate> {
            return setOf(
                Coordinate(row, column - 1),
                Coordinate(row, column + 1),
                Coordinate(row - 1, column),
                Coordinate(row + 1, column),
            )
        }
    }

    @JvmInline
    value class Height(val value: Int) {
        operator fun compareTo(other: Height): Int {
            return this.value.compareTo(other.value)
        }

        operator fun plus(increase: Int): Height {
            return Height(this.value + increase)
        }
    }

    data class Point(val coordinate: Coordinate, val height: Height) {
        fun riskLevel() = height.value + 1
    }

    data class Basin(val points: Set<Point>) {
        val size = points.size
    }
}
