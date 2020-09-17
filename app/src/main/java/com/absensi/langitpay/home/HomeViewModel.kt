package com.absensi.langitpay.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.absensi.langitpay.network.BaseUrl
import com.absensi.langitpay.network.Network
import com.absensi.langitpay.network.response.User
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers

class HomeViewModel : ViewModel() {

    private val composite = CompositeDisposable()

    private val dataUser: MutableLiveData<User> = MutableLiveData()

    fun getUser(): LiveData<User> {
        composite += Network.getRoutes(BaseUrl.BASE_URL).getUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                dataUser.postValue(it)
            }, {
                it.printStackTrace()
            })
        return dataUser
    }
}