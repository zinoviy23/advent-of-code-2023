package zinoviy23.advent.code2023.day1

import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() {
    val input = Path("input/day1/input.txt")
        .readText()
        .lines()
        .filter { it.isNotBlank() }

    val answer1 = input
        .sumOf { firstAndLastDigit(it) }

    println("Task 1: $answer1")

    val answer2 = input
        .sumOf { line ->
            val firstDigit = line.findAnyOf(digits)!!.second
            val lastDigit = line.findLastAnyOf(digits)!!.second

            asDigit(firstDigit) * 10 + asDigit(lastDigit)
        }

    println("Task 2: $answer2")
}

private fun firstAndLastDigit(line: String): Long {
    val digits = line.filter { it.isDigit() }
    return "${digits.first()}${digits.last()}".toLong()
}

private fun asDigit(s: String): Long {
    if (s.length == 1 && s[0].isDigit()) return s.toLong()
    return digits.indexOf(s) + 1L
}

private val digits = listOf(
    "one",
    "two",
    "three",
    "four",
    "five",
    "six",
    "seven",
    "eight",
    "nine",
) + (1 .. 9).map { it.toString() }
