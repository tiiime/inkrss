package com.github.tiiime.android.inkrss.model

import com.google.gson.annotations.SerializedName

data class RssResponse(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val desc: String,

    @SerializedName("items")
    val list: List<RssFeed>
)