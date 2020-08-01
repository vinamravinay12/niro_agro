package com.niro.niroapp.network

import android.content.Context
import com.niro.niroapp.utils.NiroAppUtils.getToken
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private var retrofit: Retrofit? = null

    // Get client object
    private fun getClient(context: Context?): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl("https://niroapp.df.r.appspot.com")
                .addConverterFactory(GsonConverterFactory.create())
                .client(getHttpClient(context))
                .build()
        }
        return retrofit
    }

    // Set client
    private fun getHttpClient(context: Context?): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", getToken(context!!))
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }


    fun getAPIInterface(context: Context?): ApiInterface? {
        return getClient(context)?.create(ApiInterface::class.java)
    }
}