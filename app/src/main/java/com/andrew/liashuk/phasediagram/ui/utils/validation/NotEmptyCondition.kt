package com.andrew.liashuk.phasediagram.ui.utils.validation

class NotEmptyCondition : Condition {

    override fun check(input: String?): Boolean {
        return !input.isNullOrEmpty()
    }
}