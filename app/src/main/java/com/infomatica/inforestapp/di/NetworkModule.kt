package com.infomatica.inforestapp.di

import android.content.Context
import com.infomatica.inforestapp.core.RetrofitHelper
import com.infomatica.inforestapp.data.network.CanalVentaApiClient
import com.infomatica.inforestapp.data.network.ClienteFacturadoApiClient
import com.infomatica.inforestapp.data.network.DocumentoApiClient
import com.infomatica.inforestapp.data.network.GrupoApiClient
import com.infomatica.inforestapp.data.network.ImpresionApiClient
import com.infomatica.inforestapp.data.network.ParametroApiClient
import com.infomatica.inforestapp.data.network.PedidoApiClient
import com.infomatica.inforestapp.data.network.ProductoApiClient
import com.infomatica.inforestapp.data.network.QuoteApiClient
import com.infomatica.inforestapp.data.network.SalonApiClient
import com.infomatica.inforestapp.data.network.TurnoApiClient
import com.infomatica.inforestapp.data.network.UsuarioApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideRetrofit(@ApplicationContext context: Context): Retrofit  {
//        return Retrofit.Builder()
//            .baseUrl("http://161.132.106.116/api/")
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()

        return RetrofitHelper.getRetrofitInstance(context)
    }
    @Singleton
    @Provides
    fun provideQuoteApiClient(retrofit: Retrofit): QuoteApiClient{
        return  retrofit.create(QuoteApiClient::class.java)
    }
    @Singleton
    @Provides
    fun provideSalonApiClient(retrofit: Retrofit): SalonApiClient{
        return  retrofit.create(SalonApiClient::class.java)
    }
    @Singleton
    @Provides
    fun provideGrupoApiClient(retrofit: Retrofit): GrupoApiClient{
        return  retrofit.create(GrupoApiClient::class.java)
    }
    @Singleton
    @Provides
    fun provideProductoApiClient(retrofit: Retrofit): ProductoApiClient{
        return  retrofit.create(ProductoApiClient::class.java)
    }
    @Singleton
    @Provides
    fun provideUsuarioApiClient(retrofit: Retrofit): UsuarioApiClient{
        return  retrofit.create(UsuarioApiClient::class.java)
    }
    @Singleton
    @Provides
    fun providePedidoApiClient(retrofit: Retrofit): PedidoApiClient{
        return  retrofit.create(PedidoApiClient::class.java)
    }
    @Singleton
    @Provides
    fun provideImpresionApiClient(retrofit: Retrofit): ImpresionApiClient{
        return  retrofit.create(ImpresionApiClient::class.java)
    }
    @Singleton
    @Provides
    fun provideParametroApiClient(retrofit: Retrofit): ParametroApiClient{
        return  retrofit.create(ParametroApiClient::class.java)
    }
    @Singleton
    @Provides
    fun provideClienteFacturadoApiClient(retrofit: Retrofit): ClienteFacturadoApiClient{
        return  retrofit.create(ClienteFacturadoApiClient::class.java)
    }
    @Singleton
    @Provides
    fun provideDocumentoApiClient(retrofit: Retrofit): DocumentoApiClient{
        return  retrofit.create(DocumentoApiClient::class.java)
    }
    @Singleton
    @Provides
    fun provideCanalVentaApiClient(retrofit: Retrofit): CanalVentaApiClient{
        return  retrofit.create(CanalVentaApiClient::class.java)
    }
    @Singleton
    @Provides
    fun provideTurnoApiClient(retrofit: Retrofit): TurnoApiClient{
        return  retrofit.create(TurnoApiClient::class.java)
    }
}