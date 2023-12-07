package zinoviy23.advent.code2023.day5

import com.google.common.collect.Range
import com.google.common.collect.RangeMap
import com.google.common.collect.TreeRangeMap
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.time.DurationUnit
import kotlin.time.measureTime

fun main() {
    val rawInput = Path("input/day5/input.txt").readText().trimEnd().split("\n\n")
    val seeds = parseSeeds(rawInput.first())
    val mappings = rawInput.drop(1).map { Mapping.parse(it) }.associateBy { it.from }

    var values = seeds

    val time = measureTime {
        var currentFrom = "seed"
        while (currentFrom != "location") {
            val mapping = mappings[currentFrom]!!
            values = values.map { mapping[it] }
            currentFrom = mapping.to
        }
    }

    val answer1 = values.min()
    println("Task 1: $answer1")
    println("Task 1 took $time")

    val seedRanges = seeds.chunked(2).map { (start, length) -> start until (start + length) }
    var rangeValues = seedRanges

    val answer2: Long
    val time1 = measureTime {
        var currentFrom = "seed"
        while (currentFrom != "location") {
            val mapping = mappings[currentFrom]!!
            rangeValues = rangeValues.flatMap { mapping[it] }
            currentFrom = mapping.to
        }

        answer2 = rangeValues.minOf { it.first }
    }

    println("Task 2: $answer2")
    println("Task 2 took $time1")
}

private fun parseSeeds(s: String): List<Long> {
    return s.removePrefix("seeds: ").split(" ").map { it.toLong() }
}

data class MappingRange(val sourceStart: Long, val destinationStart: Long, val length: Long)

private class Mapping(val from: String, val to: String, private val ranges: RangeMap<Long, MappingRange>) {
    operator fun get(source: Long): Long {
        val (sourceStart, destinationStart) = ranges[source] ?: return source
        return destinationStart + source - sourceStart
    }

    operator fun get(sourceRange: LongRange): List<LongRange> {
        val ranges = ranges.subRangeMap(Range.closed(sourceRange.first, sourceRange.last)).asMapOfRanges()
        if (ranges.isEmpty()) return listOf(sourceRange)

        var last = sourceRange.first
        val resultRanges = mutableListOf<LongRange>()
        for ((range, mapping) in ranges) {
            if (range.lowerEndpoint() != last) {
                resultRanges += last until range.lowerEndpoint()
            }

            last = range.upperEndpoint() + 1
            resultRanges += (range.lowerEndpoint() - mapping.sourceStart + mapping.destinationStart)..(range.upperEndpoint() - mapping.sourceStart + mapping.destinationStart)
        }

        if (last <= sourceRange.last) {
            resultRanges += last .. sourceRange.last
        }

        return resultRanges
    }

    override fun toString(): String {
        return "Mapping(from=$from, to=$to)"
    }

    companion object {
        fun parse(s: String): Mapping {
            val lines = s.lines()
            val (from, to) = lines.first().removeSuffix(" map:").split("-to-", limit = 2)

            val ranges = TreeRangeMap.create<Long, MappingRange>()
            lines.drop(1).forEach { line ->
                val (destinationStart, sourceStart, rangeLength) = line.split(" ", limit = 3).map { it.toLong() }
                ranges.put(Range.closed(sourceStart, sourceStart + rangeLength - 1), MappingRange(sourceStart, destinationStart, rangeLength))
            }

            return Mapping(from, to, ranges)
        }
    }
}
