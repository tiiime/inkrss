package com.github.tiiime.android.inkrss.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ClickableText
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.ui.tooling.preview.Preview
import com.github.tiiime.android.inkrss.model.RssFeed
import com.github.tiiime.android.inkrss.model.RssResponse
import com.github.tiiime.android.inkrss.service.RssService
import com.github.tiiime.android.inkrss.ui.theme.InkRssTheme
import com.github.tiiime.android.inkrss.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class RssFeedsActivity : AppCompatActivity() {
    private val link by lazy { intent.getStringExtra(EXTRA_LINK)!! }
    private val name by lazy { intent.getStringExtra(EXTRA_NAME)!! }

    private val service: RssService by lazy { createRssService(link) }

    private val feedLiveData: MutableLiveData<RequestWrapper<RssResponse>> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            InkRssTheme {
                Surface {
                    FeedScreen(feedLiveData, ::finish, ::refresh, ::openUrl)
                }
            }
        }

        refresh()
    }

    // TODO: bind lifecycle
    private fun refresh() = service.getFeedList(link)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindStateToLiveData(feedLiveData))
        .subscribe({
            println("success")
        }, {
            it.printStackTrace()
        })


    private fun openUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    companion object {
        private const val EXTRA_NAME = "EXTRA_NAME"
        private const val EXTRA_LINK = "EXTRA_LINK"

        fun start(activity: Activity, name: String, link: String) =
            activity.launch<RssFeedsActivity> {
                this.putString(EXTRA_LINK, link)
                this.putString(EXTRA_NAME, name)
            }
    }
}

@Composable
fun FeedScreen(
    wrapper: LiveData<RequestWrapper<RssResponse>>,
    close: () -> Unit,
    refresh: () -> Unit,
    openUrl: (String) -> Unit
) {
    val request by wrapper.observeAsState(requestLoading())

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            elevation = 0.dp,
            title = {
                ClickableText(text = AnnotatedString("back"), onClick = {
                    close()
                })
            },
        )
        when (request) {
            is RequestWrapper.LOADING -> Box(modifier = Modifier.fillMaxSize()) {
                Text(text = "Loading", modifier = Modifier.align(Alignment.Center))
            }
            is RequestWrapper.ERROR -> Box(
                modifier = Modifier.fillMaxSize().clickable(onClick = refresh)
            ) {
                Text(text = "ClickRetry", modifier = Modifier.align(Alignment.Center))
            }
            is RequestWrapper.SUCCESS -> FeedList(request.forceSuccess().data.list, openUrl)
        }
    }
}

@Composable
fun FeedList(list: List<RssFeed>, openUrl: (String) -> Unit) {
    LazyColumnFor(
        items = list, modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(20.dp)
    ) { item ->
        FeedItem(feed = item, openUrl)
    }
}

@Composable
fun FeedItem(feed: RssFeed, openUrl: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().clickable(onClick = { openUrl(feed.url) })) {
        Text(
            text = feed.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = feed.desc,
            fontSize = 16.sp,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        )
        Divider(modifier = Modifier.padding(vertical = 20.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    InkRssTheme {
        FeedList(
            listOf(RssFeed("title", "desc"))
        ) {

        }
    }
}