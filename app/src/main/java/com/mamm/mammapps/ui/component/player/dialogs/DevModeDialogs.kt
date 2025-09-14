package com.mamm.mammapps.ui.component.player.dialogs

import android.app.AlertDialog
import android.content.Context

class DefaultDialogHelper(private val context: Context)  {
    fun showInfoDialog(title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}