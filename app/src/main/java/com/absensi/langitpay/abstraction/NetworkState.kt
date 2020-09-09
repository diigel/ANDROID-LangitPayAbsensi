package com.absensi.langitpay.abstraction

enum class Status {
    RUNNING,
    SUCCESS,
    FAILED,
    ENDOFPAGE,
    NULLKEYWORLD,
    EMPTY
}

class NetworkState(val status: Status,val message: String?) {

    companion object {
        val LOADED: NetworkState = NetworkState(Status.SUCCESS, "Berhasil")
        val LOADING: NetworkState = NetworkState(Status.RUNNING, "Berjalan")
        val ERROR: NetworkState = NetworkState(Status.FAILED, "Maaf Terjadi Kesalahann !")
        val END_OF_PAGE: NetworkState = NetworkState(Status.ENDOFPAGE, "\n-")
        val NULL_KEYWORD: NetworkState = NetworkState(Status.NULLKEYWORLD, "Produk yang kamu cari belum tersedia !")
        val EMPTY: NetworkState = NetworkState(Status.EMPTY, "Produk Kosong")
        fun empty(msg: String?): NetworkState = NetworkState(Status.EMPTY, msg)
        fun failed(msg: String?) = NetworkState(Status.FAILED, msg)
    }
}