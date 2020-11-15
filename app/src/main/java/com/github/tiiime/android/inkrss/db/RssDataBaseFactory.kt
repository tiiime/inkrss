package com.github.tiiime.android.inkrss.db

object RssDataBaseFactory : RoomFactory<RssDatabase>() {
    override fun roomDbName() = "rss.room"

    override fun deleteRoomIfMigrationNeeded() = false

    override fun isAllowMainThreadQueries() = false
}
