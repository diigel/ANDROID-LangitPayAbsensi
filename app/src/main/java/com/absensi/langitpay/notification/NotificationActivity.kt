package com.absensi.langitpay.notification

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.absensi.langitpay.R
import com.absensi.langitpay.abstraction.NetworkState
import com.absensi.langitpay.abstraction.onBack
import com.absensi.langitpay.network.SharedPref
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {

    private val adapter by lazy {
        NotificationAdapter()
    }

    private val viewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        initView()
    }

    private fun initView() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBack()
        }
        rv_notification.layoutManager = LinearLayoutManager(this)
        rv_notification.adapter = adapter

        adapter.updateNetworkState(NetworkState.LOADING)
        viewModel.getNotification(SharedPref.getValue(resources.getString(R.string.pref_id_user)))
            .observe(this, Observer {
                adapter.updateNetworkState(NetworkState.LOADED)
                if (it.data != null) {
                    adapter.updateList(it.data)
                } else {
                    adapter.updateNetworkState(NetworkState.empty(it.message))
                }
            })
    }
}