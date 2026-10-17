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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.formulaeditor

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.FaceRecognizer.FaceDatabase
import org.catrobat.catroid.FaceRecognizer.FaceNameWindow
import org.catrobat.catroid.FaceRecognizer.Recognizer
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.camera.mlkitdetectors.FaceNameDetector
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.SayBubbleBrick
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.FUNCTION
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.NUMBER
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType.SENSOR
import org.catrobat.catroid.formulaeditor.InternTokenType.FUNCTION_NAME
import org.catrobat.catroid.formulaeditor.InternTokenType.FUNCTION_PARAMETERS_BRACKET_CLOSE
import org.catrobat.catroid.formulaeditor.InternTokenType.FUNCTION_PARAMETERS_BRACKET_OPEN
import org.catrobat.catroid.formulaeditor.InternTokenType.OPERATOR
import org.catrobat.catroid.formulaeditor.InternTokenType.STRING
import org.catrobat.catroid.formulaeditor.InternTokenType.USER_VARIABLE
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.ui.recyclerview.fragment.CategoryListItems
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Follows the value from its Sensors entry through the editor, interpreter and
 * saved project format. Real cached similarity scores make an ignored, quoted
 * or globally shared threshold observable without loading the face models.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], qualifiers = "en")
class FaceNameThresholdFormulaTest {
    private lateinit var context: Context
    private lateinit var recognizer: Recognizer
    private lateinit var scope: Scope
    private lateinit var injectedProjectManager: ProjectManager
    private var savedMinSimilarity = 0f
    private var savedMinMargin = 0f
    private var savedFaceNameSetting = false

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        savedMinSimilarity = FaceDatabase.minSimilarity
        savedMinMargin = FaceDatabase.minMargin
        savedFaceNameSetting = SettingsFragment.isAIFaceNameDetectionSharedPreferenceEnabled(context)
        FaceNameDetector.reset()
        FileUtils.init(context)
        FileUtils.deleteAll()
        recognizer = Recognizer.withoutModelsForTest(context)
        recognizer.addPerson(PERSON)

        val project = Project(context, "Face threshold formulas")
        val sprite = Sprite("Face sensor")
        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
        // CategoryListItems uses Koin while the formula interpreter still uses
        // the static singleton; Robolectric can give them different instances.
        injectedProjectManager = KoinJavaComponent.get(ProjectManager::class.java)
        injectedProjectManager.currentProject = project
        injectedProjectManager.currentSprite = sprite
        scope = Scope(project, sprite, SequenceAction())
        seedCachedScore(0.56f)
    }

    @After
    fun tearDown() {
        FaceNameDetector.reset()
        FileUtils.deleteAll()
        FaceDatabase.minSimilarity = savedMinSimilarity
        FaceDatabase.minMargin = savedMinMargin
        SettingsFragment.setAIFaceNameDetectionPreferenceEnabled(context, savedFaceNameSetting)
        injectedProjectManager.resetProjectManager()
        ProjectManager.getInstance().resetProjectManager()
    }

    @Test
    fun insertingTheSensorGivesAnEditableNumericDefault() {
        val editor = InternFormula(insertedTokens())
        editor.generateExternFormulaStringAndInternExternMapping(context)
        val defaultFormula = parse(editor.internTokenFormulaList)
        assertEquals(FUNCTION, defaultFormula.root.elementType)
        assertEquals(Functions.FACE_NAME_WITH_THRESHOLD.name, defaultFormula.root.value)
        assertEquals(InternTokenType.NUMBER, defaultFormula.root.internTokenList[2].internTokenType)
        assertEquals("0.53", defaultFormula.root.internTokenList[2].tokenStringValue)
        assertEquals(PERSON, defaultFormula.interpretString(scope))
        assertEquals(
            "detectedfacenamewiththreshold(0.53)",
            compact(editor.externFormulaString)
        )

        // Select only the argument and replace it using the numeric keyboard.
        editor.internFormulaTokenSelection = InternFormulaTokenSelection(
            InternFormula.TokenSelectionType.USER_SELECTION, 2, 2
        )
        editor.handleKeyInput(R.id.formula_editor_keyboard_1, context, "")
        val editedFormula = parse(editor.internTokenFormulaList)
        assertEquals(Functions.FACE_NAME_WITH_THRESHOLD.name, editedFormula.root.value)
        assertEquals(InternTokenType.NUMBER, editedFormula.root.internTokenList[2].internTokenType)
        assertEquals("1", editedFormula.root.internTokenList[2].tokenStringValue)
        assertEquals(FaceNameWindow.UNKNOWN, editedFormula.interpretString(scope))
    }

    @Test
    fun theArgumentCanBeANumericExpression() {
        val formula = parse(functionTokens(
            InternToken(InternTokenType.NUMBER, "0.5"),
            InternToken(OPERATOR, Operators.PLUS.name),
            InternToken(InternTokenType.NUMBER, "0.03")
        ))

        assertEquals(PERSON, formula.interpretString(scope))
        assertEquals(setOf(Brick.FACE_NAME_DETECTION), resourcesOf(formula))
    }

    @Test
    fun changingAVariableChangesThisFormulaWithoutRetraining() {
        val sensitivity = UserVariable("threshold")
        scope.project!!.addUserVariable(sensitivity)
        val formula = parse(functionTokens(InternToken(USER_VARIABLE, sensitivity.name)))
        sensitivity.value = 0.53
        assertEquals(PERSON, formula.interpretString(scope))

        sensitivity.value = 0.61
        assertEquals(FaceNameWindow.UNKNOWN, formula.interpretString(scope))

        sensitivity.value = 0.53
        assertEquals(PERSON, formula.interpretString(scope))
        assertEquals(setOf(Brick.FACE_NAME_DETECTION), resourcesOf(formula))
    }

    @Test
    fun formulasWithDifferentThresholdsDoNotChangeEachOther() {
        val relaxed = parse(insertedTokens())
        val strict = parse(functionTokens(InternToken(InternTokenType.NUMBER, "0.61")))

        repeat(3) {
            assertEquals(PERSON, relaxed.interpretString(scope))
            assertEquals(FaceNameWindow.UNKNOWN, strict.interpretString(scope))
            assertEquals(PERSON, relaxed.interpretString(scope))
        }
    }

    @Test
    fun anInvalidTextArgumentReturnsUnknownAsText() {
        val formula = parse(functionTokens(InternToken(STRING, "not a threshold")))

        assertEquals(FaceNameWindow.UNKNOWN, formula.interpretObject(scope))
    }

    @Test
    fun anUndefinedNumericExpressionReturnsUnknownAsText() {
        val formula = parse(functionTokens(
            InternToken(InternTokenType.NUMBER, "0"),
            InternToken(OPERATOR, Operators.DIVIDE.name),
            InternToken(InternTokenType.NUMBER, "0")
        ))

        assertEquals(FaceNameWindow.UNKNOWN, formula.interpretObject(scope))
    }

    @Test
    fun anOverflowingNumericExpressionUsesTheUpperCutoff() {
        val formula = parse(functionTokens(
            InternToken(InternTokenType.NUMBER, "10"),
            InternToken(OPERATOR, Operators.POW.name),
            InternToken(InternTokenType.NUMBER, "1000")
        ))

        // The formula engine saturates infinity to Double.MAX_VALUE; the
        // sensor clamps finite values to 0..1, so a 0.56 match is rejected.
        assertEquals(FaceNameWindow.UNKNOWN, formula.interpretObject(scope))
    }

    @Test
    fun savingAndLoadingABrickPreservesTheNumberAndItsBehavior() {
        val formula = parse(insertedTokens())
        formula.root.setLeftChild(FormulaElement(NUMBER, "0.61", null))
        val brick = SayBubbleBrick().apply {
            setFormulaWithBrickField(Brick.BrickField.STRING, formula)
        }
        val xstream = XstreamSerializer.getInstance().xstream
        val xml = xstream.toXML(brick)
        assertTrue(xml.contains("<value>FACE_NAME_WITH_THRESHOLD</value>"))
        val loaded = xstream.fromXML(xml) as SayBubbleBrick
        val loadedFormula = loaded.getFormulaWithBrickField(Brick.BrickField.STRING)

        assertEquals(InternTokenType.NUMBER, loadedFormula.root.internTokenList[2].internTokenType)
        assertEquals("0.61", loadedFormula.root.internTokenList[2].tokenStringValue)
        assertEquals(FaceNameWindow.UNKNOWN, loadedFormula.interpretString(scope))
        assertEquals(
            "detectedfacenamewiththreshold(0.61)",
            compact(loadedFormula.getTrimmedFormulaString(context))
        )
        assertEquals(setOf(Brick.FACE_NAME_DETECTION), resourcesOf(loadedFormula))

        // A reloaded formula remains editable and connected to the same cached scores.
        loadedFormula.root.setLeftChild(FormulaElement(NUMBER, "0.53", null))
        assertEquals(PERSON, loadedFormula.interpretString(scope))
    }

    @Test
    fun anOlderProjectWithTheParameterlessSensorStillLoadsAndRuns() {
        val xml = """
            <brick type="SayBubbleBrick">
              <formulaList>
                <formula category="STRING">
                  <type>SENSOR</type>
                  <value>ON_DEVICE_FACE_RECOGNITION</value>
                </formula>
              </formulaList>
            </brick>
        """.trimIndent()
        val loaded = XstreamSerializer.getInstance().xstream.fromXML(xml) as SayBubbleBrick
        val formula = loaded.getFormulaWithBrickField(Brick.BrickField.STRING)

        assertEquals(SENSOR, formula.root.elementType)
        assertEquals(Sensors.ON_DEVICE_FACE_RECOGNITION.name, formula.root.value)
        assertEquals(PERSON, formula.interpretString(scope))
        assertEquals("detected face name", formula.getTrimmedFormulaString(context).trim())
        assertEquals(setOf(Brick.FACE_NAME_DETECTION), resourcesOf(formula))
        assertNotNull(InternFormulaParser(formula.root.internTokenList).parseFormula(scope))
    }

    @Test
    fun theEnabledFaceNameCategoryContainsOneParameterizedEntry() {
        SettingsFragment.setAIFaceNameDetectionPreferenceEnabled(context, true)
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        try {
            val items = CategoryListItems().getSensorItems(activity)
            val entries = items.filter { it.nameResId == R.string.formula_editor_function_face_name_with_threshold }

            assertEquals(1, entries.size)
            assertEquals(context.getString(R.string.formula_editor_device_face_name_detection), entries.single().header)
            assertEquals("detectedfacenamewiththreshold(0.53)", compact(entries.single().text))
            assertFalse(items.any { it.nameResId == R.string.formula_editor_sensor_face_name_detected })
        } finally {
            activity.finish()
        }
    }

    @Test
    fun theSensorEntryIsHiddenWhenTheFaceNameSettingIsDisabled() {
        SettingsFragment.setAIFaceNameDetectionPreferenceEnabled(context, false)
        val activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        try {
            val items = CategoryListItems().getSensorItems(activity)

            assertFalse(items.any { it.nameResId == R.string.formula_editor_function_face_name_with_threshold })
            assertFalse(items.any { it.nameResId == R.string.formula_editor_sensor_face_name_detected })
        } finally {
            activity.finish()
        }
    }

    private fun insertedTokens(): List<InternToken> = InternFormulaKeyboardAdapter()
        .createInternTokenListByResourceId(R.string.formula_editor_function_face_name_with_threshold, "")

    private fun functionTokens(vararg argument: InternToken): List<InternToken> = listOf(
        InternToken(FUNCTION_NAME, Functions.FACE_NAME_WITH_THRESHOLD.name),
        InternToken(FUNCTION_PARAMETERS_BRACKET_OPEN)
    ) + argument + InternToken(FUNCTION_PARAMETERS_BRACKET_CLOSE)

    private fun parse(tokens: List<InternToken>): Formula =
        Formula(InternFormulaParser(tokens).parseFormula(scope))

    private fun resourcesOf(formula: Formula): Set<Int> = mutableSetOf<Int>().also {
        formula.addRequiredResources(it)
    }

    private fun compact(text: String): String = text.replace(Regex("\\s+"), "")

    /** Only bypass the camera/model work; use the real formula and score selection. */
    private fun seedCachedScore(score: Float) {
        val cache = FaceNameDetector::class.java.getDeclaredField("cachedRecognizer")
        cache.isAccessible = true
        cache.set(FaceNameDetector, recognizer)
        val defaultName = FaceNameDetector.window.addScores(
            recognizer, floatArrayOf(score), 10_000L, recognizer.classNames
        )
        SensorHandler.setFaceNameRecognitionResult(defaultName)
    }

    companion object {
        private const val PERSON = "Ada"
    }
}
