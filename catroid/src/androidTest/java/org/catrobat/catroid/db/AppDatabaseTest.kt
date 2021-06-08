/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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

package org.catrobat.catroid.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.catrobat.catroid.retrofit.models.FeaturedProject
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var memoryDB: AppDatabase
    private lateinit var featuredProjectDao: FeaturedProjectDao
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        memoryDB = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        featuredProjectDao = memoryDB.featuredProjectDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        memoryDB.close()
    }

    @Test
    fun insertReadAndDeleteFeaturedProjects() = runBlocking{

            val projects = generateFeaturedProjects()
            featuredProjectDao.insertFeaturedProjects(projects)

            var fetchedFeaturedProjects = featuredProjectDao.getFeaturedProjects().toList()
            assertTrue(fetchedFeaturedProjects.size == 3)

            featuredProjectDao.deleteAll()
            fetchedFeaturedProjects = featuredProjectDao.getFeaturedProjects().toList()
            assertTrue(fetchedFeaturedProjects.isEmpty())
    }

    private fun generateFeaturedProjects(): List<FeaturedProject> {
        return mutableListOf<FeaturedProject>()
            .apply {
                add(
                    FeaturedProject(
                        "58",
                        "74758",
                        "https://share.catrob.at/app/project/74758",
                        "Palmina and the Pirates",
                        "silverLining",
                        "https://share.catrob.at/resources/featured/featured_58.png"
                    )
                )
                add(
                    FeaturedProject(
                        "45",
                        "48404",
                        "https://share.catrob.at/app/project/48404",
                        "Magic and More",
                        "silverLining",
                        "https://share.catrob.at/resources/featured/featured_45.png"
                    )
                )

                add(
                    FeaturedProject(
                        "48",
                        "53658",
                        "https://share.catrob.at/app/project/53658",
                        "CatWalk",
                        "silverLining",
                        "https://share.catrob.at/resources/featured/featured_48.png"
                    )
                )
            }
    }
}
