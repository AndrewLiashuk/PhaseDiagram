package com.andrew.liashuk.phasediagram.ext

fun <T> lazyNonSync(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)