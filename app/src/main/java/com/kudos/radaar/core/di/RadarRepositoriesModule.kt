package com.kudos.radaar.core.di

import android.content.Context
import com.kudos.radaar.core.data.repositories.RadarRepositoryImpl
import com.kudos.radaar.core.domain.BTHelper
import com.kudos.radaar.core.domain.repositories.RadarRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RadarRepositoriesModule {
    @Provides
    @Singleton
    fun provideRadarRepository(
        @ApplicationContext context: Context,
        btHelper: BTHelper
    ):RadarRepository = RadarRepositoryImpl(context, btHelper)
}