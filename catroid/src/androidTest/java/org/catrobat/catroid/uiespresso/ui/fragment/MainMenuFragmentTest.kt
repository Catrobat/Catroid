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

package org.catrobat.catroid.uiespresso.ui.fragment

import android.app.Activity
import android.content.Context
import android.preference.PreferenceManager
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
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
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.CATROBAT_TERMS_OF_USE_ACCEPTED
import org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION
import org.catrobat.catroid.db.AppDatabase
import org.catrobat.catroid.retrofit.CatroidWebServer
import org.catrobat.catroid.retrofit.models.CursorPaginatedResponse
import org.catrobat.catroid.retrofit.models.FeaturedProjectApi
import org.catrobat.catroid.retrofit.models.ProjectsCategoryListResponse
import org.catrobat.catroid.retrofit.models.toRoomEntity
import org.catrobat.catroid.retrofittesting.MockResponseFileReader
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.ui.recyclerview.adapter.CategoriesAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.FeaturedProjectsAdapter
import org.catrobat.catroid.uiespresso.util.actions.CustomActions
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.catrobat.catroid.utils.NetworkConnectionMonitor
import org.catrobat.catroid.utils.toProjectCategoryWithResponsesList
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class MainMenuFragmentTest : KoinTest {

    companion object {
        private const val FEATURED_PROJECTS_FIXTURE = "featured_projects_success_response.json"
        private const val PROJECTS_CATEGORIES_FIXTURE = "projects_categories_response.json"
        private const val ACTIVITY_TIMEOUT_MS = 20_000L
        private const val POLL_INTERVAL_MS = 100L
    }

    private var privacyPreferenceSetting: Int = 0
    private lateinit var applicationContext: Context

    private val connectionMonitor: NetworkConnectionMonitor by inject()
    private val appDatabase: AppDatabase by inject()
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

        createProject()
    }

    @After
    fun tearDown() {
        // The monitor is a singleton, so a pinned offline state must not leak into the next test.
        connectionMonitor.clearForcedValue()
        connectionMonitor.setValueTo(true)
        connectionMonitor.clearForcedValue()
        appDatabase.featuredProjectDao().deleteAll()
        appDatabase.projectCategoryDao().nukeAll()
        TestUtils.deleteProjects(javaClass.simpleName)
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit()
            .putInt(AGREED_TO_PRIVACY_POLICY_VERSION, privacyPreferenceSetting)
            .commit()
    }

    @Test
    fun testCategoriesSectionIsDisplayed() {
        seedCommunityDataAndLaunch()
        assertTrue("no project categories to display", categoriesAdapter.itemCount > 0)
        onView(withId(R.id.categoriesRecyclerView))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCatrobatCommunitySectionIsDisplayed() {
        seedCommunityDataAndLaunch()
        onView(withId(R.id.exploreShareTextView))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))

        assertTrue("no featured projects to display", featuredProjectsAdapter.itemCount > 0)

        onView(withId(R.id.featuredProjectsRecyclerView))
            .perform(scrollTo())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testHelpIsDisplayed() {
        seedCommunityDataAndLaunch(false)
        onView(withId(R.id.menu_help))
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))
    }

    @Test
    fun testDoesNotShowNoInternetMsg() {
        seedCommunityDataAndLaunch()
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
        // The offline state has to be in place before the fragment subscribes to it, otherwise the
        // assertions race the observer that hides the community sections.
        connectionMonitor.setValueTo(false)
        seedCommunityDataAndLaunch(false)

        onView(withId(R.id.noInternetLayout))
            .perform(scrollTo())
            .check(matches(isDisplayed()))

        onView(withText(R.string.no_internet_connection))
            .check(matches(isDisplayed()))

        onView(withId(R.id.featuredProjectsRecyclerView))
            .check(matches(not(isDisplayed())))

        onView(withId(R.id.categoriesRecyclerView))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun testBackButtonAfterTappingOnPlayButton() {
        seedCommunityDataAndLaunch(false)
        onView(withId(R.id.playProject))
            .perform(ViewActions.click())
        waitForActivityInStage(StageActivity::class.java, Stage.RESUMED)

        // The stage renders continuously, so it never goes idle and Espresso cannot drive it.
        // The key events go through UiAutomator instead: the first back opens the stage dialog,
        // the second one leaves the stage. Waiting for the dialog in between keeps the second
        // press from outrunning the screenshot the first one triggers.
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressBack()
        val stageDialogBackButton = By.res(applicationContext.packageName, "stage_dialog_button_back")
        assertTrue(
            "the stage dialog did not open",
            device.wait(Until.hasObject(stageDialogBackButton), ACTIVITY_TIMEOUT_MS) == true
        )
        device.pressBack()
        waitForActivityInStage(MainMenuActivity::class.java, Stage.RESUMED)

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

    private fun storedFeaturedProjectsCount(): Int =
        runBlocking { appDatabase.featuredProjectDao().getFeaturedProjects().first().size }

    private fun storedProjectCategoriesCount(): Int =
        runBlocking { appDatabase.projectCategoryDao().getProjectsCategories().first().size }

    private fun waitForActivityInStage(activityClass: Class<out Activity>, stage: Stage) {
        val deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(ACTIVITY_TIMEOUT_MS)
        var found = false
        while (!found && System.nanoTime() < deadline) {
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                found = ActivityLifecycleMonitorRegistry.getInstance()
                    .getActivitiesInStage(stage)
                    .any { activityClass.isInstance(it) }
            }
            if (!found) {
                Thread.sleep(POLL_INTERVAL_MS)
            }
        }
        assertTrue("${activityClass.simpleName} did not reach $stage", found)
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

    /**
     * The community sections render whatever the local database holds, so the fixtures are
     * decoded and stored directly. Going through HTTP here only made the test depend on a
     * reachable server, which is what made it fail outside CI.
     */
    private fun seedCommunityData() {
        val instrumentationContext = InstrumentationRegistry.getInstrumentation().context

        val featuredType = Types.newParameterizedType(
            CursorPaginatedResponse::class.java,
            FeaturedProjectApi::class.java
        )
        val featuredResponse = CatroidWebServer.moshi
            .adapter<CursorPaginatedResponse<FeaturedProjectApi>>(featuredType)
            .fromJson(MockResponseFileReader(instrumentationContext, FEATURED_PROJECTS_FIXTURE).content)
        val featuredProjects = featuredResponse?.data.orEmpty().map { it.toRoomEntity() }

        val categoriesResponse = CatroidWebServer.moshi
            .adapter(ProjectsCategoryListResponse::class.java)
            .fromJson(MockResponseFileReader(instrumentationContext, PROJECTS_CATEGORIES_FIXTURE).content)
        val categories = categoriesResponse?.data.orEmpty().toProjectCategoryWithResponsesList()

        appDatabase.featuredProjectDao().deleteAll()
        appDatabase.featuredProjectDao().insertFeaturedProjects(featuredProjects)
        appDatabase.projectCategoryDao().insertProjectCategoriesWithResponses(categories)

        // Assert instead of assume: the fixtures ship with the test, so empty tables mean the
        // response models drifted away from them, which is a failure and not a skip.
        assertTrue("featured projects fixture was not stored", storedFeaturedProjectsCount() > 0)
        assertTrue("project categories fixture was not stored", storedProjectCategoriesCount() > 0)
    }

    private fun seedCommunityDataAndLaunch(seedCommunityData: Boolean = true) {
        if (seedCommunityData) {
            connectionMonitor.setValueTo(true)
            seedCommunityData()
        }
        baseActivityTestRule.launchActivity(null)
    }
}
