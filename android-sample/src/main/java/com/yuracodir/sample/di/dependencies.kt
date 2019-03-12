package com.yuracodir.sample.di

import com.yuracodir.sample.data.network.HeadHunterApiService
import org.koin.dsl.module.module

val mainModule = module {
  single {
    HeadHunterApiService().get()
  }
}