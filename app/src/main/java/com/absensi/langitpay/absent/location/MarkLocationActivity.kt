package com.absensi.langitpay.absent.location

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import com.absensi.langitpay.R
import com.absensi.langitpay.abstraction.*
import com.absensi.langitpay.network.response.Item
import com.absensi.langitpay.network.response.ResultSearch
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_mark_location.*
import kotlinx.android.synthetic.main.bottom_sheet_location.*
import kotlinx.android.synthetic.main.bottom_sheet_location.view.*
import java.util.*
import java.util.concurrent.TimeUnit

@Parcelize
data class LatLongParcel(
    val lat: Double = 0.0,
    val long: Double = 0.0,
    val address: String? = null,
    val title: String? = null
) : Parcelable

@SuppressLint("SetTextI18n")
class MarkLocationActivity : AppCompatActivity() {

    private val composite = CompositeDisposable()

    private val myLocation by latLongExtras("myLocation")

    private var animateMarker = true
    private var hasFetch = false

    private val adapter by lazy {
        MarkAdapter()
    }

    private val repository by lazy {
        MarkLocationRepository(composite)
    }

    private var bottomSheet = BottomSheetBehavior<View>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_location)
        initView()
        setupMaps()
    }

    private fun initView() {
        bottomSheet = BottomSheetBehavior.from(bottom_sheet)
        progress.gone(true)
        rv_address.layoutManager = LinearLayoutManager(this)
        rv_address.adapter = adapter

        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            when {
                rv_address.isVisible -> {
                    rv_address.gone(true)
                    icon_marker.visible()
                    icon_marker_shadow.visible()
                    toolbar.setBackgroundColor(Color.TRANSPARENT)
                }
                !rv_address.isVisible -> onBack()
            }
        }

    }

    private fun setupMaps() {
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        (map_view as SupportMapFragment).getMapAsync { maps ->
            maps.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation.toLatlong(), 18f))

            val oldPosition = maps.cameraPosition.target

            maps.setOnCameraMoveStartedListener {
                // drag started
                if (animateMarker) {
                    bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                    setAnimate(-50f, 10)
                }

                hasFetch = false
            }

            maps.setOnCameraIdleListener {
                val newPosition = maps.cameraPosition.target
                if (newPosition != oldPosition) {
                    // drag ended
                    setAnimate(0f, 0)
                    getLocation(newPosition) { item ->
                        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                        val findLocation =
                            LatLng(item?.position?.lat ?: 0.0, item?.position?.lng ?: 0.0)

                        maps.animateCamera(CameraUpdateFactory.newLatLng(findLocation), 200,
                            object : GoogleMap.CancelableCallback {
                                override fun onFinish() {
                                    hasFetch = true
                                    animateMarker = true
                                }

                                override fun onCancel() {
                                    animateMarker = true
                                }
                            })

                        bottom_sheet.apply {
                            text_title.text = item?.title
                            text_address.text = item?.address?.label
                            btn_confirmation.clicked {
                                setupConfirmation(
                                    LatLongParcel(
                                        lat = item?.position?.lat ?: 0.0,
                                        long = item?.position?.lng ?: 0.0,
                                        title = item?.title,
                                        address = item?.address?.label
                                    )
                                )
                            }
                        }
                    }
                }
            }

            et_location.textChanges()
                .subscribeOn(Schedulers.io())
                .debounce(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.toString() }
                .subscribe({ key ->
                    val newPosition = maps.cameraPosition.target
                    logi("key is =-----> $key")
                    if (!key.isNullOrEmpty()) {
                        icon_marker.gone()
                        icon_marker_shadow.gone()
                        getSearchLocation(newPosition, key) { item ->
                            setupListAddress(maps, item, key)
                        }
                    }

                }, {
                    it.printStackTrace()
                })
        }
    }

    private fun setupListAddress(map: GoogleMap, list: List<ResultSearch>, key: String) {
        val filterList = list.filter {
            it.title.toLowerCase(Locale.getDefault()).contains(key)
        }
        adapter.updateList(filterList)
        rv_address.visible(true)
        toolbar.setBackgroundColor(Color.WHITE)

        adapter.itemClick { item ->
            rv_address.gone(true)
            hideKeyboard()

            when (bottomSheet.state) {
                BottomSheetBehavior.STATE_HIDDEN -> {
                    bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                }
                BottomSheetBehavior.STATE_EXPANDED -> {
                    bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                    Handler().postDelayed({
                        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                    }, 500)
                }
            }

            bottom_sheet.apply {
                text_title.text = item.title
                text_address.text =
                    HtmlCompat.fromHtml(item.vicinity ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
                btn_confirmation.clicked {
                    setupConfirmation(
                        LatLongParcel(
                            lat = item.position[0],
                            long = item.position[1],
                            title = item.title,
                            address = item.vicinity
                        )
                    )
                }
            }

            icon_marker.visible()
            icon_marker_shadow.visible()

            val lat = item.position[0]
            val lon = item.position[1]
            val latLon = LatLng(lat, lon)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLon, 18f))
            setAnimate(-50f, 10)
        }


    }

    private fun setupConfirmation(latLongParcel: LatLongParcel) {
        val intent = Intent()
        intent.putExtra("result_location", latLongParcel)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun setAnimate(translationY: Float, padding: Int) {
        icon_marker.animate().translationY(translationY).start()
        icon_marker_shadow.animate().withStartAction {
            icon_marker_shadow.setPadding(padding)
        }.start()
        toolbar.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun getLocation(latLng: LatLng, result: (Item?) -> Unit) {
        val at = "${latLng.latitude},${latLng.longitude}"

        if (!hasFetch) {
            et_location.text = null
            animateMarker = false
            progress.visible(true)
            repository.getLocation(at, resources.getString(R.string.maps_key)) {
                if (it != null) {
                    runOnUiThread {
                        progress.gone(true)
                        result.invoke(it.items.first())
                    }
                } else {
                    runOnUiThread {
                        progress.gone(true)
                        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                        bottomSheet.apply {
                            text_title.text = "Maaf Terjadi Kesalahan"
                            text_address.text = "Silahkan Coba Kembali"
                            btn_confirmation.text = "Tutup"
                            btn_confirmation.clicked { onBackPressed() }
                        }
                    }
                }
            }
        }
    }

    private fun getSearchLocation(
        latLng: LatLng,
        searchKey: String,
        result: (List<ResultSearch>) -> Unit
    ) {
        val at = "${latLng.latitude},${latLng.longitude}"

        if (bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        }

        animateMarker = false
        adapter.updateNetworkState(NetworkState.LOADING)
        repository.getSearchLocation(at, searchKey, resources.getString(R.string.maps_key)) {
            adapter.updateNetworkState(NetworkState.LOADED)
            if (it != null) {
                runOnUiThread {
                    result.invoke(it.results)
                }
            } else {
                runOnUiThread {
                    adapter.updateNetworkState(NetworkState.failed("Maaf Terjadi Kesalahan"))
                }
            }
        }
    }

}