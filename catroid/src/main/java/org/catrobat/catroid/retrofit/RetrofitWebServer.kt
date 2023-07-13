/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.retrofit

import okhttp3.ConnectionSpec
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.catrobat.catroid.common.Constants.CURRENT_CATROBAT_LANGUAGE_VERSION
import org.catrobat.catroid.common.Constants.RETROFIT_WRITE_TIMEOUT
import org.catrobat.catroid.common.FlavoredConstants.FLAVOR_NAME
import org.catrobat.catroid.retrofit.models.DeprecatedToken
import org.catrobat.catroid.retrofit.models.FeaturedProject
import org.catrobat.catroid.retrofit.models.LoginResponse
import org.catrobat.catroid.retrofit.models.LoginUser
import org.catrobat.catroid.retrofit.models.OAuthLogin
import org.catrobat.catroid.retrofit.models.ProjectUploadResponseApi
import org.catrobat.catroid.retrofit.models.ProjectResponse
import org.catrobat.catroid.retrofit.models.ProjectsCategoryApi
import org.catrobat.catroid.retrofit.models.RefreshToken
import org.catrobat.catroid.retrofit.models.RegisterUser
import org.catrobat.catroid.retrofit.models.Tag
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query
import java.util.Locale
import java.util.concurrent.TimeUnit

interface WebService {
    @GET("authentication")
    fun checkToken(
        @Header("Authorization") bearerToken: String
    ): Call<Void>

    @POST("authentication")
    fun login(
        @Header("Authorization") bearerToken: String,
        @Body user: LoginUser
    ): Call<LoginResponse>

    @DELETE("authentication")
    fun expireToken(
        @Header("Authorization") bearerToken: String,
        @Header("X-Refresh") refreshToken: String
    ): Call<Void>

    @POST("authentication/refresh")
    fun refreshToken(
        @Header("Authorization") bearerToken: String,
        @Body refreshToken: RefreshToken
    ): Call<LoginResponse>

    @POST("authentication/upgrade")
    fun upgradeToken(
        @Body uploadToken: DeprecatedToken
    ): Call<LoginResponse>

    @POST("authentication/oauth")
    fun oAuthLogin(
        @Body oAuthLogin: OAuthLogin
    ): Call<LoginResponse>

    @POST("user")
    fun register(
        @Body user: RegisterUser
    ): Call<LoginResponse>

    @DELETE("user")
    fun deleteUser(
        @Header("Authorization") bearerToken: String
    ): Call<Void>

    @GET("projects/featured")
    fun getFeaturedProjects(
        @Query("max_version") maxVersion: String = CURRENT_CATROBAT_LANGUAGE_VERSION.toString(),
        @Query("flavor") flavor: String = FLAVOR_NAME,
        @Query("platform") platform: String = "android",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): Call<List<FeaturedProject>>

    @GET("projects/categories")
    fun getProjectCategories(
        @Query("max_version") maxVersion: String = CURRENT_CATROBAT_LANGUAGE_VERSION.toString(),
        @Query("flavor") flavor: String = FLAVOR_NAME
    ): Call<List<ProjectsCategoryApi>>

    @GET("projects/tags")
    fun getTags(): Call<List<Tag>>

    @Multipart
    @POST("projects")
    fun uploadProject(
        @Header("Authorization") bearerToken: String,
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part projectFile: MultipartBody.Part
    ): Call<ProjectUploadResponseApi>

    @SuppressWarnings("LongParameterList")
    @GET("projects/user")
    fun getUserProjects(
        @Header("Authorization") bearerToken: String,
        @Query("max_version") maxVersion: String = CURRENT_CATROBAT_LANGUAGE_VERSION.toString(),
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("attributes") attributes: String = "id,name,description",
        @Query("flavor") flavor: String = FLAVOR_NAME
    ): Call<List<ProjectResponse>>
}

class CatroidWebServer private constructor() {
    companion object {
        @JvmStatic
        fun getWebService(baseUrl: String): WebService {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .writeTimeout(RETROFIT_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS))
                .addInterceptor { chain ->
                    val lang = Locale.getDefault().language
                    val request = chain.request()
                        .newBuilder()
                        .addHeader("Accept-Language", lang)
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(ErrorInterceptor())
                .addInterceptor(loggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(WebService::class.java)
        }
    }
}
