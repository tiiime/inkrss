package com.github.tiiime.android.inkrss.service

import com.github.tiiime.android.inkrss.model.RssResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Url

interface RssService {
    @GET
    fun getFeedList(@Url url: String): Observable<RssResponse>
}