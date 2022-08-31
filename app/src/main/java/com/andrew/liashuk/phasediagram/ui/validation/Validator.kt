package com.andrew.liashuk.phasediagram.ui.validation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface Validator {

    val isValid: Boolean

    val isActive: Boolean

    val isActiveFlow: StateFlow<Boolean>

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

    private val _isActive = MutableStateFlow(false)
    override val isActiveFlow: StateFlow<Boolean> = _isActive.asStateFlow()
    override val isActive: Boolean
        get() = isActiveFlow.value

    override fun start() {
        _isActive.value = true
    }

    override fun stop() {
        _isActive.value = false
    }

    override fun validate(input: String?): String? {
        val failedCondition = conditions.firstOrNull { (condition, _) -> !condition.check(input) }

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