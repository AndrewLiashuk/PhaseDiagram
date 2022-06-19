package com.andrew.liashuk.phasediagram.helpers

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.andrew.liashuk.phasediagram.R

@Deprecated("")
object Helpers {
    fun showToast(context: Context?, messageId: Int) {
        context?.let {
            Toast.makeText(context, messageId, Toast.LENGTH_LONG).show()
        }
    }


    fun showAlert(context: Context?, messageId: Int) {
        context?.let {
            androidx.appcompat.app.AlertDialog.Builder(context)
                .setMessage(messageId)
                .setPositiveButton(context.getString(android.R.string.ok), null)
                .create()
                .show()
        }
    }


    fun showErrorAlert(context: Context?, exception: Exception) {
        context?.let {
            val message = exception.message ?: context.getString(R.string.internal_error)
            showErrorAlert(context, message)
        }
    }


    fun showErrorAlert(context: Context?, messageId: Int) {
        context?.let {
            val message = context.getString(messageId)
            showErrorAlert(context, message)
        }
    }


    fun showErrorAlert(context: Context?, errorMessage: String?, title: String? = null) {
        context?.let {
            val dialogMessage = errorMessage ?: context.getString(R.string.null_message)

            AlertDialog.Builder(context)
                .setTitle(title ?: context.getString(R.string.exception_title))
                .setMessage(dialogMessage)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.cancel() }
                .show()
        }
    }
}