package zinoviy23.advent.code2023.day5

import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.floor
import kotlin.math.sqrt

private val spaces = """\W+""".toRegex()

fun main() {
    val (times, distances) = Path("input/day6/input.txt").readText().trimEnd().lines()
        .map { line -> line.split(":", limit = 2).last().trim().split(spaces).map { it.toLong() } }

    val answer1 = times.zip(distances).map { (time, distance) -> countOfWaysToBeatARecord(time, distance) }
        .fold(1L) { acc, it -> acc * it }
    println("Task 1: $answer1")

    val answer2 = countOfWaysToBeatARecord(
        times.joinToString(separator = "") { "$it" }.toLong(),
        distances.joinToString(separator = "") { "$it" }.toLong(),
    )
    println("Task 2: $answer2")
}

private fun countOfWaysToBeatARecord(totalTime: Long, recordDistance: Long): Long {
    val recordTime = minTimeForRecord(totalTime, recordDistance)
    return if (totalTime % 2 == 0L) {
        (totalTime / 2 - recordTime) * 2 + 1
    } else {
        (totalTime / 2 - recordTime + 1) * 2
    }
}

private fun minTimeForRecord(totalTime: Long, recordDistance: Long): Long {
    val D = totalTime * totalTime - 4 * recordDistance
    return floor((totalTime - sqrt(D.toDouble())) / 2.0 + 1).toLong()
}

