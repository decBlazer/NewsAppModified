package com.loc.newsapp.di

import com.loc.newsapp.data.manger.LocalUserManagerImplementation
import com.loc.newsapp.domain.manager.LocalUserManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ManagerModule {

    @Binds
    @Singleton
    abstract fun bindLocalUserManager(localUserManagerImplementation: LocalUserManagerImplementation) : LocalUserManager
}