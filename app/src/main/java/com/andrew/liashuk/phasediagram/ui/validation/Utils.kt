package com.andrew.liashuk.phasediagram.ui.validation

import androidx.lifecycle.LifecycleOwner
import com.andrew.liashuk.phasediagram.ext.collectFlow
import com.andrew.liashuk.phasediagram.ext.textChanges
import com.google.android.material.textfield.TextInputLayout

// Add listener
fun TextInputLayout.createValidator(
    owner: LifecycleOwner,
    vararg conditions: Pair<Condition, String>,
    onValid: (String?) -> Unit = {}
): Validator {
    return ValidatorImpl(conditions.toList()).also { validator ->
        this.editText?.textChanges()?.collectFlow(owner) { text ->
            if (validator.isActive) {
                val errorText = validator.validate(text)
                this.error = errorText

                if (errorText == null) {
                    onValid.invoke(text)
                }
            }
        }
    }
}