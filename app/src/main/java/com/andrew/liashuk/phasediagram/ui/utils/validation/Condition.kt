package com.andrew.liashuk.phasediagram.ui.utils.validation

interface Condition {

    fun check(input: String?): Boolean
}