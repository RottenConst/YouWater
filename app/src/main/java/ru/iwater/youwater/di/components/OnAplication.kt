package ru.iwater.youwater.di.components

import javax.inject.Scope

/**
 * кастомная аннотация приложения для выделения области видимости и ограничения жизни зависимостей
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class OnApplication