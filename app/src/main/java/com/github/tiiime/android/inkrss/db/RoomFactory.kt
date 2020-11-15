package com.github.tiiime.android.inkrss.db

import android.content.Context
import androidx.annotation.Nullable
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import java.lang.reflect.ParameterizedType

abstract class RoomFactory<Database : RoomDatabase> {
    @Volatile
    private lateinit var db: Database

    private val roomLock = Any()

    fun getDataBase(context: Context): Database {
        synchronized(roomLock) {
            if (::db.isInitialized.not() || !db.isOpen) {
                val builder: RoomDatabase.Builder<Database> = Room.databaseBuilder(
                    context.applicationContext,
                    getDatabaseClass(), roomDbName()
                )
                if (deleteRoomIfMigrationNeeded()) {
                    builder.fallbackToDestructiveMigration()
                } else {
                    val migrations: Array<Migration> = addMigrations()
                    if (migrations.isNotEmpty()) {
                        builder.addMigrations(*migrations)
                    } else {
                        builder.fallbackToDestructiveMigration()
                    }
                }
                if (isAllowMainThreadQueries()) {
                    builder.allowMainThreadQueries()
                }
                db = try {
                    builder.build()
                } catch (e: Exception) {
                    e.printStackTrace()
                    builder.fallbackToDestructiveMigration()
                    builder.build()
                }
            }
        }
        return db
    }

    open fun close() {
        synchronized(roomLock) {
            if (::db.isInitialized && db.isOpen) {
                db.close()
            }
        }
    }

    protected open fun isAllowMainThreadQueries(): Boolean {
        return false
    }

    protected abstract fun roomDbName(): String

    protected abstract fun deleteRoomIfMigrationNeeded(): Boolean

    @Nullable
    protected fun addMigrations(): Array<Migration> = emptyArray()

    private fun getDatabaseClass(): Class<Database> {
        return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<Database>
    }
}