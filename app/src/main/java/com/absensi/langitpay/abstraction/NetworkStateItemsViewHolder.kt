package com.absensi.langitpay.abstraction

import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.absensi.langitpay.R
import com.facebook.shimmer.ShimmerFrameLayout

class NetworkStateItemsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(networkState: NetworkState?, shimmerLayout: View? = null) {
        itemView.run {
            logi("network is -> ${networkState?.message}")
            val imgNetwork = find<ImageView>(R.id.images_network)
            val progressBar = find<ProgressBar>(R.id.progress_bar)
            val textDesc = find<TextView>(R.id.text_lost_connection)
            val shimmer = find<ShimmerFrameLayout>(R.id.shimmer_frame)

            imgNetwork.loadResource(R.drawable.image_no_data)
            shimmer.visible()

            shimmerLayout?.let {
                shimmer.addView(it)
            }

            networkState.let {
                when (networkState?.status) {
                    Status.RUNNING -> {
                        imgNetwork.gone()
                        textDesc.gone()

                        if (shimmerLayout == null) {
                            progressBar.visible()
                            shimmer.gone()
                        } else {
                            progressBar.gone()
                            shimmer.visible()
                        }
                    }
                    Status.FAILED -> {
                        textDesc.visible()
                        imgNetwork.visible()
                        progressBar.gone()
                        imgNetwork.loadResource(R.drawable.image_server_error)
                        textDesc.text = networkState.message
                        shimmer.gone()
                    }
                    Status.ENDOFPAGE -> {
                        progressBar.gone()
                        imgNetwork.gone()
                        textDesc.visible()
                        textDesc.text = networkState.message
                        shimmer.gone()
                    }
                    Status.NULLKEYWORLD -> {
                        progressBar.gone()
                        imgNetwork.visible()
                        imgNetwork.loadResource(R.drawable.image_no_data)
                        textDesc.visible()
                        textDesc.text = networkState.message
                        shimmer.gone()
                    }
                    Status.EMPTY -> {
                        progressBar.gone()
                        imgNetwork.visible()
                        imgNetwork.loadResource(R.drawable.image_no_data)
                        textDesc.visible()
                        textDesc.text = networkState.message
                        shimmer.gone()
                    }
                    else -> {
                        textDesc.gone()
                        imgNetwork.gone()
                        progressBar.gone()
                        shimmer.gone()
                    }
                }
            }
        }
    }
}