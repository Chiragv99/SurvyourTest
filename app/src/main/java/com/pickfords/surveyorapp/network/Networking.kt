package com.pickfords.surveyorapp.network

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.GsonBuilder
import com.pickfords.surveyorapp.utils.AppConstants
import com.pickfords.surveyorapp.utils.Session
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

open class Networking(private val context: Context?) {
//    private var baseURL: String = "http://www.mocky.io/v2/" //Test
   // private var baseURL: String = "http://5.77.39.57:50091/api/" //Local
//    private var baseURL: String = "https://surveyorappapi.pickfords.com/api/" //Live

    companion object {
        /**
         * @param context
         * @return Instance of this class
         * create instance of this class
         */
        fun with(context: Context? = null): Networking {
            return Networking(context)
        }

        fun wrapParams(params: HashMap<String, *>): RequestBody {
            return RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                JSONObject(params).toString()
            )
        }
    }

    fun getServices(): APIInterface {
        val httpClient = OkHttpClient.Builder()
        httpClient.readTimeout(AppConstants.readTimeOut, TimeUnit.MINUTES)
        httpClient.connectTimeout(AppConstants.connectionTimeOut, TimeUnit.MINUTES)

        //Log
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClient.addInterceptor(logging)

        //GSON converter
        val gson = GsonBuilder()
            .registerTypeAdapterFactory(ItemTypeAdapterFactory())
            .create()

        return retrofit2.Retrofit.Builder()
            .baseUrl(AppConstants.baseURL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(getOkHttpClient(context))
            .build().create(APIInterface::class.java)
    }


    private fun getOkHttpClient(context: Context?): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(AppConstants.connectionTimeOut, TimeUnit.MINUTES)
            .readTimeout(AppConstants.readTimeOut, TimeUnit.MINUTES)
            .writeTimeout(AppConstants.readTimeOut, TimeUnit.MINUTES)
        val logging = HttpLoggingInterceptor()
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        // add logging as last interceptor
        // Todo to active default header active below code
        httpClient.addInterceptor(object : Interceptor {
            @SuppressLint("LongLogTag")
            override fun intercept(chain: Interceptor.Chain): Response {
                val session = Session(context!!)
                val request: Request = if (session.isLoggedIn) {
                    chain.request().newBuilder()
                        .header("Authorization", "Bearer " + session.user!!.jWTToken)
                        .build()
                } else {
                    chain.request().newBuilder()
                        .build()

                }
                return chain.proceed(request)
            }
        })
        httpClient.addInterceptor(logging)
        return httpClient.build()
    }
}

