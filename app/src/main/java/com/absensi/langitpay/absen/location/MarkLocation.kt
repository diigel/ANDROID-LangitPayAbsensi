package com.absensi.langitpay.absen.location

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.absensi.langitpay.R
import com.absensi.langitpay.abstraction.clicked
import com.absensi.langitpay.abstraction.gone
import com.absensi.langitpay.abstraction.visible
import com.absensi.langitpay.network.response.Item
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_mark_location.*
import kotlinx.android.synthetic.main.bottom_sheet_location.*

@SuppressLint("SetTextI18n")
class MarkLocation : AppCompatActivity() {

    private val composite = CompositeDisposable()

    private val jakartaLatLng = LatLng(-6.21462, 106.84513)

    private var animateMarker = true
    private var hasFetch = false


    private val repository by lazy {
        MarkLocationRepository(composite)
    }

    private var bottomSheet = BottomSheetBehavior<View>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mark_location)

        bottomSheet = BottomSheetBehavior.from(bottom_sheet)
        progress.gone(true)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        setupMaps()
    }

    private fun setupMaps(){
        bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
        (map_view as SupportMapFragment).getMapAsync {maps ->
            maps.moveCamera(CameraUpdateFactory.newLatLngZoom(jakartaLatLng,18f))

            val oldPosition = maps.cameraPosition.target

            maps.setOnCameraMoveStartedListener {
                // drag started
                if (animateMarker){
                    bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                    setAnimate(-50f,10)
                }

                hasFetch = false
            }

            maps.setOnCameraIdleListener {
                val newPosition = maps.cameraPosition.target
                if (newPosition != oldPosition){
                    // drag ended
                    setAnimate(0f,0)

                    getLocation(newPosition){item ->
                        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                        val findLocation = LatLng(item?.position?.lat ?: 0.0, item?.position?.lng ?: 0.0)

                        maps.animateCamera(CameraUpdateFactory.newLatLng(findLocation),200,
                            object : GoogleMap.CancelableCallback {
                                override fun onFinish() {
                                    hasFetch = true
                                    animateMarker = true
                                }
                                override fun onCancel() {
                                    animateMarker = true
                                }
                            })

                        text_title.text = item?.title
                        text_address.text = item?.address?.label
                    }
                }
            }
        }
    }

    private fun setAnimate(translationY : Float,padding : Int){
        icon_marker.animate().translationY(translationY).start()
        icon_marker_shadow.animate().withStartAction {
            icon_marker_shadow.setPadding(padding)
        }.start()
    }

    private fun getLocation (latLng : LatLng, result: (Item?) -> Unit){
        val at = "${latLng.latitude},${latLng.longitude}"

        if (!hasFetch){
            animateMarker = false
            progress.visible(true)
            repository.getLocation(at,resources.getString(R.string.maps_key)){
                if(it != null){
                    runOnUiThread {
                        progress.gone(true)
                        result.invoke(it.items.first())
                    }
                }else {
                    progress.gone(true)
                    bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                    text_title.text = "Maaf Terjadi Kesalahan"
                    text_address.text = "Silahkan Coba Kembali"
                    btn_confirmation.text = "Tutup"
                    btn_confirmation.clicked { onBackPressed() }
                }
            }
        }

    }

}