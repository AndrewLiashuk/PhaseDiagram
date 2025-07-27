package com.andrew.liashuk.phasediagram.common.ext

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

/**
 * Show double in a pretty format
 *
 * 20.00 show as 20,
 * 20.10 as 20.1
 * if null, show nothing
 */
fun Double?.toPrettyString(): String {
    return when (this) {
        null -> ""

        // round double by converting to long and compare with the original double, if same show rounded
        this.toLong().toDouble() -> this.toLong().toString() // show 20.0 as 20

        else -> String.format("%s", this)
    }
}