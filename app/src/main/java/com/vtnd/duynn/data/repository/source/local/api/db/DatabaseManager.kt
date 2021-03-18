package com.vtnd.duynn.data.repository.source.local.api.db

import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vtnd.duynn.data.repository.source.local.api.db.dao.UserDao
import retrofit2.Converter

/**
 * Created by duynn100198 on 3/17/21.
 */
@TypeConverters(Converter::class)
abstract class DatabaseManager : RoomDatabase(){

    abstract fun userDao(): UserDao
}
