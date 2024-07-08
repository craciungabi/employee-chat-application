package com.cg.chatapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.*

fun Date.nicerFormat(): String =
    SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).format(this)

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun createAlertDialog(
    context: Context,
    title: String,
    message: String,
    onPositive: () -> Unit = { },
    onNegative: () -> Unit = { }
) {
    val builder = AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Yes") { dialogInterface, _ ->
            onPositive.invoke()
            dialogInterface.dismiss()
        }
        .setNegativeButton("No") { dialogInterface, _ ->
            onNegative.invoke()
            dialogInterface.dismiss()
        }
    builder.create().show()
}