package com.github.tiiime.android.inkrss.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.tiiime.android.inkrss.db.model.RssEntity
import io.reactivex.Completable

@Dao
interface RssDao {
    @Query("select * from RSS_SOURCE")
    fun getRssSourceList(): LiveData<List<RssEntity>>

    @Insert
    fun save(rssEntity: RssEntity): Completable

    @Update
    fun update(rssEntity: RssEntity):Completable
}