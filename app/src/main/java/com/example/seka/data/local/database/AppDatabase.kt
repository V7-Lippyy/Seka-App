package com.example.seka.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.seka.data.local.dao.*
import com.example.seka.data.local.entity.*
import com.example.seka.util.Converters

@Database(
    entities = [
        TodoItem::class,
        NoteItem::class,
        TabunganItem::class,
        KeuanganItem::class,
        ChatMessageEntity::class,
        AirMinumItem::class
    ],
    version = 11, // Increment version number for new interval reminder fields
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun noteDao(): NoteDao
    abstract fun tabunganDao(): TabunganDao
    abstract fun keuanganDao(): KeuanganDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun airMinumDao(): AirMinumDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration untuk menambahkan kolom interval reminder
        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Tambahkan kolom baru untuk interval reminder
                database.execSQL("ALTER TABLE todo_items ADD COLUMN intervalReminder INTEGER NOT NULL DEFAULT 0")
                database.execSQL("ALTER TABLE todo_items ADD COLUMN intervalValue INTEGER NOT NULL DEFAULT 5")
                database.execSQL("ALTER TABLE todo_items ADD COLUMN intervalUnit TEXT NOT NULL DEFAULT 'MINUTES'")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "seka_database"
                )
                    .addMigrations(MIGRATION_9_10) // Tambahkan migration untuk mendukung upgrade tanpa kehilangan data
                    .fallbackToDestructiveMigration() // Tetap ada sebagai fallback jika migration gagal
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}