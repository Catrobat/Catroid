/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import org.catrobat.catroid.retrofit.models.FeaturedProject
import org.catrobat.catroid.retrofit.models.ProjectsCategoryApi
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
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
        @Query("offset") offset: Int = 0
    ): Call<List<FeaturedProject>>

    @GET("projects/categories")
    fun getProjectCategories(
        @Query("max_version") maxVersion: String = CURRENT_CATROBAT_LANGUAGE_VERSION.toString(),
        @Query("flavor") flavor: String = FLAVOR_NAME
    ): Call<List<ProjectsCategoryApi>>
}

class CatroidWebServer private constructor() {
    companion object {
        @JvmStatic
        fun getWebService(baseUrl: String): WebService {
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
