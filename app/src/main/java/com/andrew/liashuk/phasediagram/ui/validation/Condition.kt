package com.andrew.liashuk.phasediagram.ui.validation

interface Condition {

    fun check(input: String?): Boolean
}