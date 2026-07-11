/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026  The Catrobat Team
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

package org.catrobat.catroid.uiespresso.ui.fragment

import android.content.Context
import android.preference.PreferenceManager
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ScrollToAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.actionWithAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.CATROBAT_TERMS_OF_USE_ACCEPTED
import org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION
import org.catrobat.catroid.db.AppDatabase
import org.catrobat.catroid.retrofit.CatroidWebServer
import org.catrobat.catroid.retrofit.WebService
import org.catrobat.catroid.retrofittesting.MockResponseFileReader
import org.catrobat.catroid.sync.DefaultFeaturedProjectSync
import org.catrobat.catroid.sync.DefaultProjectsCategoriesSync
import org.catrobat.catroid.sync.FeaturedProjectsSync
import org.catrobat.catroid.sync.ProjectsCategoriesSync
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.ui.recyclerview.adapter.CategoriesAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.FeaturedProjectsAdapter
import org.catrobat.catroid.uiespresso.util.actions.CustomActions
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.catrobat.catroid.utils.NetworkConnectionMonitor
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.HttpURLConnection

@RunWith(AndroidJUnit4::class)
class MainMenuFragmentTest : KoinTest {

    companion object {
        private const val FEATURED_PROJECTS_FIXTURE = "featured_projects_success_response.json"
        private const val PROJECTS_CATEGORIES_FIXTURE = "projects_categories_response.json"
    }

    private var privacyPreferenceSetting: Int = 0
    private lateinit var applicationContext: Context
    private lateinit var mockWebServer: MockWebServer
    private val testKoinModule = module {
        single<WebService>(override = true) { buildCleartextWebService() }
        single<FeaturedProjectsSync>(override = true) {
            DefaultFeaturedProjectSync(get(), get(), get())
        }
        single<ProjectsCategoriesSync>(override = true) {
            DefaultProjectsCategoriesSync(get(), get(), get())
        }
    }

    private val connectionMonitor: NetworkConnectionMonitor by inject()
    private val appDatabase: AppDatabase by inject()
    private val projectsCategoriesSync: ProjectsCategoriesSync by inject()
    private val featuredProjectsSync: FeaturedProjectsSync by inject()
    private val featuredProjectsAdapter: FeaturedProjectsAdapter by inject()
    private val categoriesAdapter: CategoriesAdapter by inject()
    private val projectManager: ProjectManager by inject()

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        MainMenuActivity::class.java,
        false,
        false
    )

    @Before
    fun setUp() {
        applicationContext = ApplicationProvider.getApplicationContext()
        privacyPreferenceSetting = PreferenceManager
            .getDefaultSharedPreferences(applicationContext)
            .getInt(AGREED_TO_PRIVACY_POLICY_VERSION, 0)

        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit().putInt(
                AGREED_TO_PRIVACY_POLICY_VERSION,
                CATROBAT_TERMS_OF_USE_ACCEPTED
            ).commit()

        startMockServer()
        loadKoinModules(testKoinModule)

        createProject()
    }

    @After
    fun tearDown() {
        unloadKoinModules(testKoinModule)
        mockWebServer.shutdown()
        TestUtils.deleteProjects(javaClass.simpleName)
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit()
            .putInt(AGREED_TO_PRIVACY_POLICY_VERSION, privacyPreferenceSetting)
            .commit()
    }

    @Test
    fun testCategoriesSectionIsDisplayed() {
        syncBeforeLaunch()
        onView(withId(R.id.categoriesRecyclerView))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        assumeTrue("seems there is no internet connection", categoriesAdapter.itemCount > 0)
    }

    @Test
    fun testCatrobatCommunitySectionIsDisplayed() {
        syncBeforeLaunch()
        onView(withId(R.id.exploreShareTextView))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))

        assumeTrue("no featured projects available", featuredProjectsAdapter.itemCount > 0)

        onView(withId(R.id.featuredProjectsRecyclerView))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testHelpIsDisplayed() {
        syncBeforeLaunch(false)
        onView(withId(R.id.menu_help))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
    }

    @Test
    fun testDoesNotShowNoInternetMsg() {
        syncBeforeLaunch()
        connectionMonitor.setValueTo(true)
        waitFor()
        onView(withId(R.id.noInternetLayout))
            .check(matches(not(isDisplayed())))
        onView(withText(R.string.no_internet_connection))
            .check(matches(not(isDisplayed())))

        onView(withId(R.id.featuredProjectsRecyclerView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testShowNoInternetMsg() {
        appDatabase.featuredProjectDao().deleteAll()
        appDatabase.projectCategoryDao().nukeAll()
        syncBeforeLaunch(false)
        connectionMonitor.setValueTo(false)

        onView(withId(R.id.featuredProjectsRecyclerView))
            .check(matches(not(isDisplayed())))

        onView(withId(R.id.categoriesRecyclerView))
            .check(matches(not(isDisplayed())))

        onView(withId(R.id.noInternetLayout))
            .perform(scrollTo(), CustomActions.wait(900))
            .check(matches(isDisplayed()))

        onView(withText(R.string.no_internet_connection))
            .check(matches(isDisplayed()))

        connectionMonitor.setValueTo(true)
    }

    @Test
    fun testBackButtonAfterTappingOnPlayButton() {
        syncBeforeLaunch(false)
        onView(withId(R.id.playProject))
            .perform(ViewActions.click())
        pressBack()
        pressBack()
        onView(withId(R.id.projectImageView))
            .check(matches(isDisplayed()))
    }

    private fun createProject() {
        projectManager.createNewEmptyProject(
            javaClass.simpleName,
            false,
            false
        )
    }

    private fun waitFor(time: Int = 1000) {
        onView(isRoot()).perform(CustomActions.wait(time))
    }

    private fun scrollTo(): ViewAction = actionWithAssertions(NestedScrollViewScrollToAction())
    class NestedScrollViewScrollToAction(private val action: ScrollToAction = ScrollToAction()) :
        ViewAction by action {
        override fun getConstraints(): Matcher<View> {
            return anyOf(
                allOf(
                    withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE),
                    isDescendantOfA(isAssignableFrom(NestedScrollView::class.java))
                ),
                action.constraints
            )
        }
    }

    private fun syncBeforeLaunch(triggerSync: Boolean = true) {
        if (triggerSync) {
            connectionMonitor.setValueTo(true)
            featuredProjectsSync.sync(true)
            projectsCategoriesSync.sync(true)
        }
        baseActivityTestRule.launchActivity(null)
    }

    private fun startMockServer() {
        mockWebServer = MockWebServer()
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val path = request.path.orEmpty()
                return when {
                    path.startsWith("/projects/featured") ->
                        okResponse(FEATURED_PROJECTS_FIXTURE)
                    path.startsWith("/projects/categories") ->
                        okResponse(PROJECTS_CATEGORIES_FIXTURE)
                    else -> MockResponse().setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                }
            }
        }
        mockWebServer.start()
    }

    private fun okResponse(fixtureFile: String): MockResponse {
        val instrumentationContext = InstrumentationRegistry.getInstrumentation().context
        return MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(MockResponseFileReader(instrumentationContext, fixtureFile).content)
    }

    private fun buildCleartextWebService(): WebService =
        Retrofit.Builder()
            .baseUrl(mockWebServer.url("/").toString())
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(MoshiConverterFactory.create(CatroidWebServer.moshi))
            .build()
            .create(WebService::class.java)
}
