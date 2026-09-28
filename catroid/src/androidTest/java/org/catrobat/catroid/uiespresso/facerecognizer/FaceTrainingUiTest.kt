package org.catrobat.catroid.uiespresso.facerecognizer

import android.app.Activity
import android.app.Instrumentation
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.test.espresso.Espresso.onIdle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.intent.matcher.IntentMatchers.hasType
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.android.material.textfield.TextInputLayout
import org.catrobat.catroid.FaceRecognizer.Recognizer
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.catrobat.catroid.R
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.actions.FaceNameTrainAction
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
import org.catrobat.catroid.content.bricks.FaceNameTrain
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableEqualsWithTimeout
import org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableIsGreaterThanWithTimeout
import org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableNotEqualsForTimeMs
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.endsWith
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * The Face name train brick on the real stage, with this program:
 *
 *   When scene starts: Face name train; Set afterTraining to 1
 *   When scene starts: Forever { Change ticks by 1 }
 *
 * afterTraining shows whether the script has moved past the brick; ticks shows
 * whether the stage is running. The dialogs are BrickDialogManager dialogs, the
 * photo picker is answered by Espresso-Intents, and its result reaches the
 * action through StageActivity.onActivityResult as in the app.
 */
@RunWith(AndroidJUnit4::class)
class FaceTrainingUiTest {

    @get:Rule
    val stageRule = BaseActivityTestRule(StageActivity::class.java, false, false)

    private lateinit var appContext: Context
    private lateinit var testContext: Context
    private lateinit var afterTraining: UserVariable
    private lateinit var ticks: UserVariable
    private val trainingIdle = TrainingIdlingResource()

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        testContext = InstrumentationRegistry.getInstrumentation().context
        FileUtils.init(appContext)
        FileUtils.deleteAll()
        Recognizer.release()
        FaceNameTrainAction.resetStateForTest(appContext)

        // Espresso waits for the background training through this resource
        // instead of the test polling with Thread.sleep.
        IdlingPolicies.setIdlingResourceTimeout(TRAINING_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        IdlingPolicies.setMasterPolicyTimeout(TRAINING_TIMEOUT_SECONDS * 2, TimeUnit.SECONDS)
        IdlingRegistry.getInstance().register(trainingIdle)
        Intents.init()
        createProject()
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(trainingIdle)
        IdlingPolicies.setIdlingResourceTimeout(DEFAULT_IDLING_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        IdlingPolicies.setMasterPolicyTimeout(DEFAULT_MASTER_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        Intents.release()
        FaceNameTrainAction.resetStateForTest(null)
        FileUtils.deleteAll()
        Recognizer.release()
    }

    // ---------------- Brick dialog behaviour ----------------

    @Test
    fun emptyMenuShowsAddNameAndDone() {
        startStage()

        menuTitle().check(matches(isDisplayed()))
        onView(withText(text(R.string.face_train_no_names))).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(text(R.string.face_train_add_new_name))).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(text(R.string.done))).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(text(R.string.face_train_choose_name))).check(doesNotExist())
    }

    /** With names in the list, the title tells the user what tapping a name does. */
    @Test
    fun menuWithNamesSaysThatTappingANameAddsPhotos() {
        recognizer().addPerson("Person A")
        recognizer().addPerson("Person B")
        startStage()

        onView(withText(text(R.string.face_train_choose_name))).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText("Person A")).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText("Person B")).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(text(R.string.face_train_title))).check(doesNotExist())
        onView(withText(text(R.string.face_train_no_names))).check(doesNotExist())
    }

    /**
     * Colours come from Catroid's theme, not from AppCompat's defaults: the
     * dialog buttons use @color/accent (#A8DFF4), not AppCompat's dark accent
     * #80CBC4.
     */
    @Test
    fun dialogButtonsUseTheCatroidAccent() {
        startStage()

        val accent = ContextCompat.getColor(appContext, R.color.accent)
        for (label in listOf(R.string.done, R.string.face_train_add_new_name)) {
            onView(withText(text(label))).inRoot(isDialog()).check { view, noView ->
                if (noView != null) throw noView
                assertEquals(
                    "${text(label)} must use @color/accent",
                    String.format("#%08X", accent),
                    String.format("#%08X", (view as Button).currentTextColor)
                )
            }
        }
    }

    @Test
    fun scriptWaitsUntilDoneIsPressed() {
        startStage()

        assertUserVariableNotEqualsForTimeMs(afterTraining, 1.0, HOLD_CHECK_MS)

        onView(withText(text(R.string.done))).inRoot(isDialog()).perform(click())
        assertUserVariableEqualsWithTimeout(afterTraining, 1.0, CONTINUE_TIMEOUT_MS)
        assertFalse("No dialog may stay open after Done", stageRule.activity.dialogIsShowing())
    }

    @Test
    fun stageIsPausedWhileTheMenuIsOpenAndResumesAfterDone() {
        startStage()
        onIdle()

        val ticksWhileOpen = ticks.value as Double
        assertUserVariableNotEqualsForTimeMs(ticks, ticksWhileOpen + 1, HOLD_CHECK_MS)
        assertEquals("The stage must not run while the menu is open", ticksWhileOpen, ticks.value as Double, 0.0)

        onView(withText(text(R.string.done))).inRoot(isDialog()).perform(click())
        assertUserVariableIsGreaterThanWithTimeout(ticks, ticksWhileOpen, CONTINUE_TIMEOUT_MS)
    }

    @Test
    fun tapOutsideDoesNotCloseTheMenu() {
        startStage()

        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(5, 5)
        onIdle()

        menuTitle().check(matches(isDisplayed()))
        assertUserVariableNotEqualsForTimeMs(afterTraining, 1.0, HOLD_CHECK_MS)
    }

    @Test
    fun backKeyOpensTheStageMenuAndTheBrickKeepsWaiting() {
        startStage()

        pressBack()
        onView(withId(R.id.stage_dialog_button_continue)).check(matches(isDisplayed()))
        onView(withId(R.id.stage_dialog_button_continue)).perform(click())
        onIdle()

        menuTitle().check(matches(isDisplayed()))
        assertUserVariableNotEqualsForTimeMs(afterTraining, 1.0, HOLD_CHECK_MS)
    }

    // ---------------- Names and photos ----------------

    @Test
    fun emptyNameIsRejectedInlineAndNextIsDisabled() {
        startStage()
        openNewNameDialog()

        onView(withText(text(R.string.name_empty))).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(text(R.string.next))).inRoot(isDialog()).check(matches(not(isEnabled())))

        assertTrue(recognizer().classNames.isEmpty())
        onView(withText(text(R.string.face_train_add_new_name))).inRoot(isDialog()).check(matches(isDisplayed()))
    }

    @Test
    fun existingNameIsRejectedInlineAndNextIsDisabled() {
        recognizer().addPerson("Person A")
        startStage()
        openNewNameDialog()

        onView(withClassName(endsWith("EditText")))
            .inRoot(isDialog())
            .perform(typeText("Person A"), closeSoftKeyboard())

        onView(withText(text(R.string.name_already_exists))).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(text(R.string.next))).inRoot(isDialog()).check(matches(not(isEnabled())))
        assertEquals(listOf("Person A"), recognizer().classNames)
    }

    @Test
    fun nameFieldIsAMaterialTextInputWithAHint() {
        startStage()
        openNewNameDialog()

        onView(withId(R.id.input))
            .inRoot(isDialog())
            .check(matches(allOf(isDisplayed(), withClassName(endsWith("TextInputLayout")))))
        onView(withId(R.id.input)).inRoot(isDialog()).check { view, noView ->
            if (noView != null) throw noView
            assertEquals(text(R.string.face_train_name_hint), (view as TextInputLayout).hint?.toString())
        }
    }

    @Test
    fun addingNameOpensMultiImagePicker() {
        stubPicker(Activity.RESULT_CANCELED, null)
        startStage()

        addNameThroughUi("Person A")

        intended(
            allOf(
                hasAction(Intent.ACTION_OPEN_DOCUMENT),
                hasType("image/*"),
                hasExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            )
        )
        assertEquals(listOf("Person A"), recognizer().classNames)
        assertFalse(FaceNameTrainAction.isTrainingForTest())
    }

    @Test
    fun choosingAnExistingNameOpensImagePicker() {
        recognizer().addPerson("Person A")
        stubPicker(Activity.RESULT_CANCELED, null)
        startStage()

        onView(withText("Person A")).inRoot(isDialog()).perform(click())
        onIdle()

        intended(hasAction(Intent.ACTION_OPEN_DOCUMENT))
    }

    @Test
    fun selectedImageIsTrainedAndSavedAndTheMenuReturns() {
        stubPicker(Activity.RESULT_OK, Intent().setData(requiredAssetUri("faces/p01_train1.jpg")))
        startStage()

        addNameThroughUi("Person A")
        waitForTrainingToFinish()

        assertEquals(listOf("Person A"), recognizer().classNames)
        assertTrue("Selected image produced no saved embeddings", recognizer().getPhotoCount(0) > 0)
        assertEquals(listOf(1, 1), FaceNameTrainAction.getProgressForTest().toList())
        onView(withText("Person A")).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withText(text(R.string.face_train_choose_name))).inRoot(isDialog()).check(matches(isDisplayed()))
        assertUserVariableNotEqualsForTimeMs(afterTraining, 1.0, HOLD_CHECK_MS)
    }

    @Test
    fun multipleSelectedImagesAreAllProcessed() {
        val selection = ClipData.newUri(appContext.contentResolver, "face", requiredAssetUri("faces/p01_train1.jpg"))
            .apply { addItem(ClipData.Item(requiredAssetUri("faces/p01_train2.jpg"))) }
        stubPicker(Activity.RESULT_OK, Intent().apply { clipData = selection })
        startStage()

        addNameThroughUi("Person A")
        waitForTrainingToFinish()

        assertTrue("Selected images produced no saved embeddings", recognizer().getPhotoCount(0) > 0)
        assertEquals(listOf(2, 2), FaceNameTrainAction.getProgressForTest().toList())
    }

    @Test
    fun cancellingPickerKeepsNameButDoesNotTrain() {
        stubPicker(Activity.RESULT_CANCELED, null)
        startStage()

        addNameThroughUi("Person A")

        assertEquals(listOf("Person A"), recognizer().classNames)
        assertEquals(0, recognizer().getPhotoCount(0))
        assertFalse(FaceNameTrainAction.isTrainingForTest())
        menuTitle().check(matches(isDisplayed()))
    }

    @Test
    fun imageWithoutFaceDoesNotCreateTrainingData() {
        stubPicker(Activity.RESULT_OK, Intent().setData(requiredAssetUri("faces/no_face.jpeg")))
        startStage()

        addNameThroughUi("Person A")
        waitForTrainingToFinish()

        assertEquals(listOf("Person A"), recognizer().classNames)
        assertEquals(0, recognizer().getPhotoCount(0))
    }

    /**
     * StageActivity and StageResourceHolder may both forward the same picker
     * result. The first one must start training; a second one arriving while
     * training runs must be ignored. The first call is the positive control.
     */
    @Test
    fun duplicatePickerResultDuringTrainingIsIgnored() {
        recognizer().addPerson("Person A")
        startStage()
        val onePhoto = Intent().setData(requiredAssetUri("faces/p01_train1.jpg"))
        val twoPhotos = Intent().apply {
            clipData = ClipData.newUri(appContext.contentResolver, "face", requiredAssetUri("faces/p01_train2.jpg"))
                .apply { addItem(ClipData.Item(requiredAssetUri("faces/p01_train3.jpg"))) }
        }

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val action = requireNotNull(FaceNameTrainAction.currentInstance) { "The brick has not run" }
            FaceNameTrainAction.setPendingNameForTest("Person A")
            action.handleResult(FaceNameTrainAction.REQUEST_FIRST, Activity.RESULT_OK, onePhoto)
            assertTrue("The first picker result must start training", FaceNameTrainAction.isTrainingForTest())
            assertEquals(1, FaceNameTrainAction.getProgressForTest()[1])

            // Re-arm the pending name so that only the in-progress guard can stop
            // the duplicate from starting a second, two-photo run.
            FaceNameTrainAction.setPendingNameForTest("Person A")
            action.handleResult(FaceNameTrainAction.REQUEST_FIRST, Activity.RESULT_OK, twoPhotos)
            assertEquals("A duplicate result must not replace the running training",
                         1, FaceNameTrainAction.getProgressForTest()[1])
        }
        waitForTrainingToFinish()

        assertEquals(listOf(1, 1), FaceNameTrainAction.getProgressForTest().toList())
        assertTrue("The first result produced no saved embeddings", recognizer().getPhotoCount(0) > 0)
    }

    @Test
    fun deleteNoKeepsName() {
        recognizer().addPerson("Person A")
        startStage()

        chooseNameToDelete("Person A")
        onView(withText(text(R.string.cancel))).inRoot(isDialog()).perform(click())
        onIdle()

        assertEquals(listOf("Person A"), recognizer().classNames)
        menuTitle().check(matches(isDisplayed()))
    }

    @Test
    fun deleteYesRemovesNameAndItsEmbeddings() {
        val bitmap = requiredBitmap("faces/p01_train1.jpg")
        try {
            val index = recognizer().addPerson("Person A")
            val embeddings = recognizer().embedFrame(bitmap)
            assertTrue("Fixture produced no embeddings", embeddings.isNotEmpty())
            recognizer().addEmbeddings(index, embeddings)
        } finally {
            bitmap.recycle()
        }
        startStage()

        chooseNameToDelete("Person A")
        onView(withText(text(R.string.delete))).inRoot(isDialog()).perform(click())
        onIdle()

        assertTrue(recognizer().classNames.isEmpty())
        assertEquals(0, recognizer().getPhotoCount(0))
        onView(withText(text(R.string.face_train_no_names))).inRoot(isDialog()).check(matches(isDisplayed()))
    }

    // ---------------- Helpers ----------------

    private fun createProject() {
        val project = UiTestUtils.createDefaultTestProject("FaceTrainingUiTest")
        afterTraining = UserVariable("afterTraining", 0.0)
        ticks = UserVariable("ticks", 0.0)
        project.addUserVariable(afterTraining)
        project.addUserVariable(ticks)

        val script = UiTestUtils.getDefaultTestScript(project)
        script.addBrick(FaceNameTrain())
        script.addBrick(SetVariableBrick(Formula(1.0), afterTraining))

        val forever = ForeverBrick()
        forever.addBrick(ChangeVariableBrick(Formula(1.0), ticks))
        UiTestUtils.getDefaultTestSprite(project).addScript(StartScript().apply { addBrick(forever) })
    }

    /** Launches the stage and waits until the brick's first dialog is open. */
    private fun startStage() {
        stageRule.launchActivity(null)
        val dialogOpen = BrickDialogOpenIdlingResource(stageRule.activity)
        IdlingRegistry.getInstance().register(dialogOpen)
        try {
            onIdle()
        } finally {
            IdlingRegistry.getInstance().unregister(dialogOpen)
        }
    }

    /** The name list's title: a hint once names exist, "Face names" while it is empty. */
    private fun menuTitle() = onView(
        withText(text(if (recognizer().classNames.isEmpty()) R.string.face_train_title else R.string.face_train_choose_name))
    ).inRoot(isDialog())

    private fun openNewNameDialog() {
        onView(withText(text(R.string.face_train_add_new_name))).inRoot(isDialog()).perform(click())
        onIdle()
    }

    private fun addNameThroughUi(name: String) {
        openNewNameDialog()
        onView(withClassName(endsWith("EditText")))
            .inRoot(isDialog())
            .perform(clearText(), typeText(name), closeSoftKeyboard())
        onView(withText(text(R.string.next))).inRoot(isDialog()).perform(click())
        onIdle()
    }

    private fun chooseNameToDelete(name: String) {
        onView(withText(text(R.string.face_train_delete))).inRoot(isDialog()).perform(click())
        onIdle()
        onView(withText(name)).inRoot(isDialog()).perform(click())
        onIdle()
        onView(withText(appContext.getString(R.string.face_train_delete_title, name)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withText(text(R.string.dialog_confirm_delete))).inRoot(isDialog()).check(matches(isDisplayed()))
    }

    private fun stubPicker(resultCode: Int, data: Intent?) {
        intending(hasAction(Intent.ACTION_OPEN_DOCUMENT)).respondWith(Instrumentation.ActivityResult(resultCode, data))
    }

    private fun recognizer(): Recognizer = Recognizer.getInstance(appContext)

    private fun text(resource: Int): String = appContext.getString(resource)

    private fun requiredAssetUri(assetPath: String): Uri {
        val output = File(appContext.cacheDir, assetPath.substringAfterLast('/'))
        try {
            testContext.assets.open(assetPath).use { input ->
                output.outputStream().use { input.copyTo(it) }
            }
        } catch (error: Exception) {
            throw AssertionError("Required test asset is missing: $assetPath", error)
        }
        return Uri.fromFile(output)
    }

    private fun requiredBitmap(assetPath: String) = try {
        testContext.assets.open(assetPath).use { input ->
            requireNotNull(android.graphics.BitmapFactory.decodeStream(input)) {
                "Could not decode required test asset: $assetPath"
            }
        }
    } catch (error: Exception) {
        throw AssertionError("Required test asset is missing: $assetPath", error)
    }

    /**
     * onIdle() waits for the main looper and for [trainingIdle], so this returns
     * once the picker result has been handled and any training it started has
     * finished (or fails after TRAINING_TIMEOUT_SECONDS).
     */
    private fun waitForTrainingToFinish() {
        onIdle()
        assertFalse("Face training is still running", FaceNameTrainAction.isTrainingForTest())
    }

    /** Busy while FaceNameTrainAction reports a training run in progress. */
    private class TrainingIdlingResource : IdlingResource {
        @Volatile
        private var callback: IdlingResource.ResourceCallback? = null

        override fun getName(): String = "FaceNameTrainAction training"

        override fun isIdleNow(): Boolean {
            val idle = !FaceNameTrainAction.isTrainingForTest()
            if (idle) {
                callback?.onTransitionToIdle()
            }
            return idle
        }

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
            this.callback = callback
        }
    }

    /** Busy until a BrickDialogManager dialog is open on [stage]; the models load first. */
    private class BrickDialogOpenIdlingResource(private val stage: StageActivity) : IdlingResource {
        @Volatile
        private var callback: IdlingResource.ResourceCallback? = null

        override fun getName(): String = "Face training dialog open"

        override fun isIdleNow(): Boolean {
            val idle = stage.dialogIsShowing()
            if (idle) {
                callback?.onTransitionToIdle()
            }
            return idle
        }

        override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
            this.callback = callback
        }
    }

    companion object {
        private const val TRAINING_TIMEOUT_SECONDS = 60L
        private const val HOLD_CHECK_MS = 1500
        private const val CONTINUE_TIMEOUT_MS = 3000

        /** Espresso's own defaults, restored so other test classes are unaffected. */
        private const val DEFAULT_IDLING_TIMEOUT_SECONDS = 26L
        private const val DEFAULT_MASTER_TIMEOUT_SECONDS = 60L
    }
}
