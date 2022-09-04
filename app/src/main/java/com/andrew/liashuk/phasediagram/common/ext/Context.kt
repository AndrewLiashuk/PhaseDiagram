package com.andrew.liashuk.phasediagram.common.ext

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.andrew.liashuk.phasediagram.R

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, duration).show()
}

fun Context.showToast(@StringRes messageId: Int, duration: Int = Toast.LENGTH_LONG) {
    showToast(getString(messageId), duration)
}

fun Context.showAlert(@StringRes messageId: Int) {
    AlertDialog.Builder(this)
        .setMessage(messageId)
        .setPositiveButton(getString(android.R.string.ok), null)
        .create()
        .show()
}

fun Context.showErrorAlert(errorMessage: String, title: String? = null) {
    AlertDialog.Builder(this)
        .setTitle(title ?: getString(R.string.exception_title))
        .setMessage(errorMessage)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton(android.R.string.ok, null)
        .show()
}

fun Context.showErrorAlert(exception: Exception) {
    exception.message?.let(::showErrorAlert)
}

fun Context.showErrorAlert(@StringRes messageId: Int) {
    showErrorAlert(getString(messageId))
}