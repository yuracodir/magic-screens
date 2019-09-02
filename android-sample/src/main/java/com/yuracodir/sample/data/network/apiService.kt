package com.yuracodir.sample.data.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.yuracodir.sample.data.models.AlbumDto
import com.yuracodir.sample.data.models.PhotoDto
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TypicodeApi {
  @GET("albums")
  fun getAlbums(): Deferred<Response<List<AlbumDto>>>

  @GET("photos")
  fun getPhotos(@Query("albumId") albumId: Int): Deferred<Response<List<PhotoDto>>>
}

class TypicodeApiService {
  private val baseUrl = "https://jsonplaceholder.typicode.com"
  private val service: TypicodeApi = Retrofit.Builder()
      .baseUrl(baseUrl)
      .addConverterFactory(GsonConverterFactory.create())
      .addCallAdapterFactory(CoroutineCallAdapterFactory())
      .build()
      .create(TypicodeApi::class.java)

  fun get() = service
}