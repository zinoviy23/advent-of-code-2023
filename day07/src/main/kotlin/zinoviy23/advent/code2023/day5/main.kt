package zinoviy23.advent.code2023.day5

import zinoviy23.advent.code2023.day5.HandType.*
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.time.measureTime

fun main() {
    val cardsWithBid = Path("input/day7/input.txt").readText().trimEnd().lines()
        .map {
            val (rawHand, rawBid) = it.split(" ", limit = 2)
            Hand.fromString(rawHand) to rawBid.toLong()
        }

    val time1 = measureTime {
        val answer1 = cardsWithBid
            .sortedWith { o1, o2 -> defaultComparator.compare(o1.first, o2.first) }
            .mapIndexed { rank, (_, bid) -> bid * (rank + 1) }
            .sum()
        println("Task 1: $answer1")
    }
    println("Task 1 took $time1")

    val time2 = measureTime {
        val answer2 = cardsWithBid
            .sortedWith { o1, o2 -> jokerHandComparator.compare(o1.first, o2.first) }
            .mapIndexed { rank, (_, bid) -> bid * (rank + 1) }
            .sum()
        println("Task 2: $answer2")
    }
    println("Task 2 took $time2")
}

private val defaultComparator = Comparator<Hand> { current, other ->
    val compareToKinds = current.kind.compareTo(other.kind)
    if (compareToKinds != 0) return@Comparator compareToKinds

    current.cards.zip(other.cards)
        .map { (card, otherCard) -> card.compareTo(otherCard) }
        .firstOrNull { it != 0 } ?: 0
}

private val jokerCardComparator = Comparator<Card> { current, other ->
    when {
        current == Card.J && other == Card.J -> 0
        current == Card.J -> -1
        other == Card.J -> 1
        else -> current.compareTo(other)
    }
}

private val jokerHandComparator = Comparator<Hand> { current, other ->
    val compareToKinds = current.jokerKind.compareTo(other.jokerKind)
    if (compareToKinds != 0) return@Comparator compareToKinds

    current.cards.zip(other.cards)
        .map { (card, otherCard) -> jokerCardComparator.compare(card, otherCard) }
        .firstOrNull { it != 0 } ?: 0
}

enum class Card {
    C_2, C_3, C_4, C_5, C_6, C_7, C_8, C_9, T, J, Q, K, A;

    companion object {
        fun fromChar(c: Char): Card {
            if (c.isDigit()) {
                return Card.entries[c - '0' - 2]
            }
            return when (c) {
                'T' -> T
                'J' -> J
                'Q' -> Q
                'K' -> K
                'A' -> A
                else -> error("Unknown char '$c'")
            }
        }
    }

    override fun toString(): String {
        if (this <= C_9) return (ordinal + 2).toString()
        return super.toString()
    }
}

enum class HandType {
    HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND
}

class Hand(val cards: List<Card>) {
    val kind: HandType
    val jokerKind: HandType

    init {
        assert(cards.size == 5)
        kind = defaultHandType(cards)
        jokerKind = jokerHandType(cards)
    }

    override fun toString(): String {
        return "Hand(cards=$cards, kind=$kind)"
    }

    companion object {
        fun fromString(s: String): Hand {
            return Hand(s.map { Card.fromChar(it) })
        }
    }
}

private fun defaultHandType(cards: List<Card>): HandType {
    val cardsAmounts = Card.entries.associateWithTo(mutableMapOf()) { 0 }
    for (card in cards) {
        cardsAmounts[card] = cardsAmounts[card]!! + 1
    }
    val amounts = cardsAmounts.values

    return defaultHandType(amounts)
}

private fun defaultHandType(amounts: Collection<Int>): HandType {
    return when {
        amounts.any { it == 5 } -> FIVE_OF_A_KIND
        amounts.any { it == 4 } -> FOUR_OF_A_KIND
        amounts.any { it == 3 } && amounts.any { it == 2 } -> FULL_HOUSE
        amounts.any { it == 3 } -> THREE_OF_A_KIND
        amounts.count { it == 2 } == 2 -> TWO_PAIR
        amounts.any { it == 2 } -> ONE_PAIR
        else -> HIGH_CARD
    }
}

private fun jokerHandType(cards: List<Card>): HandType {
    val cardsAmounts = Card.entries.associateWithTo(mutableMapOf()) { 0 }
    for (card in cards) {
        cardsAmounts[card] = cardsAmounts[card]!! + 1
    }
    val jokers = cardsAmounts.remove(Card.J)!!

    val amounts = cardsAmounts.values
    if (jokers == 0) return defaultHandType(amounts)

    return when {
        amounts.any { it + jokers == 5 } -> FIVE_OF_A_KIND
        amounts.any { it + jokers == 4 } -> FOUR_OF_A_KIND
        // There can be at most 2 jokers and at least 1, because 3 will produce FOUR_OF_A_KIND.
        // If there is 2 J, it will be impossible to have FULL_HOUSE, because there can't be another pair (2 + 2J = 4).
        // Without another pair, there is just 1 pair and 3 different cards => only 1 joker
        // @e can add 2 and 2 + J, because if there is already 3 it will be FOUR_OF_A_KIND (3 + J)
        jokers == 1 && amounts.count { it == 2 } == 2 -> FULL_HOUSE
        amounts.any { it + jokers == 3 } -> THREE_OF_A_KIND
        // 1 joker requires another pair => at least 3. So there are only 2 jokers, but it will produce 3 with any card => no two pairs
        // 2J + any card = 3 => only 1 joker and no pairs.
        amounts.count { it == 1 } == 4 -> ONE_PAIR
        else -> error("Impossible case! $cards")
    }
}