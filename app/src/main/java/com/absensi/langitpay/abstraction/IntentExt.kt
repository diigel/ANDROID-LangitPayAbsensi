package com.absensi.langitpay.abstraction

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.absensi.langitpay.R

fun Activity.intentTo(c: Class<*>, extIntent: (Intent.() -> Unit)? = null) {
    val intent = Intent(this, c)
    extIntent?.invoke(intent)
    startActivity(intent)
    overridePendingTransition(
        R.anim.right_in,
        R.anim.left_out
    )
}

fun Fragment.intentTo(c: Class<*>, extIntent: (Intent.() -> Unit)? = null) {
    val intent = Intent(context, c)
    extIntent?.invoke(intent)
    startActivity(intent)
    activity?.overridePendingTransition(
        R.anim.right_in,
        R.anim.left_out
    )
}

fun Activity.intentTo(c: String, extIntent: (Intent.() -> Unit)? = null) {
    val intent = Intent(this, Class.forName(c))
    extIntent?.invoke(intent)
    startActivity(intent)
    overridePendingTransition(
        R.anim.right_in,
        R.anim.left_out
    )
}

fun Fragment.intentTo(c: String, extIntent: (Intent.() -> Unit)? = null) {
    val intent = Intent(context, Class.forName(c))
    extIntent?.invoke(intent)
    startActivity(intent)
    activity?.overridePendingTransition(
        R.anim.right_in,
        R.anim.left_out
    )
}

// context

fun Context.intentTo(c: Class<*>, extIntent: (Intent.() -> Unit)? = null) {
    val intent = Intent(this, c)
    extIntent?.invoke(intent)
    startActivity(intent)
    try {
        (this as AppCompatActivity).overridePendingTransition(
            R.anim.right_in,
            R.anim.left_out
        )
    } catch (e: ClassCastException) {
        logi(e.message)
    }
}

fun Context.intentTo(c: String, extIntent: (Intent.() -> Unit)? = null) {
    val intent = Intent(this, Class.forName(c))
    extIntent?.invoke(intent)
    startActivity(intent)
    try {
        (this as AppCompatActivity).overridePendingTransition(
            R.anim.right_in,
            R.anim.left_out
        )
    } catch (e: ClassCastException) {
        logi(e.message)
    }
}

// view

fun View.intentTo(c: Class<*>, extIntent: (Intent.() -> Unit)? = null) {
    val intent = Intent(context, c)
    extIntent?.invoke(intent)
    context.startActivity(intent)
    (context as AppCompatActivity).overridePendingTransition(
        R.anim.right_in,
        R.anim.left_out
    )
}

fun View.intentTo(c: String, extIntent: (Intent.() -> Unit)? = null) {
    val intent = Intent(context, Class.forName(c))
    extIntent?.invoke(intent)
    context.startActivity(intent)
    (context as AppCompatActivity).overridePendingTransition(
        R.anim.right_in,
        R.anim.left_out
    )
}

fun Activity.onBack(){
    onBackPressed()
    overridePendingTransition(
        R.anim.left_in,
        R.anim.right_out
    )
}

fun Fragment.onBack(){
    fragmentManager?.popBackStack()
    activity?.overridePendingTransition(
        R.anim.left_in,
        R.anim.right_out
    )
}