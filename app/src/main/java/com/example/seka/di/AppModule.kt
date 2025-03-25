package com.example.seka.di

import android.content.Context
import androidx.work.WorkManager
import com.aallam.openai.client.OpenAI
import com.example.seka.data.local.dao.*
import com.example.seka.data.local.database.AppDatabase
import com.example.seka.data.repository.*
import com.example.seka.util.EnkripsiEngine
import com.example.seka.util.ReminderScheduler
import com.example.seka.util.api.OpenAIService
import com.example.seka.util.notification.NotificationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTodoDao(database: AppDatabase): TodoDao {
        return database.todoDao()
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideTabunganDao(database: AppDatabase): TabunganDao {
        return database.tabunganDao()
    }

    @Provides
    @Singleton
    fun provideKeuanganDao(database: AppDatabase): KeuanganDao {
        return database.keuanganDao()
    }

    @Provides
    @Singleton
    fun provideChatMessageDao(database: AppDatabase): ChatMessageDao {
        return database.chatMessageDao()
    }

    @Provides
    @Singleton
    fun provideAirMinumDao(database: AppDatabase): AirMinumDao {
        return database.airMinumDao()
    }

    @Provides
    @Singleton
    fun provideTodoRepository(todoDao: TodoDao): TodoRepository {
        return TodoRepository(todoDao)
    }

    @Provides
    @Singleton
    fun provideNoteRepository(noteDao: NoteDao): NoteRepository {
        return NoteRepository(noteDao)
    }

    @Provides
    @Singleton
    fun provideTabunganRepository(tabunganDao: TabunganDao): TabunganRepository {
        return TabunganRepository(tabunganDao)
    }

    @Provides
    @Singleton
    fun provideKeuanganRepository(keuanganDao: KeuanganDao): KeuanganRepository {
        return KeuanganRepository(keuanganDao)
    }

    @Provides
    @Singleton
    fun provideChatRepository(chatMessageDao: ChatMessageDao): ChatRepository {
        return ChatRepository(chatMessageDao)
    }

    @Provides
    @Singleton
    fun provideAirMinumRepository(airMinumDao: AirMinumDao): AirMinumRepository {
        return AirMinumRepository(airMinumDao)
    }

    @Provides
    @Singleton
    fun provideEnkripsiEngine(): EnkripsiEngine {
        return EnkripsiEngine()
    }

    @Provides
    @Singleton
    fun provideOpenAI(): OpenAI {
        return OpenAI(
            token = System.getenv("OPENAI_API_KEY") ?: "YOUR_OPENAI_API_KEY"
        )
    }

    @Provides
    @Singleton
    fun provideOpenAIService(openAI: OpenAI): OpenAIService {
        return OpenAIService(openAI)
    }

    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideReminderScheduler(workManager: WorkManager): ReminderScheduler {
        return ReminderScheduler(workManager)
    }
}