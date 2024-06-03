package com.example.recylerv.di

import android.app.Application
import androidx.room.Room
import com.example.recylerv.data.ContactsDatabase
import com.example.recylerv.ui.contacts.ContactsAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: ContactsDatabase.Callback
    ) = Room.databaseBuilder(app, ContactsDatabase::class.java, "contacts_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun provideContactsDao(db: ContactsDatabase) = db.contactsDoa()

    @ApplicationScope
    @Singleton
    @Provides
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())


}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope