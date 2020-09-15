package com.absensi.langitpay.absen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.absensi.langitpay.R
import com.absensi.langitpay.absen.camera.CameraActivity
import com.absensi.langitpay.absen.location.LatLongParcel
import com.absensi.langitpay.absen.location.MarkLocationActivity
import com.absensi.langitpay.abstraction.*
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.LatLng
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_absen_outside_the_office.*
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider

/**
 * Muhammad Ramdani 2020
 */

class AbsentOutsideTheOfficeFragment : Fragment() {

    private val composite = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_absen_outside_the_office, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        text_office_location.clicked {
            if (isLocationEnabled()) {
                getLocation { latitude, longitude ->
                    val latlong = LatLng(latitude ?: 0.0, longitude ?: 0.0)
                    startActivityForResult(
                        Intent(requireContext(), MarkLocationActivity::class.java)
                            .putExtra("myLocation", latlong.toParcel()),50
                    )
                }
            } else {
                startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }

        }
        img_preview.clicked {
            withPermission(Manifest.permission.CAMERA) {
                if (it) {
                    val intent = Intent(requireContext(), CameraActivity::class.java)
                    startActivityForResult(intent, 20)
                } else {
                    toast("permission denied")
                }
            }
        }
        btn_absen.clicked {
            getLocation { latitude, longitude ->
                if (isLocationEnabled()) {

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        logi("resultCode $resultCode")
        when {
            resultCode == AppCompatActivity.RESULT_OK && requestCode == 20 -> {
                val image = data?.extras?.get("image") as String?
                if (image != null) {
                    context?.getBitmap(image) { imageBitmap ->
                        lin_image_camera.gone()
                        Glide.with(this).load(imageBitmap).into(img_preview)
                        //imagePath = image
                    }
                } else {
                    context?.showDialogInfo("Gagal Mengambil Image")
                }
                //validateButton()
            }
            resultCode == AppCompatActivity.RESULT_OK && requestCode == 50 -> {
                val dataLocation = data?.extras?.getParcelable<LatLongParcel>("result_location")
                logi("data is $dataLocation")
            }
        }
    }

    private fun getLocation(result: (latitude: Double?, longitude: Double?) -> Unit) {
        withPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
            if (it) {
                requestLocation(result)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation(
        result: (latitude: Double?, longitude: Double?) -> Unit
    ) {
        val request = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setNumUpdates(1)
            .setInterval(100)

        val provider = ReactiveLocationProvider(requireContext())
        composite += provider.getUpdatedLocation(request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ location ->
                val lat = location.latitude
                val lon = location.longitude
                result.invoke(lat, lon)
            }, {
                logi("cannot get location")
                result.invoke(null, null)
            })
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}