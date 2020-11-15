package com.github.tiiime.android.inkrss.model

import com.google.gson.annotations.SerializedName

data class RssFeed(
    @SerializedName("title")
    var title: String = "",

    @SerializedName("description")
    var desc: String = "",

    @SerializedName("link")
    var url: String = ""
)
