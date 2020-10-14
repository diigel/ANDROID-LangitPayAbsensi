package com.absensi.langitpay.notification

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.absensi.langitpay.R
import com.absensi.langitpay.abstraction.BaseAdapter
import com.absensi.langitpay.abstraction.apiToMonthDay
import com.absensi.langitpay.network.response.DataNotification
import kotlinx.android.synthetic.main.item_list_notification.view.*

class NotificationAdapter : BaseAdapter<DataNotification>() {

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
       return NotificationHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_list_notification,parent,false))
    }

    override fun enableAnimation(): Boolean = true

    override fun bindHolder(holder: RecyclerView.ViewHolder, item: DataNotification, position: Int) {
        (holder as NotificationHolder).bindView(item)
    }

    override fun bindShimmer(parent: ViewGroup): View? {
        return null
    }
}

@SuppressLint("SetTextI18n")
class NotificationHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bindView(data : DataNotification) = itemView.run {
        val date = data.createdAt.apiToMonthDay()
        text_date.text = "${data.status} - $date"
        text_title.text = data.title
        text_message.text = data.message
    }
}