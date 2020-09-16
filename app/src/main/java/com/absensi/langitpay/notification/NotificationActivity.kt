package com.absensi.langitpay.notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.absensi.langitpay.R
import com.absensi.langitpay.abstraction.onBack
import kotlinx.android.synthetic.main.activity_notification.*
import androidx.lifecycle.Observer
import com.absensi.langitpay.abstraction.NetworkState

class NotificationActivity : AppCompatActivity() {

    private val adapter by lazy {
        NotificationAdapter()
    }

    private val viewModel : NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        initView()
    }

    private fun initView(){
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBack()
        }
        rv_notification.layoutManager = LinearLayoutManager(this)
        rv_notification.adapter = adapter

        adapter.updateNetworkState(NetworkState.LOADING)
        viewModel.getNotification().observe(this, Observer {
            adapter.updateNetworkState(NetworkState.LOADED)
            if (it.data != null){
                adapter.updateList(it.data)
            }else{
                adapter.updateNetworkState(NetworkState.empty(it.message))
            }
        })
    }
}