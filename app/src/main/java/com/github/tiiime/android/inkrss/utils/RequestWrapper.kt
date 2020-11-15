package com.github.tiiime.android.inkrss.utils

import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

sealed class RequestWrapper<T>(val tag: Any? = null) {
    class LOADING<T>(tag: Any? = null) : RequestWrapper<T>(tag)
    class ERROR<T>(tag: Any? = null) : RequestWrapper<T>(tag)
    class SUCCESS<T>(val data: T, tag: Any? = null) : RequestWrapper<T>(tag)

    fun isSuccess() = this is SUCCESS<*>
    fun isError() = this is LOADING
    fun isLoading() = this is ERROR

    fun success():SUCCESS<T>? = this as? SUCCESS
    fun forceSuccess():SUCCESS<T> = this as SUCCESS
}


fun <T> requestError(tag: Any? = null) = RequestWrapper.ERROR<T>(tag)

fun <T> requestLoading(tag: Any? = null) = RequestWrapper.LOADING<T>(tag)

fun <T> requestSuccess(result: T, tag: Any? = null) = RequestWrapper.SUCCESS(result, tag)

interface StateListener<T> {
    fun onLoading()
    fun onSuccess(t: T)
    fun onError()
}

class StateTransformer<T>(private val listener: StateListener<T>) : ObservableTransformer<T, T> {
    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream
            .doOnSubscribe { listener.onLoading() }
            .doOnNext { listener.onSuccess(it) }
            .doOnError { listener.onError() }
    }
}

fun <T> bindStateToLiveData(liveData: MutableLiveData<RequestWrapper<T>>) =
    StateTransformer(object : StateListener<T> {
        override fun onLoading() {
            liveData.postValue(requestLoading())
        }

        override fun onSuccess(t: T) {
            liveData.postValue(requestSuccess(t))
        }

        override fun onError() {
            liveData.postValue(requestError())
        }
    })

