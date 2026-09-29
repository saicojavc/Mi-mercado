package com.saico.mimercado.core.datastore.di

import com.saico.mimercado.core.datastore.ThemePreferencesRepositoryImpl
import com.saico.mimercado.core.domain.repository.ThemePreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    @Singleton
    abstract fun bindThemePreferencesRepository(
        impl: ThemePreferencesRepositoryImpl
    ): ThemePreferencesRepository
}
