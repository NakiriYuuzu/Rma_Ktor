package net.yuuzu.di

import net.yuuzu.data.model.user.UserDataSourceImpl
import net.yuuzu.data.model.user.UserDataSource
import net.yuuzu.common.DatabaseFactory
import net.yuuzu.data.model.customer.CustomerDataSource
import net.yuuzu.data.model.customer.CustomerDataSourceImpl
import net.yuuzu.data.model.image.ImageDataSource
import net.yuuzu.data.model.image.ImageDataSourceImpl
import net.yuuzu.data.model.project.ProjectDataSource
import net.yuuzu.data.model.project.ProjectDataSourceImpl
import net.yuuzu.data.model.schedule.ScheduleDataSource
import net.yuuzu.data.model.schedule.ScheduleDataSourceImpl
import org.koin.dsl.module

val databaseModule = module {
    single { DatabaseFactory.createDatabase() }
}

val sourceModule = module {
    single<UserDataSource> { UserDataSourceImpl(get()) }
    single<CustomerDataSource> { CustomerDataSourceImpl(get()) }
    single<ProjectDataSource> { ProjectDataSourceImpl(get()) }
    single<ScheduleDataSource> { ScheduleDataSourceImpl(get()) }
    single<ImageDataSource> { ImageDataSourceImpl(get()) }
}