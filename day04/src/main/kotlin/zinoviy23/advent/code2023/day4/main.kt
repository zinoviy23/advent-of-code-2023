package zinoviy23.advent.code2023.day4

import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.math.min

fun main() {
    val input = Path("input/day4/input.txt").readText().trimEnd().lines()
        .map { Card.parse(it) }

    val answer1 = input.sumOf { it.winPoints() }
    println("Task 1: $answer1")

    val copyManager = CopyManager(input.size)
    for ((index, card) in input.withIndex()) {
        copyManager.addCopiesOfNext(index, card.containingWinningNumbers)
    }
    val answer2 = copyManager.finalCardsAmount()
    println("Task 2: $answer2")
}

class CopyManager(private val cardsNumber: Int) {
    private val segments = SegmentTree(List(cardsNumber) { 1 })

    fun addCopiesOfNext(card: Int, points: Int) {
        val currentCardAmount = segments.getSum(card, card)
        segments[card + 1, min(cardsNumber - 1, card + points)] += currentCardAmount
    }

    fun finalCardsAmount(): Long {
        return segments.getSum(0, cardsNumber - 1)
    }
}

private val spaces = """\W+""".toRegex()

data class Card(
    val winingNumbers: List<Int>,
    val cardNumbers: Set<Int>,
) {
    fun winPoints(): Long {
        if (containingWinningNumbers == 0) return 0L
        return 1L shl (containingWinningNumbers - 1)
    }

    val containingWinningNumbers: Int by lazy {
        winingNumbers.filter { it in cardNumbers }.size
    }

    companion object {
        fun parse(s: String): Card {
            val (_, numbers) = s.split(":", limit = 2)
            val (rawWiningNumbers, rawCardNumbers) = numbers.split("|", limit = 2)
            return Card(
                winingNumbers = rawWiningNumbers.trim().split(spaces).map { it.toInt() },
                cardNumbers = rawCardNumbers.trim().split(spaces).map { it.toInt() }.toSet()
            )
        }
    }
}

//<editor-fold desc="Segment Tree">
/**
 * See [https://en.wikipedia.org/wiki/Segment_tree]
 */
private class SegmentTree(elements: List<Long>) {
    private val elements = mutableListOf<Long>().also { it.addAll(elements) }
    private val sum: MutableList<Long>
    private val add: MutableList<Long>

    init {
        var size = 1
        while (size < this.elements.size) {
            size = size shl 1
        }

        repeat(size - this.elements.size) {
            this.elements.add(0)
        }

        sum = MutableList(size * 2) { 0 }
        add = MutableList(size * 2) { 0 }
        build(1, 0, size - 1)
    }

    private fun build(nodeIndex: Int, l: Int, r: Int) {
        if (l == r) {
            sum[nodeIndex] = elements[l]
        } else {
            val mid = (l + r) / 2
            build(nodeIndex * 2, l, mid)
            build(nodeIndex * 2 + 1, mid + 1, r)
            sum[nodeIndex] = sum[nodeIndex * 2] + sum[nodeIndex * 2 + 1]
        }
    }

    inner class Modificator(private val l: Int, private val r: Int) {
        operator fun plusAssign(modification: Long) {
            modify(l, r, modification)
        }
    }

    operator fun get(l: Int, r: Int): Modificator {
        return Modificator(l, r)
    }

    private fun modify(l: Int, r: Int, modification: Long) {
        modify(1, elements.indices, l..r, modification)
    }

    private fun modify(nodeIndex: Int, nodeRange: ClosedRange<Int>, updateRange: ClosedRange<Int>, modification: Long) {
        if (!nodeRange.intersects(updateRange)) return

        if (nodeRange in updateRange) {
            sum[nodeIndex] += modification * nodeRange.length
            add[nodeIndex] += modification
        }
        else {
            modify(nodeIndex * 2, nodeRange.left, updateRange, modification)
            modify(nodeIndex * 2 + 1, nodeRange.right, updateRange, modification)
            sum[nodeIndex] = sum[nodeIndex * 2] + sum[nodeIndex * 2 + 1] + add[nodeIndex] * nodeRange.length
        }
    }

    fun getSum(l: Int, r: Int): Long {
        return getSum(1, elements.indices, l..r, 0)
    }

    private fun getSum(nodeIndex: Int, nodeRange: ClosedRange<Int>, queryRange: ClosedRange<Int>, additive: Long): Long {
        if (!nodeRange.intersects(queryRange)) return 0;
        if (nodeRange in queryRange) {
            return sum[nodeIndex] + additive * nodeRange.length
        }
        return getSum(nodeIndex * 2, nodeRange.left, queryRange, additive + add[nodeIndex]) +
                getSum(nodeIndex * 2 + 1, nodeRange.right, queryRange, additive + add[nodeIndex])
    }
}

private val ClosedRange<Int>.mid
    get() = (start + endInclusive) / 2

private val ClosedRange<Int>.length
    get() = (endInclusive - start + 1)

private val ClosedRange<Int>.left: ClosedRange<Int>
    get() = start..mid

private val ClosedRange<Int>.right: ClosedRange<Int>
    get() = (mid + 1)..endInclusive

private fun ClosedRange<Int>.intersects(other: ClosedRange<Int>): Boolean =
    !(start > other.endInclusive || endInclusive < other.start)

private operator fun ClosedRange<Int>.contains(other: ClosedRange<Int>): Boolean =
    other.start in this && other.endInclusive in this
//</editor-fold>
