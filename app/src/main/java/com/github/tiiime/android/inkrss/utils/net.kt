package com.github.tiiime.android.inkrss.utils

import com.github.tiiime.android.inkrss.service.RssService
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

fun createRssService(link: String): RssService = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .baseUrl(URL(link).let { "${it.protocol}://${it.authority}" })
    .build()
    .create(RssService::class.java)
