package com.absensi.langitpay.abstraction

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.absensi.langitpay.R
import com.absensi.langitpay.absen.location.LatLongParcel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.IllegalArgumentException
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*


fun Context.toast(msg: String?) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun Fragment.toast(msg: String?) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
fun View.toast(msg: String?) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

fun logi(msg: String?) = Log.d("LANGITPAY_LOG_INFO", msg)
fun loge(msg: String?) = Log.e("LANGITPAY_LOG_INFO", msg)

@SuppressLint("HardwareIds")
fun getDeviceUniqueId(context: Context): String =
    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

@SuppressLint("CheckResult")
fun View.clicked(action: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        action(it)

        try {
            val idResource = it.resources.getResourceEntryName(it.id)
            logi("--- id clicked -> $idResource ---")
        } catch (e: Resources.NotFoundException) {
            logi("--- id clicked -> not found ---")
        }
    }
    setOnClickListener(safeClickListener)
}

fun Context.showDialogInfo(){
    val dialog = BottomSheetDialog(this)
    val sheetView = LayoutInflater.from(this).inflate(R.layout.dialog_info, null)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.setCancelable(true)
    dialog.setContentView(sheetView)
}

fun <T : View> View.find(@IdRes id: Int): T = findViewById(id)
fun <T : View> Activity.find(@IdRes id: Int): T = findViewById(id)
fun <T : View> Fragment.find(@IdRes id: Int): T? = view?.findViewById(id)

fun View.visible(
    animation: Boolean? = null,
    speed: Long? = null,
    finishAnim: (View.() -> Unit)? = null,
    startAnim: (View.() -> Unit)? = null
) {
    startAnim?.invoke(this)
    if (animation != null) {
        animate()
            .alpha(1.0f)
            .setDuration(speed ?: 600)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    finishAnim?.invoke(this@visible)
                    visibility = View.VISIBLE
                }
            })
    } else {
        visibility = View.VISIBLE
    }
}

fun View.invisible(
    animation: Boolean? = null,
    speed: Long? = null,
    finishAnim: (View.() -> Unit)? = null,
    startAnim: (View.() -> Unit)? = null
) {
    startAnim?.invoke(this)
    if (animation != null) {
        animate()
            .alpha(0.0f)
            .setDuration(speed ?: 600)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    finishAnim?.invoke(this@invisible)
                    visibility = View.INVISIBLE
                }
            })
    } else {
        visibility = View.INVISIBLE
    }
}

fun View.gone(
    animation: Boolean? = null,
    speed: Long? = null,
    finishAnim: (View.() -> Unit)? = null,
    startAnim: (View.() -> Unit)? = null
) {
    startAnim?.invoke(this)
    if (animation != null) {
        animate()
            .alpha(0.0f)
            .setDuration(speed ?: 600)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    finishAnim?.invoke(this@gone)
                    visibility = View.GONE
                }
            })
    } else {
        visibility = View.GONE
    }
}

fun ImageView.loadResource(imageResource: Int){
    Glide.with(this).load(imageResource).into(this)
}

fun Context.loaderDialog(): AlertDialog {
    val builderProgress = AlertDialog.Builder(this)
        .setView(LayoutInflater.from(this).inflate(R.layout.loader_layout, null))
        .setCancelable(false)

    val dialogProgress = builderProgress.create()
    dialogProgress.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    return dialogProgress
}

fun View.visibilism(
    visible: Boolean,
    animation: Boolean? = null,
    speed: Long? = null,
    finishAnim: (View.() -> Unit)? = null,
    startAnim: (View.() -> Unit)? = null
) {
    if (visible) visible(
        animation = animation,
        speed = speed,
        finishAnim = finishAnim,
        startAnim = startAnim
    )
    else gone(animation = animation, speed = speed, finishAnim = finishAnim, startAnim = startAnim)
}

fun Context.getBitmap(path: String?, bitmap: (Bitmap) -> Unit) {
    Glide.with(this)
        .asBitmap()
        .load(path)
        .into(object : CustomTarget<Bitmap>() {
            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmap.invoke(resource)
            }

        })
}

fun Context.showDialogInfo(
    message: String?,
    buttonText: String? = "Tutup",
    dialogResult: (() -> Unit)? = null
) {
    val dialog = BottomSheetDialog(this)
    val sheetView = LayoutInflater.from(this).inflate(R.layout.dialog_info, null)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.setCancelable(true)
    dialog.setContentView(sheetView)

    val text = dialog.findViewById<TextView>(R.id.text_info)
    text?.text = message

    val btnCancel = dialog.findViewById<Button>(R.id.button_dismiss)

    btnCancel?.text = buttonText
    btnCancel?.setOnClickListener {
        dialog.dismiss()
        dialogResult?.invoke()
    }

    if (!dialog.isShowing) {
        dialog.show()
    }
}
fun TextView.autoSize(composite: CompositeDisposable? = null, minSize: Float? = null) {
    val disposable = textChanges()
        .observeOn(AndroidSchedulers.mainThread())
        .map { it.length }
        .subscribe({
            if (it > 10) {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, minSize ?: 12f)
            } else {
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            }
        }, {
            it.printStackTrace()
        })

    composite?.add(disposable)
}

fun LatLng.toParcel() : LatLongParcel {
    return LatLongParcel(latitude,longitude)
}

fun LatLongParcel.toLatlong() : LatLng {
    return LatLng(lat,long)
}

fun Activity.latLongExtras(key: String) : Lazy<LatLongParcel> {
    return lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        try {
            intent.getParcelableExtra<LatLongParcel>(key)
        }catch (e : NullPointerException){
            e.printStackTrace()
            throw IllegalArgumentException("key error")
        }
    }
}

fun Fragment.latLongExtras(key: String) : Lazy<LatLongParcel?> {
    return lazy(LazyThreadSafetyMode.SYNCHRONIZED){
        try {
            arguments?.getParcelable<LatLongParcel>(key)
        }catch (e : NullPointerException){
            e.printStackTrace()
            throw IllegalArgumentException("key error")
        }
    }
}

fun Activity.hideKeyboard() {
    var view = currentFocus
    if (view == null) view = View(this)
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun <T> route(
    call: Observable<T>,
    io: ((T) -> Unit)? = null,
    result: ((T) -> Unit)? = null,
    error: ((throwable: Throwable) -> Unit)? = null
): Disposable {
    return call
        .subscribeOn(Schedulers.io())
        .doOnNext {
            io?.invoke(it)
        }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
            result?.invoke(it)
        }, {
            it.printStackTrace()
            error?.invoke(it)
        })
}

fun String.apiToMonthDay(): String? {
    val fixStringDate = this.take(20).replace("T", " ")
    logi("string date -> $fixStringDate")

    val sdfOrigin = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val dateParsing = SimpleDateFormat("dd/MM", Locale.getDefault())
    dateParsing.timeZone = TimeZone.getTimeZone("Asia/Jakarta")

    val origin =  sdfOrigin.parse(fixStringDate)
    val stringDateFormat = SimpleDateFormat("dd-MMMM ", Locale.getDefault())
    return stringDateFormat.format(origin?:"")
}

private fun String.getDate(): Date? {
    val sdfOrigin = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return sdfOrigin.parse(replace("T", " "))
}

@SuppressLint("SimpleDateFormat")
fun String.formatDate(format: String): String {
    val dateParsing = SimpleDateFormat(format, Locale.ENGLISH)
    dateParsing.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
    return dateParsing.format(getDate())
}



