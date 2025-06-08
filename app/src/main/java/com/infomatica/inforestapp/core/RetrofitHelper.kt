package com.infomatica.inforestapp.core

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import com.infomatica.inforestapp.BuildConfig
@Module
@InstallIn(SingletonComponent::class)
object RetrofitHelper {
    // Método para obtener Retrofit con la configuración de OkHttpClient incluida
    @Singleton
    fun getRetrofitInstance(context: Context): Retrofit {
        val baseUrl: String = try {
            LocalStore.getBaseUrl(context)
        } catch (ex: Exception) {
            BuildConfig.BASE_URL
        }

        val okHttpClient = createOkHttpClient()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Crear un OkHttpClient personalizado
    @Singleton
    fun createOkHttpClient(): OkHttpClient {
        // Crear un TrustManager que acepta todos los certificados
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        // Instalar el TrustManager que acepta todos los certificados
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        val sslSocketFactory = sslContext.socketFactory

        return OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()
    }
}
