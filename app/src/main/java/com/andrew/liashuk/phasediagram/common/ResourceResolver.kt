package com.andrew.liashuk.phasediagram.common

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.annotation.BoolRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface ResourceResolver {

    fun getString(@StringRes resId: Int, vararg formatArgs: Any = emptyArray()): String

    fun getInt(@IntegerRes resId: Int): Int

    fun getBoolean(@BoolRes resId: Int): Boolean

    fun getDimension(@DimenRes id: Int): Float

    fun getDimensionPixelSize(@DimenRes id: Int): Int

    fun getDrawable(@DrawableRes resId: Int): Drawable?

    fun getColor(@ColorRes colorRes: Int, theme: Resources.Theme? = null): Int
}

class DefaultResourceResolverImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourceResolver {
    override fun getString(resId: Int, vararg formatArgs: Any): String =
        context.resources.getString(resId, formatArgs)
    override fun getInt(resId: Int): Int = context.resources.getInteger(resId)
    override fun getBoolean(resId: Int): Boolean = context.resources.getBoolean(resId)
    override fun getDimension(id: Int): Float = context.resources.getDimension(id)
    override fun getDimensionPixelSize(id: Int): Int = context.resources.getDimensionPixelSize(id)
    override fun getDrawable(resId: Int): Drawable? = AppCompatResources.getDrawable(context, resId)
    override fun getColor(colorRes: Int, theme: Resources.Theme?): Int =
        ResourcesCompat.getColor(context.resources, colorRes, theme)
}