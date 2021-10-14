package ru.iwater.youwater.iteractor

import ru.iwater.youwater.data.AuthClient

interface Storage<Type> {

    fun save(data: Type)

    fun get (): Type

    fun remove()
}

interface StorageStateAuthClient: Storage<AuthClient>