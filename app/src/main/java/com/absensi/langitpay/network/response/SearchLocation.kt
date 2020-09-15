package com.absensi.langitpay.network.response


import com.google.gson.annotations.SerializedName

data class SearchLocation(
    @SerializedName("results")
    val results: List<ResultSearch>
)

data class ResultSearch(
    @SerializedName("category")
    val category: String,
    @SerializedName("categoryTitle")
    val categoryTitle: String,
    @SerializedName("completion")
    val completion: String,
    @SerializedName("distance")
    val distance: Int,
    @SerializedName("highlightedTitle")
    val highlightedTitle: String,
    @SerializedName("highlightedVicinity")
    val highlightedVicinity: String,
    @SerializedName("href")
    val href: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("position")
    val position: List<Double>,
    @SerializedName("resultType")
    val resultType: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("vicinity")
    val vicinity: String? = null
)