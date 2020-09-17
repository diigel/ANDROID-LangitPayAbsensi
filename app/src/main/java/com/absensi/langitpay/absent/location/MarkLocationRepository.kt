package com.absensi.langitpay.absent.location

import com.absensi.langitpay.network.BaseUrl
import com.absensi.langitpay.network.Network
import com.absensi.langitpay.network.response.GetLocation
import com.absensi.langitpay.network.response.SearchLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class MarkLocationRepository(private val composite: CompositeDisposable) {

    fun getLocation(at: String,key : String, result: (GetLocation?) -> Unit) {
        composite += Network.getRoutes(BaseUrl.MAPS_URL)
            .getLocation(at,key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                result.invoke(it)
            }, {
                result.invoke(null)
                it.printStackTrace()
            })
    }

    fun getSearchLocation(at: String,placeName : String,key: String,result: (SearchLocation?) -> Unit){
        composite += Network.getRoutes(BaseUrl.MAPS_SEARCH_URL)
            .getLocationSearch(at,placeName,key)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                result.invoke(it)
            },{
                result.invoke(null)
                it.printStackTrace()
            })
    }
}