package com.absensi.langitpay.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.absensi.langitpay.R
import com.absensi.langitpay.abstraction.BaseAdapter
import com.absensi.langitpay.network.response.DataNotification
import com.absensi.langitpay.network.response.Notification

class NotificationAdapter : BaseAdapter<DataNotification>() {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
       return NotificationHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_address,parent,false))
    }

    override fun enableAnimation(): Boolean = true

    override fun bindHolder(holder: RecyclerView.ViewHolder, item: DataNotification, position: Int) {
        (holder as NotificationHolder).bindView(item)
    }

    override fun bindShimmer(parent: ViewGroup): View? {
        return null
    }
}

class NotificationHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bindView(data : DataNotification) = itemView.run {

    }
}