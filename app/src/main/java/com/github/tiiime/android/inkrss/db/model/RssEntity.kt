package com.github.tiiime.android.inkrss.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rss_source")
data class RssEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "source_id")
    var id: Int = 0,

    @ColumnInfo(name = "url")
    var url: String,

    @ColumnInfo(name = "name")
    var name: String
)