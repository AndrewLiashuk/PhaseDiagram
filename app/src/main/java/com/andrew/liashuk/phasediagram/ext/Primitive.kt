package com.andrew.liashuk.phasediagram.ext

/**
 * Returns a progression that goes over the same range with the given step.
 * https://stackoverflow.com/questions/44315977/ranges-in-kotlin-using-data-type-double
 */
infix fun ClosedRange<Double>.step(step: Double): Iterable<Double> {
    require(start.isFinite())
    require(endInclusive.isFinite())
    require(step > 0.0) { "Step must be positive, was: $step." }
    val sequence = generateSequence(start) { previous ->
        if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
        val next = previous + step
        if (next > endInclusive) null else next
    }
    return sequence.asIterable()
}