/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
import okhttp3.OkHttpClient
import org.catrobat.catroid.common.Constants.CURRENT_CATROBAT_LANGUAGE_VERSION
import org.catrobat.catroid.common.Constants.RETROFIT_WRITE_TIMEOUT
import org.catrobat.catroid.common.FlavoredConstants.FLAVOR_NAME
import org.catrobat.catroid.retrofit.models.CursorPaginatedResponse
import org.catrobat.catroid.retrofit.models.FeaturedProjectApi
import org.catrobat.catroid.retrofit.models.ProjectResponseApi
import org.catrobat.catroid.retrofit.models.ProjectUploadResponse
import org.catrobat.catroid.retrofit.models.ProjectsCategoryListResponse
import org.catrobat.catroid.retrofit.models.MediaAssetResponse
import org.catrobat.catroid.retrofit.models.MediaCategoryPreview
import org.catrobat.catroid.retrofit.models.SurveyResponse
import org.catrobat.catroid.retrofit.models.TagsResponse
import retrofit2.Call
import retrofit2.Retrofit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Locale
import java.util.concurrent.TimeUnit

interface WebService {
    @GET("projects/featured")
    fun getFeaturedProjects(
        @Query("max_version") maxVersion: String = CURRENT_CATROBAT_LANGUAGE_VERSION.toString(),
        @Query("flavor") flavor: String = FLAVOR_NAME,
        @Query("platform") platform: String = "android",
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null
    ): Call<CursorPaginatedResponse<FeaturedProjectApi>>

    @GET("projects/categories")
    fun getProjectCategories(
        @Query("max_version") maxVersion: String = CURRENT_CATROBAT_LANGUAGE_VERSION.toString(),
        @Query("flavor") flavor: String = FLAVOR_NAME
    ): Call<ProjectsCategoryListResponse>

    @GET("projects")
    suspend fun getProjectsByCategory(
        @Query("category") category: String,
        @Query("max_version") maxVersion: String = CURRENT_CATROBAT_LANGUAGE_VERSION.toString(),
        @Query("flavor") flavor: String = FLAVOR_NAME,
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null
    ): CursorPaginatedResponse<ProjectResponseApi>

    @GET("projects/search")
    suspend fun searchProjects(
        @Query("query") query: String,
        @Query("max_version") maxVersion: String = CURRENT_CATROBAT_LANGUAGE_VERSION.toString(),
        @Query("flavor") flavor: String = FLAVOR_NAME,
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null
    ): CursorPaginatedResponse<ProjectResponseApi>

    @Multipart
    @POST("projects")
    fun uploadProject(
        @Part file: MultipartBody.Part,
        @Part("checksum") checksum: RequestBody,
        @Part("flavor") flavor: RequestBody? = null,
        @Part("private") private: RequestBody? = null,
        @Part("project_id") projectId: RequestBody? = null
    ): Call<ProjectUploadResponse>

    @GET("projects/tags")
    fun getProjectTags(): Call<TagsResponse>

    @GET("survey/{langCode}")
    fun getSurvey(
        @Path("langCode") langCode: String,
        @Query("platform") platform: String = "android"
    ): Call<SurveyResponse>

    @GET("media/library")
    suspend fun getMediaLibrary(
        @Query("file_type") fileType: String? = null,
        @Query("flavor") flavor: String? = null,
        @Query("search") search: String? = null,
        @Query("assets_per_category") assetsPerCategory: Int = 5,
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null
    ): CursorPaginatedResponse<MediaCategoryPreview>

    @GET("media/assets")
    suspend fun getMediaAssets(
        @Query("category_id") categoryId: String? = null,
        @Query("file_type") fileType: String? = null,
        @Query("flavor") flavor: String? = null,
        @Query("search") search: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("cursor") cursor: String? = null
    ): CursorPaginatedResponse<MediaAssetResponse>
}

class CatroidWebServer private constructor() {
    companion object {
        val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        private fun baseHttpClientBuilder(): OkHttpClient.Builder =
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(RETROFIT_WRITE_TIMEOUT, TimeUnit.SECONDS)
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

        @JvmStatic
        fun getWebService(
            baseUrl: String,
            additionalInterceptors: List<okhttp3.Interceptor> = emptyList()
        ): WebService {
            val builder = baseHttpClientBuilder()

            additionalInterceptors.forEach { builder.addInterceptor(it) }

            builder.addInterceptor(ErrorInterceptor())

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(builder.build())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(WebService::class.java)
        }

        @JvmStatic
        fun getAuthService(baseUrl: String): AuthService {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(baseHttpClientBuilder().build())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(AuthService::class.java)
        }
    }
}
