package ru.iwater.youwater.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import ru.iwater.youwater.utils.Logger

/**
 * Базовая активность приложения от которой наследуются все остальные
 **/
open class BaseActivity : ComponentActivity() {
    private lateinit var logger: Logger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger = Logger()
        lifecycle.addObserver(logger)
    }
}