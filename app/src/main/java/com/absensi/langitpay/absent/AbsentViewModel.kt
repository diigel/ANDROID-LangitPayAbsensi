package com.absensi.langitpay.absent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.absensi.langitpay.abstraction.getDeviceUniqueId
import com.absensi.langitpay.abstraction.logi
import com.absensi.langitpay.abstraction.toJson
import com.absensi.langitpay.network.AbsentLangitPayAplication
import com.absensi.langitpay.network.BaseUrl
import com.absensi.langitpay.network.Network
import com.absensi.langitpay.network.response.GetOfficeLocation
import com.absensi.langitpay.network.response.RequestAbsent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.create
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AbsentViewModel : ViewModel() {

    private val composite = CompositeDisposable()
    private val dataRequestAbsent: MutableLiveData<RequestAbsent> = MutableLiveData()
    private val getOfficeLocation: MutableLiveData<GetOfficeLocation> = MutableLiveData()

    fun requestAbsentOffice(
        userId: String?,
        name: String?,
        typeAbsent: String?,
        image: String,
        address: String? = null,
        latitude: String? = null,
        longitude: String? = null,
        division: String?,
        noted: String? = null
    ): LiveData<RequestAbsent> {
        val file: File = object : File(image) {}
        val requestFile: RequestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body: MultipartBody.Part = MultipartBody.Part.createFormData("image", file.name, requestFile)

        composite += Network.getRoutes(BaseUrl.BASE_URL)
            .requestAbsent(
                userId?.toRequestBody("text/plain".toMediaTypeOrNull()),
                name?.toRequestBody("text/plain".toMediaTypeOrNull()),
                typeAbsent?.toRequestBody("text/plain".toMediaTypeOrNull()),
                body,
                address?.toRequestBody("text/plain".toMediaTypeOrNull()),
                latitude?.toRequestBody("text/plain".toMediaTypeOrNull()),
                longitude?.toRequestBody("text/plain".toMediaTypeOrNull()),
                division?.toRequestBody("text/plain".toMediaTypeOrNull()),
                noted?.toRequestBody("text/plain".toMediaTypeOrNull()) ,
                getDeviceUniqueId(AbsentLangitPayAplication.getApplicationContext()).toRequestBody("text/plain".toMediaTypeOrNull())
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                dataRequestAbsent.postValue(it)
            }, {
                it.printStackTrace()
            })


        return dataRequestAbsent
    }

    fun getOfficeLocation() : LiveData<GetOfficeLocation> {

        composite += Network.getRoutes(BaseUrl.BASE_URL).getOfficeLocation()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                logi("offie location is -> ${it?.toJson()}")
                getOfficeLocation.postValue(it)
            },{
                it.printStackTrace()
            })
        return getOfficeLocation
    }
}