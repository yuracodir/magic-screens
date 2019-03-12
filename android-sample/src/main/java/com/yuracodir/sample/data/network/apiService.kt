package com.yuracodir.sample.data.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.yuracodir.sample.data.models.VacanciesResponse
import com.yuracodir.sample.data.models.VacancyDetailsDto
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HeadHunterApi {
  @GET("/vacancies")
  fun getVacancies(
    @Query("text") text: String,
    @Query("page") page: Int,
    @Query("per_page") perPage: Int): Deferred<Response<VacanciesResponse>>

  @GET("/vacancies/{id}")
  fun getVacancyDetails(@Path("id") id: Long): Deferred<Response<VacancyDetailsDto>>
}

class HeadHunterApiService {
  private val baseUrl = "https://api.hh.ru"
  private val service: HeadHunterApi = Retrofit.Builder()
      .baseUrl(baseUrl)
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(CoroutineCallAdapterFactory())
      .build()
      .create(HeadHunterApi::class.java)

  fun get() = service
}