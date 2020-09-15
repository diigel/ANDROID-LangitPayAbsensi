package com.absensi.langitpay.absen.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.absensi.langitpay.R
import com.absensi.langitpay.abstraction.BaseAdapter
import com.absensi.langitpay.abstraction.autoSize
import com.absensi.langitpay.abstraction.clicked
import com.absensi.langitpay.network.response.ResultSearch
import kotlinx.android.synthetic.main.item_list_address.view.*

class MarkAdapter : BaseAdapter<ResultSearch>() {

    private var itemClick: ((ResultSearch) -> Unit)? = null

    fun itemClick(itemClick: ((ResultSearch) -> Unit)?) {
        this.itemClick = itemClick
    }

    override fun createViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return MarkHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_address, parent, false)
        )
    }

    override fun enableAnimation(): Boolean = true

    override fun bindHolder(holder: RecyclerView.ViewHolder, item: ResultSearch, position: Int) {
        (holder as MarkHolder).bindItem(item, itemClick)
    }

    override fun bindShimmer(parent: ViewGroup): View? = null

}

class MarkHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bindItem(result: ResultSearch, itemClick: ((ResultSearch) -> Unit)?) = itemView.run {
        text_title.autoSize()
        text_address.autoSize()
        text_title.text = result.title
        text_address.text = HtmlCompat.fromHtml(result.vicinity ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)

        parent_item.clicked {
            itemClick?.invoke(result)
        }

    }
}