package zinoviy23.advent.code2023.day2

import kotlin.io.path.Path
import kotlin.io.path.readText

fun main() {
    val input = Path("input/day2/input.txt").readText().trimEnd().lines().map { Game.parse(it) }

    val task1Amounts = CubeAmounts(red = 12, green = 13, blue = 14)
    val answer1 = input
        .filter { it.isPossibleFor(task1Amounts) }
        .sumOf {
            it.id
        }

    println("Task 1: $answer1")

    val answer2 = input.sumOf { it.minimalAmounts().power }
    println("Task 2: $answer2")
}

data class Game(
    val id: Int,
    val rounds: List<Round>
) {
    fun isPossibleFor(cubeAmounts: CubeAmounts): Boolean =
        rounds.all { it.isPossibleFor(cubeAmounts) }

    fun minimalAmounts(): CubeAmounts {
        return CubeAmounts(
            red = rounds.maxOf { it.red },
            green = rounds.maxOf { it.green },
            blue = rounds.maxOf { it.blue }
        )
    }

    companion object {
        fun parse(input: String): Game {
            val (header, rawRounds) = input.split(":", limit = 2)
            val id = header.split(" ").last().toInt()

            return Game(
                id,
                rawRounds.split(";").map { Round.parse(it) }
            )
        }
    }
}

data class Round(
    val blue: Int,
    val red: Int,
    val green: Int,
) {
    fun isPossibleFor(cubeAmounts: CubeAmounts): Boolean =
        blue <= cubeAmounts.blue && red <= cubeAmounts.red && green <= cubeAmounts.green

    companion object {
        fun parse(s: String): Round {
            val colors = s.split(",").associate { colorInfo ->
                val (count, color) = colorInfo.trim().split(" ", limit = 2)
                color to count.toInt()
            }

            return Round(
                blue = colors["blue"] ?: 0,
                red = colors["red"] ?: 0,
                green = colors["green"] ?: 0,
            )
        }
    }
}

data class CubeAmounts(
    val blue: Int,
    val red: Int,
    val green: Int,
) {
    val power: Long
        get() = blue.toLong() * red * green
}