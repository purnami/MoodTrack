package com.example.moodtrack.di

import android.content.Context
import androidx.room.Room
import com.example.moodtrack.data.local.dao.MoodDao
import com.example.moodtrack.data.local.database.MoodDatabase
import com.example.moodtrack.data.local.preferences.UserPreferences
import com.example.moodtrack.data.remote.services.OpenAIService
import com.example.moodtrack.data.remote.services.SelfAssessmentApiService
import com.example.moodtrack.data.remote.services.YoutubeApiService
import com.example.moodtrack.data.repository.AuthRepository
import com.example.moodtrack.data.repository.MoodRepository
import com.example.moodtrack.data.repository.OpenAIRepository
import com.example.moodtrack.data.repository.RecommendationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        userPreferences: UserPreferences
    ): AuthRepository {
        return AuthRepository(auth, userPreferences)
    }

    @Provides
    @Singleton
    fun provideMoodDatabase(@ApplicationContext context: Context): MoodDatabase {
        return Room.databaseBuilder(
            context,
            MoodDatabase::class.java,
            "mood_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideMoodDao(database: MoodDatabase): MoodDao {
        return database.moodDao()
    }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                })
            }
        }
    }

    @Provides
    @Singleton
    fun provideYoutubeApiService(): YoutubeApiService {
        return YoutubeApiService()
    }

    @Provides
    @Singleton
    fun provideOpenAIService(client: HttpClient): OpenAIService {
        return OpenAIService(client)
    }

    @Provides
    @Singleton
    fun provideSelfAssessmentApiService(): SelfAssessmentApiService {
        return SelfAssessmentApiService()
    }

//    @Provides
//    fun provideSelfAssessmentApiService(): SelfAssessmentApiService {
//        return SelfAssessmentApiService()
//    }

    @Provides
    @Singleton
    fun provideMoodRepository(
        moodDao: MoodDao,
        firestore: FirebaseFirestore,
        userPreferences: UserPreferences
    ): MoodRepository {
        return MoodRepository(moodDao, firestore, userPreferences)
    }

    @Provides
    @Singleton
    fun provideRecommendationRepository(
        apiService: YoutubeApiService
    ): RecommendationRepository {
        return RecommendationRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideOpenAIRepository(
        openAIService: OpenAIService
    ): OpenAIRepository {
        return OpenAIRepository(openAIService)
    }

}
