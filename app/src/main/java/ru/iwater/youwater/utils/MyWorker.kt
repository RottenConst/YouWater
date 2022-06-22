package ru.iwater.youwater.utils

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import timber.log.Timber

class MyWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Timber.d("Performing long running task in scheduled job")
        return Result.success()
    }
}