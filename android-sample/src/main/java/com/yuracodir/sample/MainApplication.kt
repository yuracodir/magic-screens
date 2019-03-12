package com.yuracodir.sample

import android.app.Application
import com.yuracodir.sample.di.mainModule
import org.koin.android.ext.android.startKoin

class MainApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    startKoin(this, listOf(mainModule))
  }
}