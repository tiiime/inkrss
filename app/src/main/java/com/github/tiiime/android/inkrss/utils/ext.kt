package com.github.tiiime.android.inkrss.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle

inline fun <reified T : Activity> Activity.launch(
    extra: (Bundle.() -> Unit) = {}
) = startActivity(Intent(this, T::class.java).apply { putExtras(Bundle().apply(extra)) })
