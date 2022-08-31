package com.andrew.liashuk.phasediagram.ui.validation

import androidx.lifecycle.LifecycleOwner
import com.andrew.liashuk.phasediagram.ext.collectWithLifecycle
import com.andrew.liashuk.phasediagram.ext.textChanges
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.createValidator(
    owner: LifecycleOwner,
    vararg conditions: Pair<Condition, String>,
    onTextChanged: ((String?) -> Unit)? = null
): Validator {
    return ValidatorImpl(conditions.toList()).also { validator ->
        fun validate(text: String?) {
            // keeps validator state `isValid` always up to date
            val errorText = validator.validate(text)
            if (validator.isActive) {
                this.error = errorText
            }
        }

        this.editText?.textChanges()?.collectWithLifecycle(owner = owner) { text ->
            validate(text)
            onTextChanged?.invoke(text)
        }

        validator.isActiveFlow.collectWithLifecycle(owner) { isActive ->
            if (isActive) {
                validate(editText?.text?.toString())
            } else {
                // clear error if validator is not active
                this.error = null
            }
        }
    }
}