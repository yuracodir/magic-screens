package com.yuracodir.sample.di

import com.yuracodir.sample.data.network.TypicodeApiService
import org.koin.dsl.module.module

val mainModule = module {
  single {
    TypicodeApiService().get()
  }
}