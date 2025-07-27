package com.andrew.liashuk.phasediagram

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import org.junit.Assert

@OptIn(ExperimentalContracts::class)
inline fun <reified T> assertInstanceOf(actual: Any?, message: String? = null) {
    contract {
        returns() implies (actual is T)
    }

    if (actual !is T) {
        var formatted = message?.let { "$it " } ?: ""
        formatted += "expected instance <${T::class.java.name}>, but was: "
        formatted += actual?.let { "<${it::class.java.name}>" } ?: "<null>"

        Assert.fail(formatted)
    }
}