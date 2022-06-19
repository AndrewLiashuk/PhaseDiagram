package com.andrew.liashuk.phasediagram.ext

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.andrew.liashuk.phasediagram.R

fun Context.showToast(@StringRes messageId: Int) {
    Toast.makeText(this, messageId, Toast.LENGTH_LONG).show()
}

fun Context.showAlert(@StringRes messageId: Int) {
    AlertDialog.Builder(this)
        .setMessage(messageId)
        .setPositiveButton(this.getString(android.R.string.ok), null)
        .create()
        .show()
}

fun Context.showErrorAlert(errorMessage: String?, title: String? = null) {
    AlertDialog.Builder(this)
        .setTitle(title ?: getString(R.string.exception_title))
        .setMessage(errorMessage ?: getString(R.string.null_message))
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.cancel() }
        .show()
}

fun Context.showErrorAlert(exception: Exception) {
    showErrorAlert(exception.message ?: getString(R.string.internal_error))
}

fun Context.showErrorAlert(@StringRes messageId: Int) {
    showErrorAlert(getString(messageId))
}