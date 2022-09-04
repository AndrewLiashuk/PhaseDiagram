package com.andrew.liashuk.phasediagram.mocks

import android.content.res.Resources
import android.graphics.drawable.Drawable
import com.andrew.liashuk.phasediagram.common.ResourceResolver

open class ResourceResolverMock : ResourceResolver {
    override fun getString(resId: Int, vararg formatArgs: Any): String = resId.toString()
    override fun getInt(resId: Int): Int = resId
    override fun getBoolean(resId: Int): Boolean = false
    override fun getDimension(id: Int): Float = id.toFloat()
    override fun getDimensionPixelSize(id: Int): Int = id
    override fun getDrawable(resId: Int): Drawable? = null
    override fun getColor(colorRes: Int, theme: Resources.Theme?): Int = colorRes
}