package ru.iwater.youwater.di.components

import javax.inject.Scope

/**
 * кастомная аннотация для выделения области видимости и ограничения жизни зависимостей экрана
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class OnScreen