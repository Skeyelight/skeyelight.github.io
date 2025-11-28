package com.example.dailyweighttracker_kotlin.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.dailyweighttracker_kotlin.data.room.*

// Defines the database
@Database(
    entities = [Weight::class, User::class, Goal::class],
    version = 6,
    exportSchema = false
)


@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // Connects the database to the DAOs.
    abstract fun weightDao(): WeightDao
    abstract fun userDao(): UserDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

       // Database Migration
        val MIGRATION_1_2 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE weight_table ADD COLUMN notes TEXT DEFAULT '' NOT NULL")
            }
        }

        // Creates database if database doesn't exist.
        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "daily_weight_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                Instance = instance
                instance
            }
        }
    }
}
