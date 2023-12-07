package zinoviy23.advent.code2023.day3

import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() {
    val engine = Path("input/day3/input.txt").readText().trimEnd()
        .lines()

    val partNumbers = mutableListOf<Int>()
    val gearRatios = mutableListOf<Long>()
    for ((rowIndex, row) in engine.withIndex()) {
        for ((columnIndex, symbol) in row.withIndex()) {
            if (symbol.isDigit() || symbol == '.') continue

            val currentPartNumbers = mutableListOf<Int>()
            for (dx in listOf(-1, 0, 1)) {
                for (dy in listOf(-1, 0, 1)) {
                    if ((dx == 0 && dy == 0) || (rowIndex + dy) !in engine.indices || (columnIndex + dx) !in row.indices) {
                        continue
                    }

                    if (!engine[rowIndex + dy][columnIndex + dx].isDigit()) continue
                    // if there is a digit at left, and we are not at left, then we count this number when processing a left digit
                    if (dx != -1 && engine[rowIndex + dy].getOrNull(columnIndex + dx - 1)?.isDigit() == true) {
                        continue
                    }

                    val currentRow = engine[rowIndex + dy]
                    val firstDigitIndex = ((columnIndex + dx) downTo 0).takeWhile { currentRow[it].isDigit() }.last()
                    val lastDigitIndex = ((columnIndex + dx)..<currentRow.length).takeWhile { currentRow[it].isDigit() }.last()

                    currentPartNumbers += currentRow.substring(firstDigitIndex..lastDigitIndex).toInt()
                }
            }

            if (symbol == '*' && currentPartNumbers.size == 2) {
                gearRatios += currentPartNumbers[0].toLong() * currentPartNumbers[1]
            }

            partNumbers += currentPartNumbers
        }
    }

    val answer1 = partNumbers.sum()
    println("Task 1: $answer1")

    val answer2 = gearRatios.sum()
    println("Task 2: $answer2")
}