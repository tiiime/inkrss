package com.github.tiiime.android.inkrss.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ClickableText
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Text
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.annotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import com.github.tiiime.android.inkrss.R
import com.github.tiiime.android.inkrss.db.RssDataBaseFactory
import com.github.tiiime.android.inkrss.db.model.RssEntity
import com.github.tiiime.android.inkrss.utils.launch

class MainActivity : AppCompatActivity() {
    private val db by lazy { RssDataBaseFactory.getDataBase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = findViewById<ComposeView>(R.id.rss_list)
        val addButton = findViewById<View>(R.id.add)
        val title = findViewById<View>(R.id.title)

        addButton.setOnClickListener { launch<RssSourceEditActivity>() }
        title.setOnClickListener { finish() }

        list.setContent {
            RssList(
                list = db.rssDao().getRssSourceList(),
                launch = { rssEntity ->
                    RssFeedsActivity.start(
                        this,
                        rssEntity.name,
                        rssEntity.url
                    )
                },
                edit = { entity -> RssSourceEditActivity.edit(this, entity) }
            )
        }
    }
}

@Composable
fun RssList(
    list: LiveData<List<RssEntity>>,
    launch: (RssEntity) -> Unit,
    edit: (RssEntity) -> Unit
) {
    val list: List<RssEntity> by list.observeAsState(emptyList())

    LazyColumnFor(items = list) { item ->
        RssRow(rssEntity = item, launch, edit)
        Divider()
    }
}


@Composable
fun RssRow(rssEntity: RssEntity, launch: (RssEntity) -> Unit, edit: (RssEntity) -> Unit) {
    Row(modifier = Modifier.clickable(onClick = {
        launch(rssEntity)
    }).fillMaxWidth()) {
        Text(
            text = rssEntity.name,
            fontSize = 16.sp,
            modifier = Modifier.weight(1F).padding(vertical = 20.dp).padding(start = 20.dp)
        )
        Text(
            text = "edit", modifier = Modifier.fillMaxHeight()
                .clickable(onClick = { edit(rssEntity) })
                .padding(20.dp)
        )
    }
}