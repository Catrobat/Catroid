package org.catrobat.catroid.test.content.bricks

import android.content.Context
import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.FaceNameDetect
import org.catrobat.catroid.content.bricks.FaceNameTrain
import org.catrobat.catroid.content.bricks.FlashBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The face name bricks sit in the Device category behind the "Face name
 * detection" AI setting, like the other AI bricks and like the face name sensor.
 */
@RunWith(AndroidJUnit4::class)
class FaceNameBrickCategoryTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val factory = CategoryBricksFactory()

    @Before
    fun setUp() {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit()
        val project = Project(context, javaClass.simpleName)
        val sprite = Sprite("testSprite")
        sprite.addScript(StartScript().apply { addBrick(SetXBrick()) })
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
    }

    @After
    fun tearDown() {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit()
    }

    @Test
    fun faceNameBricksAreHiddenWhileTheSettingIsOff() {
        val classes = deviceBrickClasses()

        assertFalse(FaceNameTrain::class.java in classes)
        assertFalse(FaceNameDetect::class.java in classes)
    }

    @Test
    fun faceNameBricksFollowTheCameraBricksWhenTheSettingIsOn() {
        SettingsFragment.setAIFaceNameDetectionPreferenceEnabled(context, true)

        val classes = deviceBrickClasses()

        val flash = classes.indexOf(FlashBrick::class.java)
        assertEquals(
            listOf(FlashBrick::class.java, FaceNameTrain::class.java, FaceNameDetect::class.java),
            classes.subList(flash, flash + 3)
        )
    }

    @Test
    fun bothFaceNameBricksBelongToDeviceEvenWithTheSettingOff() {
        // A loaded project can contain the bricks while the setting is off.
        val device = context.getString(R.string.category_device)

        assertEquals(device, factory.getBrickCategory(FaceNameTrain(), false, context))
        assertEquals(device, factory.getBrickCategory(FaceNameDetect(), false, context))
    }

    private fun deviceBrickClasses(): List<Class<*>> =
        factory.getBricks(context.getString(R.string.category_device), false, context).map { it.javaClass }
}
