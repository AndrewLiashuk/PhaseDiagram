package com.andrew.liashuk.phasediagram.di

import android.annotation.SuppressLint
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.coroutineScope
import com.andrew.liashuk.phasediagram.common.DefaultDispatcherProviderImpl
import com.andrew.liashuk.phasediagram.common.DefaultResourceResolverImpl
import com.andrew.liashuk.phasediagram.common.DispatcherProvider
import com.andrew.liashuk.phasediagram.common.ResourceResolver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ProcessCoroutineScope

@Module
@InstallIn(SingletonComponent::class)
abstract class MainModule {

    @Binds
    abstract fun bindDispatcherProvider(
        dispatcherProvider: DefaultDispatcherProviderImpl
    ): DispatcherProvider

    @Binds
    abstract fun bindResourceResolver(
        resourceResolver: DefaultResourceResolverImpl
    ): ResourceResolver


    @SuppressLint("JvmStaticProvidesInObjectDetector")
    companion object {

        @ProcessCoroutineScope
        @JvmStatic
        @Provides
        fun provideProcessCoroutineScope(): CoroutineScope {
            return ProcessLifecycleOwner.get().lifecycle.coroutineScope
        }
    }
}