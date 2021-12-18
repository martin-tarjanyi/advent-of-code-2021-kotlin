
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5(): String = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray())).toString(16)

fun <T> Iterable<T>.bufferUntil(stopPredicate: (T) -> Boolean): List<List<T>> {
    val iterator = iterator()
    val lists = mutableListOf<List<T>>()
    var list = mutableListOf<T>()
    while (iterator.hasNext()) {
        val next = iterator.next()
        if (stopPredicate(next)) {
            lists.add(list)
            list = mutableListOf()
        } else {
            list.add(next)
        }
    }
    lists.add(list)

    return lists
}

fun List<Int>.median(): Int = if (this.size % 2 == 1) {
    val medianIndex = this.size / 2
    this.sorted()[medianIndex]
} else {
    val lowerMedianIndex = this.size / 2 - 1
    val upperMedianIndex = this.size / 2
    val sorted = this.sorted()
    (sorted[upperMedianIndex] + sorted[lowerMedianIndex]) / 2
}

fun List<Long>.median(): Long = if (this.size % 2 == 1) {
    val medianIndex = this.size / 2
    this.sorted()[medianIndex]
} else {
    val lowerMedianIndex = this.size / 2 - 1
    val upperMedianIndex = this.size / 2
    val sorted = this.sorted()
    (sorted[upperMedianIndex] + sorted[lowerMedianIndex]) / 2
}