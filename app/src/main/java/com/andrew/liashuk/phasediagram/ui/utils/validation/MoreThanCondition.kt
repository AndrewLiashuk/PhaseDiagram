package com.andrew.liashuk.phasediagram.ui.utils.validation

class MoreThanCondition(
    private val value: Double,
    private val inclusive: Boolean = false
) : Condition {

    override fun check(input: String?): Boolean {
        // should not validate on empty input, this is required for optional fields
        if (input.isNullOrEmpty()) return true

        val inputNumber = input.toDoubleOrNull() ?: return false
        return if (inclusive) inputNumber >= value else inputNumber > value
    }
}