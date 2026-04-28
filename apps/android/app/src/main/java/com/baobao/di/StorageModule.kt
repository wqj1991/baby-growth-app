package com.baobao.di

import android.content.Context
import androidx.room.Room
import com.baobao.data.local.db.BaobaoDatabase
import com.baobao.data.local.db.dao.CareLogDao
import com.baobao.data.local.db.dao.MediaItemDao
import com.baobao.data.local.db.dao.SyncTaskDao
import com.baobao.data.local.provider.LocalConfigStorage
import com.baobao.data.local.provider.LocalLogStorage
import com.baobao.data.local.provider.LocalMediaStorage
import com.baobao.data.local.provider.LocalSyncQueueStorage
import com.baobao.data.storage.IConfigStorage
import com.baobao.data.storage.ILogStorage
import com.baobao.data.storage.IMediaStorage
import com.baobao.data.storage.ISyncQueueStorage
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// ── 数据库 & 基础依赖 ──────────────────────────────────────────────────────
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BaobaoDatabase =
        Room.databaseBuilder(context, BaobaoDatabase::class.java, "baobao.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCareLogDao(db: BaobaoDatabase): CareLogDao = db.careLogDao()

    @Provides
    fun provideMediaItemDao(db: BaobaoDatabase): MediaItemDao = db.mediaItemDao()

    @Provides
    fun provideSyncTaskDao(db: BaobaoDatabase): SyncTaskDao = db.syncTaskDao()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()
}

// ── Phase 1 存储绑定（Phase 2 替换为 Quark* 实现即可）──────────────────────
@Module
@InstallIn(SingletonComponent::class)
abstract class StorageBindingModule {

    @Binds
    @Singleton
    abstract fun bindConfigStorage(impl: LocalConfigStorage): IConfigStorage

    @Binds
    @Singleton
    abstract fun bindLogStorage(impl: LocalLogStorage): ILogStorage

    @Binds
    @Singleton
    abstract fun bindMediaStorage(impl: LocalMediaStorage): IMediaStorage

    @Binds
    @Singleton
    abstract fun bindSyncQueueStorage(impl: LocalSyncQueueStorage): ISyncQueueStorage
}
