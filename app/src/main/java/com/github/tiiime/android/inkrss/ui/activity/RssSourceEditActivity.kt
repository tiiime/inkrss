package com.github.tiiime.android.inkrss.ui.activity

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ClickableText
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.github.tiiime.android.inkrss.db.RssDataBaseFactory
import com.github.tiiime.android.inkrss.db.model.RssEntity
import com.github.tiiime.android.inkrss.ui.theme.InkRssTheme
import com.github.tiiime.android.inkrss.utils.launch
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RssSourceEditActivity : AppCompatActivity(), Callback {
    private val editId: Int? by lazy { intent.getIntExtra(EXTRA_EDIT_ID, -1).takeIf { it >= 0 } }
    private val initName: String by lazy { intent.getStringExtra(EXTRA_INIT_NAME) ?: "" }
    private val initUrl: String by lazy { intent.getStringExtra(EXTRA_INIT_URL) ?: "" }

    private val db by lazy { RssDataBaseFactory.getDataBase(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InkRssTheme {
                Surface {
                    AddRssSourcePanel(this, initName, initUrl)
                }
            }
        }
    }

    override fun close() = finish()

    override fun submit(name: String, url: String) {
        if (name.isBlank()) {
            Toast.makeText(this, "input name please", Toast.LENGTH_SHORT).show()
            return
        }
        val urlCheckPass = Patterns.WEB_URL.matcher(url).matches()

        if (!urlCheckPass) {
            Toast.makeText(this, "url not validate", Toast.LENGTH_SHORT).show()
            return
        }

        val id = editId

        // TODO: bind lifecycle
        val ignore = if (id == null) {
            db.rssDao().save(RssEntity(name = name, url = url))
        } else {
            db.rssDao().update(RssEntity(id = id, name = name, url = url))
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Toast.makeText(this, "succeed", Toast.LENGTH_SHORT).show()
                finish()
            }, {
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
            })
    }

    override fun getClipboardContent(): String {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return clipboardManager.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
    }

    companion object {
        private const val EXTRA_EDIT_ID = "EXTRA_EDIT_ID"
        private const val EXTRA_INIT_NAME = "EXTRA_INIT_NAME"
        private const val EXTRA_INIT_URL = "EXTRA_INIT_URL"

        fun edit(activity: Activity, rssEntity: RssEntity) {
            activity.launch<RssSourceEditActivity> {
                putInt(EXTRA_EDIT_ID, rssEntity.id)
                putString(EXTRA_INIT_NAME, rssEntity.name)
                putString(EXTRA_INIT_URL, rssEntity.url)
            }
        }
    }
}

private interface Callback {
    fun close()
    fun submit(name: String, url: String)
    fun getClipboardContent(): String
}

@Composable
private fun AddRssSourcePanel(callback: Callback? = null, initName: String, initUrl: String) {
    val urlState = remember { mutableStateOf(TextFieldValue(initUrl)) }
    val nameState = remember { mutableStateOf(TextFieldValue(initName)) }

    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {
        TopAppBar(
            elevation = 0.dp,
            title = {
                ClickableText(text = AnnotatedString("back"), onClick = {
                    callback?.close()
                })
            },
            actions = {
                ClickableText(onClick = {
                    callback?.submit(nameState.value.text, urlState.value.text)
                }, text = AnnotatedString("submit"))
            }
        )
        TextField(
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "input name here") },
            value = nameState.value,
            onValueChange = { nameState.value = it }
        )
        Button(
            onClick = {
                nameState.value = TextFieldValue(callback?.getClipboardContent() ?: "")
            },
        ) {
            Text(text = "paste name")
        }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = "input link here") },
            value = urlState.value,
            onValueChange = { urlState.value = it }
        )
        Button(
            onClick = {
                urlState.value = TextFieldValue(callback?.getClipboardContent() ?: "")
            },
        ) {
            Text(text = "paste link")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    InkRssTheme {
        AddRssSourcePanel(initName = "hello", initUrl = "")
    }
}