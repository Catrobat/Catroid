package org.catrobat.catroid.uiespresso.facerecognizer

import android.app.Activity
import android.app.Instrumentation
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.intent.matcher.IntentMatchers.hasType
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.FaceRecognizer.Recognizer
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.catrobat.catroid.R
import org.catrobat.catroid.content.actions.FaceNameTrainAction
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.endsWith
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class FaceTrainingUiTest {

    private lateinit var appContext: Context
    private lateinit var testContext: Context
    private lateinit var scenario: ActivityScenario<TestHostActivity>
    private lateinit var action: FaceNameTrainAction

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        testContext = InstrumentationRegistry.getInstrumentation().context
        FileUtils.init(appContext)
        FileUtils.deleteAll()
        Recognizer.release()
        FaceNameTrainAction.resetStateForTest(appContext)

        Intents.init()
        scenario = ActivityScenario.launch(TestHostActivity::class.java)
        scenario.onActivity { activity ->
            FaceNameTrainAction.testActivity = activity
            action = FaceNameTrainAction()
            action.openMenuForTest(activity)
        }
        waitForUi()
    }

    @After
    fun tearDown() {
        FaceNameTrainAction.resetStateForTest(null)
        if (::scenario.isInitialized) scenario.close()
        Intents.release()
        FileUtils.deleteAll()
        Recognizer.release()
    }

    @Test
    fun emptyMenuShowsAddLabelButton() {
        onView(withText(text(R.string.face_train_title)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withText(text(R.string.face_train_no_names)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withText(text(R.string.face_train_add_new_name)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun emptyLabelIsRejectedAndPickerDoesNotOpen() {
        openNewLabelDialog()
        onView(withText(text(R.string.face_train_next))).inRoot(isDialog()).perform(click())
        waitForUi()

        assertTrue(recognizer().classNames.isEmpty())
        onView(withText(text(R.string.face_train_new_name_title)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun addingLabelOpensMultiImagePicker() {
        stubPicker(Activity.RESULT_CANCELED, null)

        addLabelThroughUi("Person A")

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
    fun clickingExistingLabelOpensImagePicker() {
        recognizer().addPerson("Person A")
        reopenMenu()
        stubPicker(Activity.RESULT_CANCELED, null)

        onView(withText("Person A")).inRoot(isDialog()).perform(click())
        waitForUi()

        intended(hasAction(Intent.ACTION_OPEN_DOCUMENT))
    }

    @Test
    fun selectedImageIsTrainedAndSaved() {
        val photo = requiredAssetUri("faces/p01_train1.jpg")
        stubPicker(Activity.RESULT_OK, Intent().setData(photo))

        addLabelThroughUi("Person A")
        waitForTrainingToFinish()

        assertEquals(listOf("Person A"), recognizer().classNames)
        assertTrue("Selected image produced no saved embeddings", recognizer().getPhotoCount(0) > 0)
        assertEquals(listOf(1, 1), FaceNameTrainAction.getProgressForTest().toList())
        assertFalse(FaceNameTrainAction.isTrainingForTest())
    }

    @Test
    fun multipleSelectedImagesAreAllProcessed() {
        val first = requiredAssetUri("faces/p01_train1.jpg")
        val second = requiredAssetUri("faces/p01_train2.jpg")
        val selection = ClipData.newUri(appContext.contentResolver, "face", first).apply {
            addItem(ClipData.Item(second))
        }
        stubPicker(Activity.RESULT_OK, Intent().apply { clipData = selection })

        addLabelThroughUi("Person A")
        waitForTrainingToFinish()

        assertTrue("Selected images produced no saved embeddings", recognizer().getPhotoCount(0) > 0)
        assertEquals(listOf(2, 2), FaceNameTrainAction.getProgressForTest().toList())
        assertFalse(FaceNameTrainAction.isTrainingForTest())
    }

    @Test
    fun cancellingPickerKeepsLabelButDoesNotTrain() {
        stubPicker(Activity.RESULT_CANCELED, null)

        addLabelThroughUi("Person A")

        assertEquals(listOf("Person A"), recognizer().classNames)
        assertEquals(0, recognizer().getPhotoCount(0))
        assertFalse(FaceNameTrainAction.isTrainingForTest())
    }

    @Test
    fun imageWithoutFaceDoesNotCreateTrainingData() {
        val photo = requiredAssetUri("faces/no_face.jpeg")
        stubPicker(Activity.RESULT_OK, Intent().setData(photo))

        addLabelThroughUi("Person A")
        waitForTrainingToFinish()

        assertEquals(listOf("Person A"), recognizer().classNames)
        assertEquals(0, recognizer().getPhotoCount(0))
        assertFalse(FaceNameTrainAction.isTrainingForTest())
    }

    @Test
    fun deleteNoKeepsLabel() {
        recognizer().addPerson("Person A")
        reopenMenu()

        onView(withText(DELETE_SYMBOL)).inRoot(isDialog()).perform(click())
        onView(withText(text(R.string.face_train_no))).inRoot(isDialog()).perform(click())
        waitForUi()

        assertEquals(listOf("Person A"), recognizer().classNames)
    }

    @Test
    fun deleteYesRemovesLabelAndItsEmbeddings() {
        val bitmap = requiredBitmap("faces/p01_train1.jpg")
        try {
            val index = recognizer().addPerson("Person A")
            val embeddings = recognizer().embedFrame(bitmap)
            assertTrue("Fixture produced no embeddings", embeddings.isNotEmpty())
            recognizer().addEmbeddings(index, embeddings)
        } finally {
            bitmap.recycle()
        }
        reopenMenu()

        onView(withText(DELETE_SYMBOL)).inRoot(isDialog()).perform(click())
        onView(withText(text(R.string.face_train_yes))).inRoot(isDialog()).perform(click())
        waitForUi()

        assertTrue(recognizer().classNames.isEmpty())
        assertEquals(0, recognizer().getPhotoCount(0))
        onView(withText(text(R.string.face_train_no_names)))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    private fun openNewLabelDialog() {
        onView(withText(text(R.string.face_train_add_new_name)))
            .inRoot(isDialog())
            .perform(click())
        waitForUi()
    }

    private fun addLabelThroughUi(name: String) {
        openNewLabelDialog()
        onView(withClassName(endsWith("EditText")))
            .inRoot(isDialog())
            .perform(clearText(), typeText(name), closeSoftKeyboard())
        onView(withText(text(R.string.face_train_next))).inRoot(isDialog()).perform(click())
        waitForUi()
    }

    private fun reopenMenu() {
        scenario.onActivity { action.openMenuForTest(it) }
        waitForUi()
    }

    private fun stubPicker(resultCode: Int, data: Intent?) {
        intending(hasAction(Intent.ACTION_OPEN_DOCUMENT)).respondWith(
            Instrumentation.ActivityResult(resultCode, data)
        )
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

    private fun waitForTrainingToFinish() {
        val startedDeadline = System.currentTimeMillis() + 10_000L
        while (!FaceNameTrainAction.isTrainingForTest() &&
            FaceNameTrainAction.getProgressForTest()[0] == 0 &&
            System.currentTimeMillis() < startedDeadline
        ) {
            Thread.sleep(50L)
        }

        val finishedDeadline = System.currentTimeMillis() + 60_000L
        while (FaceNameTrainAction.isTrainingForTest() &&
            System.currentTimeMillis() < finishedDeadline
        ) {
            Thread.sleep(100L)
        }
        if (FaceNameTrainAction.isTrainingForTest()) {
            fail("Face training did not finish within 60 seconds")
        }
        waitForUi()
    }

    private fun waitForUi() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Thread.sleep(250L)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    companion object {
        private const val DELETE_SYMBOL = "\u2715"
    }
}
