package com.duwna.alarmable.di

import android.content.Context
import android.os.Build
import com.duwna.alarmable.BuildConfig
import com.duwna.alarmable.data.api.WeatherService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val networkModule = module {
    single { provideOkHttpClient(get()) }
    single { provideRetrofit(get()) }
    single { provideWeatherService(get()) }
}

private fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
    .baseUrl(BuildConfig.OWM_BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

private fun provideWeatherService(retrofit: Retrofit): WeatherService {
    return retrofit.create(WeatherService::class.java)
}

private fun provideOkHttpClient(context: Context): OkHttpClient = OkHttpClient()
    .newBuilder()
    // add api key and app language in all queries
    .addInterceptor(Interceptor { chain ->
        val originalRequest = chain.request()

        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }

        val url = originalRequest.url.newBuilder()
            .addQueryParameter("appid", BuildConfig.OWM_API_KEY)
            .apply { if (locale != null) addQueryParameter("lang", locale.country) }
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(url)
            .build()
        chain.proceed(newRequest)
    })
    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    .build()