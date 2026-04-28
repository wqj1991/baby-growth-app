package com.baobao.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.baobao.data.local.db.dao.CareLogDao
import com.baobao.data.local.db.dao.MediaItemDao
import com.baobao.data.local.db.dao.SyncTaskDao
import com.baobao.data.local.db.entity.CareLogEntity
import com.baobao.data.local.db.entity.MediaItemEntity
import com.baobao.data.local.db.entity.SyncTaskEntity

@Database(
    entities = [
        CareLogEntity::class,
        MediaItemEntity::class,
        SyncTaskEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class BaobaoDatabase : RoomDatabase() {
    abstract fun careLogDao(): CareLogDao
    abstract fun mediaItemDao(): MediaItemDao
    abstract fun syncTaskDao(): SyncTaskDao
}
