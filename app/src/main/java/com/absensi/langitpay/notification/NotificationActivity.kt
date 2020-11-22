package com.absensi.langitpay.notification

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.absensi.langitpay.R
import com.absensi.langitpay.abstraction.*
import com.absensi.langitpay.network.SharedPref
import com.absensi.langitpay.network.response.DataNotification
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onCancel
import com.afollestad.materialdialogs.datetime.datePicker
import kotlinx.android.synthetic.main.activity_notification.*
import java.text.SimpleDateFormat
import java.util.*

class NotificationActivity : AppCompatActivity() {

    private val adapter by lazy {
        NotificationAdapter()
    }

    private val viewModel: NotificationViewModel by viewModels()
    private var dateState: Calendar? = null
    @SuppressLint("SimpleDateFormat")
    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd")

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
                    filterList(it.data)
                    adapter.notifyDataSetChanged()
                } else {
                    adapter.updateNetworkState(NetworkState.empty(it.message))
                }

            })
    }

    private fun filterList(data: MutableList<DataNotification>) {
        img_pick_date.clicked {
            MaterialDialog(this).show {
                cancelable(false)
                datePicker(currentDate = dateState) { dialog, datetime ->
                    dateState = datetime
                    if (dateState?.time != null) {
                        val newList: MutableList<DataNotification> = mutableListOf()
                        newList.addAll(data.filterIndexed { index, dataNotification ->
                            dataNotification.updatedAt.getApiDateFormat().contains(apiDateFormat.format(dateState?.time ?: ""))
                        })

                        logi("newlist is -> ${newList.toJson()}")
                        adapter.filterList(newList, whenFilter = {
                                if (newList.isNullOrEmpty()) {
                                    adapter.updateNetworkState(NetworkState.empty("data tidak di temukan"))
                                } else {
                                    adapter.updateNetworkState(NetworkState.LOADED)
                                }
                            })

                    }

                    onCancel {
                        dateState = datetime
                    }
                }
            }
        }
    }
}