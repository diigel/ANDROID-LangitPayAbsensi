package com.absensi.langitpay.absent

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.absensi.langitpay.R
import com.absensi.langitpay.absent.camera.CameraActivity
import com.absensi.langitpay.abstraction.*
import com.absensi.langitpay.network.SharedPref
import com.absensi.langitpay.network.response.DataGetOfficeLocation
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
    private val viewModel: AbsentViewModel by viewModels()

    private val isValid = mutableListOf("", "", "", "")

    private val loader by lazy {
        context?.loaderDialog()
    }

    private val spinnerAdapter by lazy {
        ArrayAdapter<DataGetOfficeLocation>(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item
        )
    }

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
        validateButton()
        setupSpinner()
    }

    private fun initView() {
        text_username.text = SharedPref.getValue(resources.getString(R.string.pref_user_name))
        text_division.text = SharedPref.getValue(resources.getString(R.string.pref_user_division))
        text_nik.text = SharedPref.getValue(resources.getString(R.string.pref_user_nik))
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
        spinner_office_location.isEnabled = false

        btn_absen.clicked {
            loader?.show()
            when {
                spinner_office_location.selectedItemPosition == 0 -> {
                    loader?.dismiss()
                    context?.showDialogInfo("Silahkan pilih lokasi kantor")
                }
                context?.isLocationEnabled() == false -> {
                    loader?.dismiss()
                    startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                else -> {
                    getLocation { latitude, longitude ->
                        val locationMe = Location("").apply {
                            this.latitude = latitude ?: 0.0
                            this.longitude = longitude ?: 0.0
                        }
                        val locationOffice = Location("").apply {
                            this.latitude = isValid[1].toDouble()
                            this.longitude = isValid[2].toDouble()
                        }
                        if (!getLocationDistance(locationMe, locationOffice)) {
                            isValid[1] = latitude.toString()
                            isValid[2] = longitude.toString()
                            validateButton()
                            requestAbsent()
                        } else {
                            loader?.dismiss()
                            context?.showDialogInfo(
                                "Lokasi Kurang Akurat"
                            )
                        }

                    }
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
                    isValid[0] = image
                    spinner_office_location.isEnabled = true
                }
            } else {
                context?.showDialogInfo("Gagal Mengambil Image")
            }
            validateButton()
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
        val inMeters = 300f
        val distance = locationOffice.distanceTo(locationMe)
        logi("distance is -> $distance \n inMeter is -> $inMeters")
        logi("loc me is -> $locationMe \n loc office is -> $locationOffice")
        return distance >= inMeters
    }

    private fun requestAbsent() {
        viewModel.requestAbsentOffice(
            userId = SharedPref.getValue(resources.getString(R.string.pref_user_id)),
            name = SharedPref.getValue(resources.getString(R.string.pref_user_name)),
            typeAbsent = "1",
            image = isValid[0],
            address = isValid[3],
            latitude = isValid[1],
            longitude = isValid[2],
            division = SharedPref.getValue(resources.getString(R.string.pref_user_division)),
            noted = "Absen Di Kantor"
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

    private fun setupSpinner() {
        viewModel.getOfficeLocation().observe(viewLifecycleOwner, Observer {
            val data = it.data
            if (data != null) {
                spinnerAdapter.apply {
                    clear()
                    add(DataGetOfficeLocation(0, "", "", "", "Pilih Kantor"))
                    addAll(data)
                    notifyDataSetChanged()
                }
            }

            logi("data is -> ${it?.toJson()}")
        })

        spinner_office_location.adapter = spinnerAdapter
        spinner_office_location.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val itemFound = spinnerAdapter.getItem(position)
                itemFound.let {
                    if (position > 0) {
                        isValid[1] = itemFound?.lat.toString()
                        isValid[2] = itemFound?.long.toString()
                        isValid[3] = "${itemFound?.officeName}, ${itemFound?.address}"
                    }else{
                        isValid[1] = ""
                        isValid[2] = ""
                        isValid[3] = ""
                    }

                    validateButton()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
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
