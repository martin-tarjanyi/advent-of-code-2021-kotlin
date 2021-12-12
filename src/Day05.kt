fun main() {
    fun part1(input: List<String>): Int {
        val lines = input.map { inputLine -> parseVentLine(inputLine) }
            .filter { line -> line.isHorizontal() || line.isVertical() }

        val seen: MutableSet<Coordinate> = mutableSetOf()
        val dangerous: MutableSet<Coordinate> = mutableSetOf()
        for (coordinate in lines.flatMap { line -> line.coordinates() }) {
            if (coordinate in seen) {
                dangerous.add(coordinate)
            } else {
                seen.add(coordinate)
            }
        }

        return dangerous.size
    }

    fun part2(input: List<String>): Int {
        val lines = input.map { inputLine -> parseVentLine(inputLine) }

        val seen: MutableSet<Coordinate> = mutableSetOf()
        val dangerous: MutableSet<Coordinate> = mutableSetOf()
        for (coordinate in lines.flatMap { line -> line.coordinates() }) {
            if (coordinate in seen) {
                dangerous.add(coordinate)
            } else {
                seen.add(coordinate)
            }
        }

        return dangerous.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

fun parseVentLine(inputLine: String): Line {
    val (start, end) = inputLine.split(" -> ")
    val (startX, startY) = start.split(",").map { it.toInt() }
    val (endX, endY) = end.split(",").map { it.toInt() }
    return Line(Coordinate(startX, startY), Coordinate(endX, endY))
}

data class Line(val start: Coordinate, val end: Coordinate) {
    fun isHorizontal() = start.y == end.y
    fun isVertical() = start.x == end.x

    fun coordinates(): Set<Coordinate> {
        if (this.isHorizontal()) {
            val horizontalRange =
                if (this.start.x <= this.end.x) (this.start.x..this.end.x)
                else (this.end.x..this.start.x)
            return horizontalRange.map { x -> Coordinate(x, this.start.y) }.toSet()
        } else if (this.isVertical()) {
            val verticalRange =
                if (this.start.y <= this.end.y) (this.start.y..this.end.y)
                else (this.end.y..this.start.y)
            return verticalRange.map { y -> Coordinate(this.start.x, y) }.toSet()
        } else { // diagonal
            val xRange =
                if (this.start.x <= this.end.x) (this.start.x..this.end.x) else (this.start.x downTo this.end.x)
            val yRange =
                if (this.start.y <= this.end.y) (this.start.y..this.end.y) else (this.start.y downTo this.end.y)

            return xRange.zip(yRange) { x, y -> Coordinate(x, y) }.toSet()
        }
    }
}

data class Coordinate(val x: Int, val y: Int)
