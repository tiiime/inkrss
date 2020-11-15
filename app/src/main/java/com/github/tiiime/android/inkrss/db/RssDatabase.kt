package com.github.tiiime.android.inkrss.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.tiiime.android.inkrss.db.dao.RssDao
import com.github.tiiime.android.inkrss.db.model.RssEntity

@Database(
    entities = [
        RssEntity::class
    ],
    version = 1
)
abstract class RssDatabase : RoomDatabase() {

    abstract fun rssDao(): RssDao

}