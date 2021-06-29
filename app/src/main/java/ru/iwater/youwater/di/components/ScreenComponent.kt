package ru.iwater.youwater.di.components

import dagger.Component

@OnScreen
@Component(dependencies = [AppComponent::class])
interface ScreenComponent {
}