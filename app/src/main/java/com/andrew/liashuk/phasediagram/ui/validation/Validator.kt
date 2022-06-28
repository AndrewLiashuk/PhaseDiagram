package com.andrew.liashuk.phasediagram.ui.validation

import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout

interface Validator {

    val isValid: Boolean

    fun start()

    fun stop()

    fun setOnValidListener(onValid: ((String?) -> Unit)?)

    fun setOnErrorListener(onError: ((String) -> Unit)?)

    fun validate(input: String?): String?

    fun addCondition(condition: Pair<Condition, String>)

    fun removeCondition(condition: Pair<Condition, String>)

    fun removeCondition(condition:Condition)
}


internal class ValidatorImpl(
    conditions: List<Pair<Condition, String>>
) : Validator {
    private var conditions: MutableList<Pair<Condition, String>> = conditions.toMutableList()

    private var _isValid: Boolean = false
    override val isValid: Boolean
        get() = _isValid

    var onValid: ((String?) -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    var isActive: Boolean = true

    override fun start() {
        isActive = true
    }

    override fun stop() {
        isActive = false
    }

    override fun setOnValidListener(onValid: ((String?) -> Unit)?) {
        this.onValid = onValid
    }

    override fun setOnErrorListener(onError: ((String) -> Unit)?) {
        this.onError = onError
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
        conditions.firstOrNull { it.first == condition }?.let {
            conditions.remove(it)
        }
    }
}

fun TextInputLayout.createValidator(vararg conditions: Pair<Condition, String>): Validator {
    return ValidatorImpl(conditions.toList()).also { validator ->
        this.editText?.doOnTextChanged { text: CharSequence?, _, _, _ ->
            if (validator.isActive) {
                val value = text?.toString()
                val errorText = validator.validate(value)
                this.error = errorText

                if (errorText == null) {
                    validator.onValid?.invoke(value)
                } else {
                    validator.onError?.invoke(errorText)
                }
            }
        }
    }
}

