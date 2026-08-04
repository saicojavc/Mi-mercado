package com.saico.mimercado.core.network.di

import com.saico.mimercado.core.domain.datasource.RemoteFavoriteDataSource
import com.saico.mimercado.core.domain.datasource.RemoteProductDataSource
import com.saico.mimercado.core.network.datasource.RemoteFavoriteDataSourceImpl
import com.saico.mimercado.core.network.datasource.RemoteProductDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindRemoteProductDataSource(
        impl: RemoteProductDataSourceImpl
    ): RemoteProductDataSource

    @Binds
    @Singleton
    abstract fun bindRemoteFavoriteDataSource(
        impl: RemoteFavoriteDataSourceImpl
    ): RemoteFavoriteDataSource
}
