package com.kudos.radaar.core.di

import com.kudos.radaar.core.domain.BTHelper
import com.kudos.radaar.core.helper.BTHelperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object BTHelperModule {
    @Provides
    @Singleton
    fun provideBTHelper(): BTHelper = BTHelperImpl()
}