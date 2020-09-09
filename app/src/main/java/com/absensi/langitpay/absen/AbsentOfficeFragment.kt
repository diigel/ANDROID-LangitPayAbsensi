package com.absensi.langitpay.absen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.absensi.langitpay.R
import com.absensi.langitpay.absen.camera.CameraActivity
import com.absensi.langitpay.abstraction.*
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_absen_office.*
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider

/**
 * Muhammad Ramdani 2020
 */

class AbsentOfficeFragment : Fragment() {

    private val composite = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_absen_office, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 20) {
            val image = data?.extras?.get("image") as String?
            if (image != null) {
                context?.getBitmap(image) { imageBitmap ->
                    lin_image_camera.gone()
                    Glide.with(this).load(imageBitmap).into(img_preview)
                    //imagePath = image
                }
            }else{
                context?.showDialogInfo("Gagal Mengambil Image")
            }
            //validateButton()
        }
    }

    private fun getLocation(result: (latitude: Double?, longitude: Double?) -> Unit) {
        withPermission(Manifest.permission.ACCESS_FINE_LOCATION) {
            requestLocation(result)
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
            .subscribe({location ->
                val lat = location.latitude
                val lon = location.longitude
                result.invoke(lat, lon)
            }, {
                logi("cannot get location")
                result.invoke(null, null)
            })
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}