package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        Producer::class,
        Product::class,
        Order::class,
        OrderItem::class,
        Favorite::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MarketplaceDatabase : RoomDatabase() {
    abstract fun marketplaceDao(): MarketplaceDao

    companion object {
        @Volatile
        private var INSTANCE: MarketplaceDatabase? = null

        fun getDatabase(context: Context): MarketplaceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MarketplaceDatabase::class.java,
                    "open_harvest_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
