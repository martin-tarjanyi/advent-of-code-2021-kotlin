@file:OptIn(ExperimentalTime::class)

import java.util.*
import kotlin.time.ExperimentalTime

fun main() {
    fun part1(input: List<String>): Int {
        val risk: Map<CavernCoordinate, Int> = toCavern(input)
        return findShortestDistanceFromStartToEnd(risk)
    }

    fun part2(input: List<String>): Int {
        var risk: Map<CavernCoordinate, Int> = toCavern(input)
        risk = extendCavern(risk, originalRows = input.size, originalColumns = input.first().length)
        return findShortestDistanceFromStartToEnd(risk)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 40)
    check(part2(testInput) == 315)

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}

fun toCavern(input: List<String>): Map<CavernCoordinate, Int> {
    val map: MutableMap<CavernCoordinate, Int> = mutableMapOf()
    for ((rowIndex, line) in input.withIndex()) {
        for ((columnIndex, riskLevelChar) in line.toCharArray().withIndex()) {
            val coordinate = CavernCoordinate(rowIndex, columnIndex)
            val riskLevel = riskLevelChar.digitToInt()
            map[coordinate] = riskLevel
        }
    }
    return map
}

// dijsktra
fun findShortestDistanceFromStartToEnd(risk: Map<CavernCoordinate, Int>): Int {
    val start = CavernCoordinate(0, 0)
    val end = risk.keys.maxWithOrNull(compareBy<CavernCoordinate> { it.row }.thenBy { it.column })!!
    val shortestPath: MutableMap<CavernCoordinate, CavernCoordinate> = mutableMapOf()
    val queue = CavernPriorityQueue<CavernPointCost, CavernCoordinate> { it.coordinate }
    risk.entries.forEach { (key, _) -> queue.add(CavernPointCost(key, Int.MAX_VALUE)) }
    queue.changePriority(queue[start], queue[start].copy(cost = 0))

    while (queue.isNotEmpty()) {
        val current = queue.min()

        if (current.coordinate == end) {
            break
        }

        for (neighbor in current.coordinate.neighbors().filter { it in queue }) {
            val newDistance = current.cost + risk[neighbor]!!
            val shortestDistance = queue[neighbor].cost

            if (newDistance < shortestDistance) {
                queue.changePriority(queue[neighbor], queue[neighbor].copy(cost = newDistance))
                shortestPath[neighbor] = current.coordinate
            }
        }
    }

    return queue[end].cost
}

fun extendCavern(
    risk: Map<CavernCoordinate, Int>,
    originalRows: Int,
    originalColumns: Int
): Map<CavernCoordinate, Int> {
    val extendedRiskMap = risk.toMutableMap()

    for (row in 0 until originalRows * 5) {
        for (column in 0 until originalColumns * 5) {
            extendedRiskMap.computeIfAbsent(CavernCoordinate(row, column)) {
                val referencePointRisk = extendedRiskMap[CavernCoordinate(row, column - originalColumns)]
                    ?: extendedRiskMap[CavernCoordinate(row - originalRows, column)]
                    ?: throw IllegalStateException("No reference point exists.")
                if (referencePointRisk < 9) referencePointRisk + 1 else 1
            }
        }
    }

    return extendedRiskMap.toMap()
}


fun printTable(risk: Map<CavernCoordinate, Int>) {
    risk.entries.sortedWith(compareBy<Map.Entry<CavernCoordinate, Int>> { it.key.row }
        .thenBy { it.key.column })
        .groupBy { it.key.row }
        .forEach { (_, value) ->
            println(value.map { it.value }.joinToString(separator = ""))
        }
}

data class CavernCoordinate(val row: Int, val column: Int) {
    fun neighbors(): Set<CavernCoordinate> {
        return setOf(
            CavernCoordinate(row, column - 1),
            CavernCoordinate(row, column + 1),
            CavernCoordinate(row - 1, column),
            CavernCoordinate(row + 1, column),
        )
    }
}

data class CavernPointCost(val coordinate: CavernCoordinate, override val cost: Int): ScoredEntry

interface ScoredEntry {
    val cost: Int
}

class CavernPriorityQueue<T : ScoredEntry, R>(private val entryKey: (T) -> R) {
    private val set = mutableSetOf<R>() // for quick contains check
    private val map = mutableMapOf<R, T>() // for quick lookup
    private val sortedMap = TreeMap<Int, MutableSet<T>>() // sorted cost to item mapping

    fun add(t: T) {
        sortedMap.merge(t.cost, mutableSetOf(t)) { a, b -> a.addAll(b); a }
        set.add(entryKey(t))
        map[entryKey(t)] = t
    }

    operator fun contains(r: R): Boolean {
        return set.contains(r)
    }

    fun min(): T {
        val (score, pollSet) = sortedMap.pollFirstEntry()
        val poll = pollSet.first()
        pollSet.remove(poll)
        if (pollSet.isNotEmpty()) {
            sortedMap[score] = pollSet
        }

        set.remove(entryKey(poll))
        return poll
    }

    fun changePriority(original: T, new: T): Unit {
        sortedMap[original.cost]?.remove(original)
        sortedMap.merge(new.cost, mutableSetOf(new)) { a, b -> a.union(b).toMutableSet() }
        map[entryKey(original)] = new
    }

    fun isNotEmpty(): Boolean {
        return !sortedMap.isEmpty()
    }

    operator fun get(key: R): T {
        return map[key]!!
    }
}
