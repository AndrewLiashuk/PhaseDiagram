package com.andrew.liashuk.phasediagram.ui.validation


interface Validator {

    val isValid: Boolean

    val isActive: Boolean

    fun start()

    fun stop()

    fun validate(input: String?): String?

    fun addCondition(condition: Pair<Condition, String>)

    fun removeCondition(condition: Pair<Condition, String>)

    fun removeCondition(condition: Condition)
}

internal class ValidatorImpl(
    conditions: List<Pair<Condition, String>>
) : Validator {
    private var conditions: MutableList<Pair<Condition, String>> = conditions.toMutableList()

    private var _isValid: Boolean = false
    override val isValid: Boolean
        get() = _isValid

    private var _isActive: Boolean = false
    override val isActive: Boolean
        get() = _isActive

    override fun start() {
        _isActive = true
    }

    override fun stop() {
        _isActive = false
    }

    override fun validate(input: String?): String? {
        val failedCondition = conditions.firstOrNull { (condition, _) ->
            condition.check(input)
        }

        _isValid = failedCondition == null
        return failedCondition?.second
    }

    override fun addCondition(condition: Pair<Condition, String>) {
        conditions.add(condition)
    }

    override fun removeCondition(condition: Pair<Condition, String>) {
        conditions.remove(condition)
    }

    override fun removeCondition(condition: Condition) {
        conditions.filter { it.first == condition }.forEach {
            conditions.remove(it)
        }
    }
}