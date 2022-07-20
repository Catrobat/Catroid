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

package org.catrobat.catroid.test.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.asLiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.catrobat.catroid.db.AppDatabase
import org.catrobat.catroid.db.FeaturedProjectDao
import org.catrobat.catroid.db.ProjectsCategoryDao
import org.catrobat.catroid.retrofit.models.FeaturedProject
import org.catrobat.catroid.retrofit.models.ProjectCategoryWithResponses
import org.catrobat.catroid.retrofit.models.ProjectsCategoryApi
import org.catrobat.catroid.testsuites.annotations.Cat
import org.catrobat.catroid.utils.getOrAwaitValue
import org.catrobat.catroid.utils.toProjectCategoryWithResponsesList
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var memoryDB: AppDatabase
    private lateinit var featuredProjectDao: FeaturedProjectDao
    private lateinit var projectsCategoryDao: ProjectsCategoryDao
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        memoryDB = Room.inMemoryDatabaseBuilder(
            getApplicationContext<Context>(),
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        featuredProjectDao = memoryDB.featuredProjectDao()
        projectsCategoryDao = memoryDB.projectCategoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        memoryDB.close()
    }

    @Category(Cat.RoomDb::class)
    @Test
    fun insertReadAndDeleteFeaturedProjects() {
        val projects = generateFeaturedProjects()
        featuredProjectDao.insertFeaturedProjects(projects)

        var fetchedFeaturedProjects =
            featuredProjectDao.getFeaturedProjects()
                .asLiveData()
                .getOrAwaitValue()
        assertTrue(fetchedFeaturedProjects.size == 3)

        featuredProjectDao.deleteAll()
        fetchedFeaturedProjects = featuredProjectDao
            .getFeaturedProjects()
            .asLiveData()
            .getOrAwaitValue()
        assertTrue(fetchedFeaturedProjects.isEmpty())
    }

    @Category(Cat.RoomDb::class)
    @Test
    fun insertReadAndDeleteProjectsCategories() {
        val categories = generateProjectsCategories()
        projectsCategoryDao.insertProjectCategoriesWithResponses(categories)

        var fetchedProjectsCategories = projectsCategoryDao
            .getProjectsCategories()
            .asLiveData()
            .getOrAwaitValue()
        assertEquals(fetchedProjectsCategories.size, 4)
        assertEquals(fetchedProjectsCategories[0].projectsList.size, 20)
        assertEquals(fetchedProjectsCategories[1].projectsList.size, 20)
        assertEquals(fetchedProjectsCategories[2].projectsList.size, 20)
        assertEquals(fetchedProjectsCategories[3].projectsList.size, 20)

        projectsCategoryDao.nukeAll()

        fetchedProjectsCategories = projectsCategoryDao
            .getProjectsCategories()
            .asLiveData()
            .getOrAwaitValue()
        assertTrue(fetchedProjectsCategories.isEmpty())
    }

    private fun generateFeaturedProjects(): List<FeaturedProject> {
        val jsonString = context.loadJSONFromAssets(FEATURED_PROJECTS_RESPONSE)
        val typeToken = object : TypeToken<List<FeaturedProject>>() {}.type
        return Gson().fromJson(jsonString, typeToken)
    }

    private fun generateProjectsCategories(): List<ProjectCategoryWithResponses> {
        val jsonString = context.loadJSONFromAssets(PROJECT_CATEGORIES_RESPONSE)
        val typeToken = object : TypeToken<List<ProjectsCategoryApi>>() {}.type
        val categories: List<ProjectsCategoryApi> = Gson().fromJson(jsonString, typeToken)
        return categories.toProjectCategoryWithResponsesList()
    }

    private fun Context.loadJSONFromAssets(fileName: String): String {
        return assets.open(fileName).bufferedReader().use { reader ->
            reader.readText()
        }
    }

    companion object {
        private const val FEATURED_PROJECTS_RESPONSE = "featured_projects_success_response.json"
        private const val PROJECT_CATEGORIES_RESPONSE = "projects_categories_response.json"
    }
}
