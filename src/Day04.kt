fun main() {
    fun part1(input: List<String>): Int {
        val drawnNumbers: List<Int> = input.first().split(",").map { it.toInt() }

        var boards = input.drop(2)
            .bufferUntil { it.toCharArray().all { char -> char.isWhitespace() } }
            .map { toBoard(it) }

        drawnNumbers.take(4).forEach { drawnNumber ->
            boards = boards.map { board -> board.mark(drawnNumber) }
        }

        for (drawnNumber in drawnNumbers.drop(4)) {
            boards = boards.map { board -> board.mark(drawnNumber) }
            val boardWithBingo = boards.find { board -> board.hasBingo() }
            if (boardWithBingo != null) {
                val sum: Int = boardWithBingo.sumUnmarkedNumbers()
                return sum * drawnNumber
            }
        }

        throw IllegalStateException("No bingo found.")
    }

    fun part2(input: List<String>): Int {
        val drawnNumbers: List<Int> = input.first().split(",").map { it.toInt() }

        var boards = input.drop(2)
            .bufferUntil { it.toCharArray().all { char -> char.isWhitespace() } }
            .map { toBoard(it) }

        drawnNumbers.take(4).forEach { drawnNumber ->
            boards = boards.map { board -> board.mark(drawnNumber) }
        }

        for (drawnNumber in drawnNumbers.drop(4)) {
            boards = boards.map { board -> board.mark(drawnNumber) }

            if (boards.size == 1 && boards[0].hasBingo()) {
                val sum: Int = boards.single().sumUnmarkedNumbers()
                return sum * drawnNumber
            }
            boards = boards.filterNot { board -> board.hasBingo() }
        }

        throw IllegalStateException("No bingo found.")
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}

fun toBoard(rows: List<String>): Board {
    val fields: List<List<Field>> = rows.map { row ->
        row.trim()
            .split("""\s+""".toRegex())
            .map { fieldString -> Field(fieldString.trim().toInt()) }
    }
    return Board(fields)
}

data class Board(val fields: List<List<Field>>) {
    fun mark(drawnNumber: Int): Board {
        val newFields: MutableList<MutableList<Field>> = mutableListOf()

        for ((rowIndex, row) in fields.withIndex()) {
            newFields.add(mutableListOf())
            for (field in row) {
                if (field.value == drawnNumber) {
                    newFields[rowIndex].add(field.copy(marked = true))
                } else {
                    newFields[rowIndex].add(field)
                }
            }
        }

        return Board(newFields)
    }

    fun hasBingo(): Boolean {
        val hasRowBingo = fields.any { row -> row.all { it.marked } }
        val hasColumnBingo = columns().any { column -> column.all { it.marked } }

        return hasRowBingo || hasColumnBingo
    }

    fun sumUnmarkedNumbers(): Int {
        return fields.flatten().filter { field -> !field.marked }.sumOf { field -> field.value }
    }

    private fun columns(): List<List<Field>> {
        val columns: MutableList<List<Field>> = mutableListOf()

        val numberOfColumns = fields[0].size

        (0 until numberOfColumns)
            .forEach { columnIndex -> columns.add(fields.map { row -> row[columnIndex] }) }

        return columns.toList()
    }
}

data class Field(val value: Int, val marked: Boolean = false)