package com.example.jvent.di

import android.app.Application
import androidx.room.Room
import com.example.jvent.local.EventDatabase
import com.example.jvent.data.repository.EventRepositoryImpl
import com.example.jvent.domain.repository.EventRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideEventDatabase(app: Application): EventDatabase {
        return Room.databaseBuilder(
            app,
            EventDatabase::class.java,
            "event_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideEventRepository(
        firestore: FirebaseFirestore
    ): EventRepository {
        return EventRepositoryImpl(firestore)
    }
}