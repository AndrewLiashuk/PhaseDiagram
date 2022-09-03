package com.andrew.liashuk.phasediagram.common.ext

fun <T> lazyNonSync(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)