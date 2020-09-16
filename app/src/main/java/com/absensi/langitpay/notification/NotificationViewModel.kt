package com.absensi.langitpay.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.absensi.langitpay.network.BaseUrl
import com.absensi.langitpay.network.Network
import com.absensi.langitpay.network.response.Notification
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class NotificationViewModel : ViewModel() {

    private val composite = CompositeDisposable()
    private val dataNotification : MutableLiveData<Notification> = MutableLiveData()

    fun getNotification(): LiveData<Notification> {
        composite += Network.getRoutes(BaseUrl.BASE_URL).getNotification()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                dataNotification.postValue(it)
            },{
                it.printStackTrace()
            })

        return dataNotification
    }
}