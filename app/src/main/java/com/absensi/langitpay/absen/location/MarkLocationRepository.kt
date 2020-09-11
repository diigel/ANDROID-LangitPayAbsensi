package com.absensi.langitpay.absen.location

import com.absensi.langitpay.abstraction.logi
import com.absensi.langitpay.network.Network
import com.absensi.langitpay.network.response.GetLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class MarkLocationRepository(private val composite: CompositeDisposable) {

    fun getLocation(at: String,key : String, result: (GetLocation) -> Unit) {
        composite += Network.getRoutes(maps = true)
            .getLocation(at,key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    result.invoke(it)
                } else {
                    logi("Gagal Get Locaton -> $it")
                }
            }, {
                it.printStackTrace()
            })
    }
}