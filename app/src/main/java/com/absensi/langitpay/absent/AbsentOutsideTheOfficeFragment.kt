package com.absensi.langitpay.absent

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.absensi.langitpay.R
import com.absensi.langitpay.absent.camera.CameraActivity
import com.absensi.langitpay.absent.location.LatLongParcel
import com.absensi.langitpay.absent.location.MarkLocationActivity
import com.absensi.langitpay.abstraction.*
import com.absensi.langitpay.network.SharedPref
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.LatLng
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_absen_outside_the_office.*
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider

/**
 * Muhammad Ramdani 2020
 */

@SuppressLint("SetTextI18n")
class AbsentOutsideTheOfficeFragment : Fragment() {

    private val composite = CompositeDisposable()

    private val viewModel: AbsentViewModel by viewModels()

    private val isValid = mutableListOf("", "", "", "", "")

    private val loader by lazy {
        context?.loaderDialog()
    }

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
        text_username.text = SharedPref.getValue(resources.getString(R.string.pref_user_name))
        text_nik.text = SharedPref.getValue(resources.getString(R.string.pref_user_nik))
        text_division.text = SharedPref.getValue(resources.getString(R.string.pref_user_division))
        text_cek_location.clicked {
            if (context?.isLocationEnabled() == true) {
                getLocation { latitude, longitude ->
                    val latLong = LatLng(latitude ?: 0.0, longitude ?: 0.0)
                    startActivityForResult(
                        Intent(requireContext(), MarkLocationActivity::class.java)
                            .putExtra("myLocation", latLong.toParcel()), 50
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

        composite += et_location_noted.textChanges()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.toString() }
            .subscribe({
                if (!it.isNullOrEmpty()) {
                    isValid[3] = it
                } else {
                    isValid[3] = ""
                }
                validateButton()
            }, {
                it.printStackTrace()
            })

        btn_absen.clicked {
            if (context?.isLocationEnabled() == true) {
                getLocation { latitude, longitude ->
                    val locationMe = Location("").apply {
                        this.latitude = latitude ?: 0.0
                        this.longitude = longitude ?: 0.0
                    }
                    val locationOffice = Location("").apply {
                        this.latitude = resources.getString(R.string.lat_office_it).toDouble()
                        this.longitude = resources.getString(R.string.long_office_it).toDouble()
                    }
                    if (!getLocationDistance(locationMe, locationOffice)) {
                        requestAbsent()
                    } else {
                        context?.showDialogInfo(
                            "Lokasi Tidak Akurat, Silahkan anda pindah ke sekitaran lokasi yang anda tentukan")
                    }
                }
            }else{
                context?.showDialogInfo("Lokasi harus menyala untuk verifikasi lokasi kamu","Nyalakan Lokasi"){
                    startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
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
                        isValid[0] = image
                        text_cek_location.visible(true)
                    }
                } else {
                    context?.showDialogInfo("Gagal Mengambil Image")
                }
            }
            resultCode == AppCompatActivity.RESULT_OK && requestCode == 50 -> {
                val dataLocation = data?.extras?.getParcelable<LatLongParcel>("result_location")
                logi("data is $dataLocation")
                text_office_location.text = dataLocation?.title
                isValid[4] = dataLocation?.address ?: ""
                isValid[1] = dataLocation?.lat.toString()
                isValid[2] = dataLocation?.long.toString()
            }
        }
        validateButton()
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

    private fun getLocationDistance(locationMe: Location, locationOffice: Location): Boolean {
        val inMeters = 50
        val distance = locationOffice.distanceTo(locationMe)
        logi("distance is -> $distance \n inMeter is -> $inMeters")
        logi("loc me is -> $locationMe \n loc office is -> $locationOffice")
        return distance >= inMeters
    }

    private fun requestAbsent() {
        loader?.show()
        viewModel.requestAbsentOffice(
            userId = SharedPref.getValue(resources.getString(R.string.pref_user_id)),
            name = SharedPref.getValue(resources.getString(R.string.pref_user_name)),
            typeAbsent = "1",
            image = isValid[0],
            address = isValid[4],
            latitude = isValid[1],
            longitude = isValid[2],
            division = SharedPref.getValue(resources.getString(R.string.pref_user_division)),
            noted = isValid[3]
        ).observe(viewLifecycleOwner, Observer {
            loader?.dismiss()
            val data = it.data
            if (data != null) {
                isValid.clear()
                validateButton()
                context?.showDialogInfo(it.message, "Kembali") {
                    onBack()
                }
            } else {
                context?.showDialogInfo(it.message)
            }
        })
    }

    private fun validateButton() {
        btn_absen.isEnabled = !isValid.contains("")
    }

    override fun onDestroy() {
        super.onDestroy()
        composite.dispose()
        isValid.clear()
    }
}