package com.andrew.liashuk.phasediagram.helpers

import android.app.Application
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.text.style.TypefaceSpan
import androidx.annotation.FontRes
import androidx.appcompat.content.res.AppCompatResources

interface ResourceResolver {

    fun getString(resId: Int): String

    fun getString(resId: Int, vararg formatArgs: Any): String

    fun getBoolean(resId: Int): Boolean

    fun getColor(colorRes: Int): Int

    fun getInt(resId: Int): Int

    fun getPlural(resId: Int, quantity: Int): String

    fun getDrawable(resId: Int): Drawable?

    fun getDimension(id: Int): Float

    class AndroidResourceResolver(private val app: Application) : ResourceResolver {
        override fun getString(resId: Int, vararg formatArgs: Any): String = app.resources.getString(resId, *formatArgs)
        override fun getString(resId: Int): String = app.resources.getString(resId)
        override fun getBoolean(resId: Int): Boolean = app.resources.getBoolean(resId)
        override fun getInt(resId: Int): Int = app.resources.getInteger(resId)
        override fun getColor(colorRes: Int): Int = app.resources.getColor(colorRes)
        override fun getPlural(resId: Int, quantity: Int): String = app.resources.getQuantityString(resId, quantity, quantity)
        override fun getDrawable(resId: Int): Drawable? {
            return try {
                AppCompatResources.getDrawable(app, resId)
            } catch (e: Resources.NotFoundException) {
                null
            }
        }
        override fun getDimension(id: Int): Float = app.resources.getDimension(id)
    }
}
