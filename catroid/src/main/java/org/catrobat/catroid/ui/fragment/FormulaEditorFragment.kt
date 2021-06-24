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
package org.catrobat.catroid.ui.fragment

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.common.io.Files
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.UserDefinedScript
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.Brick.BrickField
import org.catrobat.catroid.content.bricks.Brick.FormulaField
import org.catrobat.catroid.content.bricks.Brick.ResourcesSet
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.content.strategy.ShowFormulaEditorStrategy
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaEditorEditText
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.InternFormulaKeyboardAdapter
import org.catrobat.catroid.formulaeditor.InternFormulaParser
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.UndoState
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.ui.BottomBar.hideBottomBar
import org.catrobat.catroid.ui.BottomBar.showBottomBar
import org.catrobat.catroid.ui.BottomBar.showPlayButton
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.ui.addTabLayout
import org.catrobat.catroid.ui.dialogs.FormulaEditorComputeDialog
import org.catrobat.catroid.ui.dialogs.FormulaEditorIntroDialog
import org.catrobat.catroid.ui.dialogs.regexassistant.RegularExpressionAssistantDialog
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter.CategoryListItem
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.recyclerview.fragment.CategoryListFragment
import org.catrobat.catroid.ui.recyclerview.fragment.DataListFragment
import org.catrobat.catroid.ui.recyclerview.fragment.DataListFragment.FormulaEditorDataInterface
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment
import org.catrobat.catroid.ui.removeTabLayout
import org.catrobat.catroid.ui.runtimepermissions.BrickResourcesToRuntimePermissions
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.userbrick.UserDefinedBrickInput
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider
import org.catrobat.catroid.utils.SnackbarUtil
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.getProjectBitmap
import org.catrobat.paintroid.colorpicker.ColorPickerDialog.Companion.newInstance
import org.catrobat.paintroid.colorpicker.OnColorPickedListener
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

class FormulaEditorFragment : Fragment(), OnGlobalLayoutListener, FormulaEditorDataInterface {
    @get:VisibleForTesting
    lateinit var formulaEditorEditText: FormulaEditorEditText
    private lateinit var formulaEditorKeyboard: TableLayout
    private lateinit var formulaEditorBrick: LinearLayout
    lateinit var formulaBrick: FormulaBrick
    private var currentMenu: Menu? = null
    private val confirmSwitchEditTextTimeStamp = longArrayOf(0, 0)
    private var confirmSwitchEditTextCounter = 0
    private var hasFormulaBeenChanged = false
    private var actionBarTitleBuffer = ""
    private var chosenCategoryItem: CategoryListItem? = null
    private var chosenUserDataItem: UserData<*>? = null

    lateinit var currentBrickField: FormulaField
    private lateinit var currentFormula: Formula


    private val projectManager: ProjectManager = inject(ProjectManager::class.java).value

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isRestoringPreviouslyDestroyedActivity = savedInstanceState != null
        if (isRestoringPreviouslyDestroyedActivity) {
            parentFragmentManager.popBackStack(
                FORMULA_EDITOR_FRAGMENT_TAG,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            return
        }
        formulaBrick =
            requireArguments().getSerializable(FORMULA_BRICK_BUNDLE_ARGUMENT) as FormulaBrick? ?: return
        currentBrickField =
            requireArguments().getSerializable(FORMULA_FIELD_BUNDLE_ARGUMENT) as FormulaField? ?: return
        currentFormula = formulaBrick.getFormulaWithBrickField(currentBrickField) ?: return
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar ?: return
        actionBarTitleBuffer = actionBar.title.toString()
        actionBar.setTitle(R.string.formula_editor_title)
        setHasOptionsMenu(true)
        SettingsFragment.setToChosenLanguage(activity)
    }

    override fun onResume() {
        super.onResume()
        if (SnackbarUtil.areHintsEnabled(this.activity)) {
            SnackbarUtil.dismissAllHints()
            if (!SnackbarUtil.wasHintAlreadyShown(
                    activity, requireActivity().resources
                        .getResourceName(R.string.formula_editor_intro_title_formula_editor)
                )
            ) {
                FormulaEditorIntroDialog(this, R.style.StageDialog).show()
                SnackbarUtil.setHintShown(
                    activity,
                    requireActivity().resources.getResourceName(R.string.formula_editor_intro_title_formula_editor)
                )
            }
        }
    }

    private var showCustomView = false
    fun updateBrickView() {
        formulaEditorBrick.removeAllViews()
        if (showCustomView) {
            formulaEditorEditText.visibility = View.GONE
            formulaEditorKeyboard.visibility = View.GONE
            formulaEditorBrick.addView(formulaBrick.getCustomView(activity))
        } else {
            formulaEditorEditText.visibility = View.VISIBLE
            formulaEditorKeyboard.visibility = View.VISIBLE
            val brickView = formulaBrick.getView(requireActivity())
            formulaBrick.setClickListeners()
            formulaBrick.disableSpinners()
            formulaBrick.highlightTextView(currentBrickField)
            formulaEditorBrick.addView(brickView)
        }
    }

    fun updateFragmentAfterVisualPlacement() {
        updateBrickView()
        setInputFormula(currentBrickField, SET_FORMULA_ON_RETURN_FROM_VISUAL_PLACEMENT)
    }

    fun updateFragmentAfterColorPicker() {
        updateBrickView()
        setInputFormula(currentBrickField, SET_FORMULA_ON_RETURN_FROM_COLOR_PICKER)
    }

    private fun onUserDismiss() {
        refreshFormulaPreviewString(
            currentFormula.getTrimmedFormulaString(
                activity
            )
        )
        formulaEditorEditText.endEdit()
        parentFragmentManager.popBackStack()
        if (activity != null) {
            showBottomBar(activity)
            showPlayButton(activity)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val fragmentView = inflater.inflate(R.layout.fragment_formula_editor, container, false)
        fragmentView.isFocusableInTouchMode = true
        fragmentView.requestFocus()
        formulaEditorBrick = fragmentView.findViewById(R.id.formula_editor_brick_space)
        formulaEditorEditText = fragmentView.findViewById(R.id.formula_editor_edit_field)
        formulaEditorKeyboard = fragmentView.findViewById(R.id.formula_editor_keyboardview)
        updateBrickView()
        fragmentView.viewTreeObserver.addOnGlobalLayoutListener(this)
        setInputFormula(currentBrickField, SET_FORMULA_ON_CREATE_VIEW)
        formulaEditorEditText.init(this)
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.formula_editor_title)

        return fragmentView
    }

    override fun onStart() {
        formulaEditorKeyboard.isClickable = true
        requireView().requestFocus()
        val touchListener: OnTouchListener = object : OnTouchListener {
            private var handler: Handler? = null
            private var deleteAction: Runnable? = null
            private fun handleLongClick(view: View, event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_UP) {
                    if (handler == null) {
                        return true
                    }
                    handler?.removeCallbacks(deleteAction!!)
                    handler = null
                }
                if (event.action == MotionEvent.ACTION_DOWN) {
                    deleteAction = object : Runnable {
                        override fun run() {
                            handler?.postDelayed(this, 100)
                            if (formulaEditorEditText.isThereSomethingToDelete) {
                                formulaEditorEditText.handleKeyEvent(view.id, "")
                            }
                        }
                    }
                    if (handler != null) {
                        return true
                    }
                    handler = Handler()
                    handler?.postDelayed(deleteAction as Runnable, 400)
                }
                return true
            }

            override fun onTouch(view: View, event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_UP) {
                    updateButtonsOnKeyboardAndInvalidateOptionsMenu()
                    view.isPressed = false
                    handleLongClick(view, event)
                    return true
                }
                if (event.action == MotionEvent.ACTION_DOWN) {
                    view.isPressed = true
                    return when (view.id) {
                        R.id.formula_editor_keyboard_compute -> {
                            showComputeDialog()
                            true
                        }
                        R.id.formula_editor_keyboard_function -> {
                            showCategoryListFragment(
                                CategoryListFragment.FUNCTION_TAG,
                                R.string.formula_editor_functions
                            )
                            true
                        }
                        R.id.formula_editor_keyboard_logic -> {
                            showCategoryListFragment(
                                CategoryListFragment.LOGIC_TAG,
                                R.string.formula_editor_logic
                            )
                            true
                        }
                        R.id.formula_editor_keyboard_object -> {
                            showCategoryListFragment(
                                CategoryListFragment.OBJECT_TAG,
                                R.string.formula_editor_choose_object_variable
                            )
                            true
                        }
                        R.id.formula_editor_keyboard_sensors -> {
                            showCategoryListFragment(
                                CategoryListFragment.SENSOR_TAG,
                                R.string.formula_editor_device
                            )
                            true
                        }
                        R.id.formula_editor_keyboard_data -> {
                            showDataFragment()
                            true
                        }
                        R.id.formula_editor_keyboard_functional_button_toggle -> {
                            toggleFunctionalButtons()
                            true
                        }
                        R.id.formula_editor_keyboard_string -> {
                            if (isSelectedTextFirstParamOfRegularExpression) {
                                showNewRegexAssistantDialog()
                            } else {
                                showNewStringDialog()
                            }
                            true
                        }
                        R.id.formula_editor_keyboard_delete -> {
                            formulaEditorEditText.handleKeyEvent(view.id, "")
                            handleLongClick(view, event)
                        }
                        R.id.formula_editor_keyboard_color_picker -> {
                            showColorPickerDialog(view)
                            true
                        }
                        else -> {
                            formulaEditorEditText.handleKeyEvent(view.id, "")
                            true
                        }
                    }
                }
                return false
            }
        }
        for (index in 0 until formulaEditorKeyboard.childCount) {
            val tableRow = formulaEditorKeyboard.getChildAt(index)
            if (tableRow is TableRow) {
                for (indexRow in 0 until tableRow.childCount) {
                    tableRow.getChildAt(indexRow).setOnTouchListener(touchListener)
                }
            }
        }
        updateButtonsOnKeyboardAndInvalidateOptionsMenu()
        super.onStart()
    }

    private fun showColorPicker(
        callback: ShowFormulaEditorStrategy.Callback,
        fragmentManager: FragmentManager
    ) {
        val currentColor = callback.value
        val dialog = newInstance(currentColor,
                                 catroidFlag = true,
                                 openedFromFormulaEditorInCatroidFlag = true
        )
        val projectBitmap = projectManager.getProjectBitmap()
        dialog.setBitmap(projectBitmap)
        dialog.addOnColorPickedListener(object : OnColorPickedListener {
            override fun colorChanged(color: Int) {
                callback.value = color
            }
        })
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.AlertDialogWithTitle)
        dialog.show(fragmentManager, null)
    }

    private fun showColorPickerDialog(view: View) {
        val activity = UiUtils.getActivityFromView(view) ?: return
        val fragmentManager = activity.supportFragmentManager
        if (fragmentManager.isStateSaved) {
            return
        }
        showColorPicker(object : ShowFormulaEditorStrategy.Callback {
            override fun showFormulaEditor(view: View) {}
            override fun setValue(value: Int) {
                addString(String.format("#%06X", 0xFFFFFF and value))
            }

            override fun getValue(): Int {
                val currentValue = selectedFormulaText
                return if (currentValue.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
                    Color.parseColor(currentValue)
                } else {
                    0
                }
            }
        }, fragmentManager)
    }

    fun toggleFunctionalButtons() {
        val row1 = requireActivity().findViewById<View>(R.id.tableRow11)
        val row2 = requireActivity().findViewById<View>(R.id.tableRow12)
        val toggleButton =
            requireActivity().findViewById<ImageButton>(R.id.formula_editor_keyboard_functional_button_toggle)
        val isVisible = row1.visibility == View.VISIBLE
        row1.visibility = if (isVisible) View.GONE else View.VISIBLE
        row2.visibility = if (isVisible) View.GONE else View.VISIBLE
        toggleButton.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(),
                if (isVisible) R.drawable.ic_keyboard_toggle_caret_up else R.drawable.ic_keyboard_toggle_caret_down
            )
        )
        toggleFormulaEditorSpace(isVisible)
    }

    private fun toggleFormulaEditorSpace(isVisible: Boolean) {
        val keyboard = requireActivity().findViewById<View>(R.id.formula_editor_keyboardview)
        val brickAndFormula = requireActivity().findViewById<View>(R.id.formula_editor_brick_and_formula)
        val keyboardLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1F
        )
        val formulaLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1F
        )
        if (isVisible) {
            val row1 = requireActivity().findViewById<View>(R.id.tableRow11)
            val row2 = requireActivity().findViewById<View>(R.id.tableRow12)
            val rowsHeight = row1.height + row2.height
            keyboardLayoutParams.topMargin = rowsHeight
            formulaLayoutParams.bottomMargin = -rowsHeight
        } else {
            keyboardLayoutParams.topMargin = 0
            formulaLayoutParams.bottomMargin = 0
        }
        brickAndFormula.layoutParams = formulaLayoutParams
        keyboard.layoutParams = keyboardLayoutParams
    }

    override fun onStop() {
        super.onStop()
        val activity = activity as AppCompatActivity? ?: return
        activity.supportActionBar?.title = actionBarTitleBuffer
    }

    private val isSelectedTextFirstParamOfRegularExpression: Boolean
        get() = formulaEditorEditText.isSelectedTokenFirstParamOfRegularExpression

    private fun showNewRegexAssistantDialog() {
        val selectedFormulaText = selectedFormulaText
        val builder = TextInputDialog.Builder(requireContext())
        builder.setHint(getString(R.string.string_label))
            .setText(selectedFormulaText)
            .setPositiveButton(
                getString(R.string.ok)
            ) { _: DialogInterface?, textInput: String ->
                addString(textInput)
            }
        val titleId = R.string.formula_editor_dialog_change_regular_expression
        builder.setNeutralButton(R.string.assistant
        ) { _: DialogInterface?, _: Int -> openAssistantDialog() }
        builder.setTitle(titleId)
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun openAssistantDialog() {
        RegularExpressionAssistantDialog(context, parentFragmentManager).createAssistant()
    }

    private fun showNewStringDialog() {
        val selectedFormulaText: String = selectedFormulaText
        val builder = TextInputDialog.Builder(requireContext())
        builder.setHint(getString(R.string.string_label))
            .setText(selectedFormulaText)
            .setPositiveButton(
                getString(R.string.ok)
            ) { _: DialogInterface?, textInput: String ->
                addString(textInput)
            }
        val titleId =
            R.string.formula_editor_dialog_change_text
        builder.setTitle(titleId)
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    fun addString(string: String) {
        val previousString = selectedFormulaText
        val currentProject = projectManager.currentProject
        val currentSprite = projectManager.currentSprite
        val context = context
        if (context != null) {
            var doNotShowWarning = false
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            if (preferences.contains(DO_NOT_SHOW_WARNING)) {
                doNotShowWarning = preferences.getBoolean(DO_NOT_SHOW_WARNING, false)
            }
            if (!doNotShowWarning && recognizedFormulaInText(
                    string,
                    context,
                    currentProject,
                    currentSprite
                )
            ) {
                showFormulaInTextWarning()
            }
        }
        if (!previousString.matches(Regex("\\s*"))) {
            overrideSelectedText(string)
        } else {
            addStringToActiveFormula(string)
        }
        updateButtonsOnKeyboardAndInvalidateOptionsMenu()
    }

    @VisibleForTesting
    fun recognizedFormulaInText(
        string: String,
        context: Context,
        project: Project,
        sprite: Sprite
    ): Boolean {
        var recognizedFormula = false
        val formulasWithParams = context.resources.getStringArray(R.array.formulas_with_params)
        val formulasWithoutParams =
            context.resources.getStringArray(R.array.formulas_without_params)
        for (formulaWithParams in formulasWithParams) {
            if (string.matches(Regex(".*$formulaWithParams\\(.+\\).*"))) {
                recognizedFormula = true
                break
            }
        }
        for (formulaWithoutParams in formulasWithoutParams) {
            if (string.contains(formulaWithoutParams!!)) {
                recognizedFormula = true
                break
            }
        }
        recognizedFormula =
            recognizedFormula or (stringContainsUserVariable(string, project.multiplayerVariables)
                || stringContainsUserVariable(string, project.userVariables)
                || stringContainsUserVariable(string, sprite.userVariables)
                || stringContainsUserList(string, project.userLists)
                || stringContainsUserList(string, sprite.userLists)
                || stringContainsUserDefinedBrickInput(string))
        return recognizedFormula
    }

    private fun stringContainsUserVariable(
        string: String,
        variableList: List<UserVariable>
    ): Boolean {
        for (variable in variableList) {
            if (string.contains(variable.name)) {
                return true
            }
        }
        return false
    }

    private fun stringContainsUserList(string: String, userList: List<UserList>): Boolean {
        for (list in userList) {
            if (string.contains(list.name)) {
                return true
            }
        }
        return false
    }

    private fun stringContainsUserDefinedBrickInput(string: String): Boolean {
        if (formulaBrick.script.scriptBrick is UserDefinedReceiverBrick) {
            val userDefinedBrickInputs =
                (formulaBrick.script.scriptBrick as UserDefinedReceiverBrick).userDefinedBrick.userDefinedBrickInputs
            for (variable in userDefinedBrickInputs) {
                if (string.contains(variable.name)) {
                    return true
                }
            }
        }
        return false
    }

    private fun showFormulaInTextWarning() {
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.warning)
            .setMessage(R.string.warning_formula_recognized)
            .setPositiveButton(R.string.ok, null)
            .setNegativeButton(R.string.do_not_show_again) { _: DialogInterface?, _: Int ->
                PreferenceManager.getDefaultSharedPreferences(
                    context
                )
                    .edit()
                    .putBoolean(DO_NOT_SHOW_WARNING, true)
                    .apply()
            }
            .create()
            .show()
    }

    private fun showComputeDialog() {
        val internFormulaParser = formulaEditorEditText.formulaParser
        val formulaElement = internFormulaParser.parseFormula(generateScope())
        if (formulaElement == null) {
            if (internFormulaParser.errorTokenIndex >= 0) {
                formulaEditorEditText.setParseErrorCursorAndSelection()
            }
            return
        }
        val resourcesSet = ResourcesSet()
        formulaElement.addRequiredResources(resourcesSet)
        val requiredRuntimePermissions = BrickResourcesToRuntimePermissions.translate(resourcesSet)
        object : RequiresPermissionTask(
            REQUEST_PERMISSIONS_COMPUTE_DIALOG,
            requiredRuntimePermissions,
            R.string.runtime_permission_general
        ) {
            override fun task() {
                if (resourcesSet.contains(Brick.SENSOR_GPS)) {
                    val sensorHandler = SensorHandler.getInstance(activity)
                    sensorHandler.setLocationManager(activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
                    if (!SensorHandler.gpsAvailable()) {
                        val checkIntent = Intent()
                        checkIntent.action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
                        startActivityForResult(checkIntent, REQUEST_GPS)
                        return
                    }
                }
                val formulaToCompute = Formula(formulaElement)
                val computeDialog = FormulaEditorComputeDialog(activity, generateScope())
                computeDialog.setFormula(formulaToCompute)
                computeDialog.show()
            }
        }.execute(activity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_GPS && resultCode == AppCompatActivity.RESULT_CANCELED && SensorHandler.gpsAvailable()) {
            showComputeDialog()
        } else {
            ToastUtil.showError(activity, R.string.error_gps_not_available)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        currentMenu = menu
        for (index in 0 until menu.size()) {
            menu.getItem(index).isVisible = false
        }
        val undo = menu.findItem(R.id.menu_undo)
        if (!formulaEditorEditText.history.undoIsPossible()) {
            undo.setIcon(R.drawable.icon_undo_disabled)
            undo.isEnabled = false
        } else {
            undo.setIcon(R.drawable.icon_undo)
            undo.isEnabled = true
        }
        val redo = menu.findItem(R.id.menu_redo)
        if (!formulaEditorEditText.history.redoIsPossible()) {
            redo.setIcon(R.drawable.icon_redo_disabled)
            redo.isEnabled = false
        } else {
            redo.setIcon(R.drawable.icon_redo)
            redo.isEnabled = true
        }
        menu.findItem(R.id.menu_undo).isVisible = true
        menu.findItem(R.id.menu_redo).isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_formulaeditor, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_undo -> formulaEditorEditText.undo()
            R.id.menu_redo -> formulaEditorEditText.redo()
        }
        updateButtonsOnKeyboardAndInvalidateOptionsMenu()
        return super.onOptionsItemSelected(item)
    }

    fun setInputFormula(formulaField: FormulaField?, mode: Int) {
        when (mode) {
            SET_FORMULA_ON_CREATE_VIEW -> {
                formulaEditorEditText.enterNewFormula(
                    UndoState(
                        currentFormula.internFormulaState,
                        formulaField
                    )
                )
                refreshFormulaPreviewString(formulaEditorEditText.stringFromInternFormula)
            }
            SET_FORMULA_ON_RETURN_FROM_VISUAL_PLACEMENT, SET_FORMULA_ON_RETURN_FROM_COLOR_PICKER, SET_FORMULA_ON_SWITCH_EDIT_TEXT -> {
                val newFormula = formulaField?.let { formulaBrick.getFormulaWithBrickField(it) }
                if (currentFormula === newFormula && formulaEditorEditText.hasChanges()) {
                    formulaEditorEditText.quickSelect()
                } else if (formulaEditorEditText.hasChanges()) {
                    confirmSwitchEditTextTimeStamp[0] = confirmSwitchEditTextTimeStamp[1]
                    confirmSwitchEditTextTimeStamp[1] = System.currentTimeMillis()
                    confirmSwitchEditTextCounter++
                    if (!saveFormulaIfPossible()) {
                        return
                    }
                }
                val undo = currentMenu!!.findItem(R.id.menu_undo)
                if (undo != null) {
                    undo.setIcon(R.drawable.icon_undo_disabled)
                    undo.isEnabled = false
                }
                val redo = currentMenu!!.findItem(R.id.menu_redo)
                redo.setIcon(R.drawable.icon_redo_disabled)
                redo.isEnabled = false
                formulaEditorEditText.endEdit()
                if (formulaField != null) {
                    currentBrickField = formulaField
                }
                currentFormula = newFormula ?: return
                formulaEditorEditText.enterNewFormula(
                    UndoState(
                        currentFormula.internFormulaState,
                        currentBrickField
                    )
                )
                refreshFormulaPreviewString(formulaEditorEditText.stringFromInternFormula)
            }
        }
    }

    private fun generateScope(): Scope {
        val project = projectManager.currentProject
        val sprite = projectManager.currentSprite
        var sequence: ScriptSequenceAction? = null
        val script = formulaBrick.script
        if (script is UserDefinedScript) {
            val brick = script.getScriptBrick() as UserDefinedReceiverBrick
            val inputs = brick.userDefinedBrick.userDefinedBrickInputs
            val inputNames: MutableList<Any> = ArrayList()
            for (input in inputs) {
                inputNames.add(convertUserDefinedBrickInputToUserVariable(input))
            }
            sequence = ScriptSequenceAction(script)
            (sequence.script as UserDefinedScript).setUserDefinedBrickInputs(inputNames)
        }
        return Scope(project, sprite, sequence)
    }

    private fun convertUserDefinedBrickInputToUserVariable(input: UserDefinedBrickInput): UserVariable {
        return UserVariable(
            input.name,
            input.value.getUserFriendlyString(
                AndroidStringProvider(context),
                null
            )
        )
    }

    private fun saveFormulaIfPossible(): Boolean {
        val formulaToParse = formulaEditorEditText.formulaParser
        val formulaParseTree = formulaToParse.parseFormula(generateScope())
        return when (formulaToParse.errorTokenIndex) {
            InternFormulaParser.PARSER_OK -> saveValidFormula(formulaParseTree)
            InternFormulaParser.PARSER_STACK_OVERFLOW -> checkReturnWithoutSaving(
                InternFormulaParser.PARSER_STACK_OVERFLOW
            )
            InternFormulaParser.PARSER_NO_INPUT -> {
                if (currentBrickField is BrickField && BrickField.isExpectingStringValue(
                        currentBrickField as BrickField?
                    )
                ) {
                    return saveValidFormula(
                        FormulaElement(
                            FormulaElement.ElementType.STRING,
                            "",
                            null
                        )
                    )
                }
                formulaEditorEditText.setParseErrorCursorAndSelection()
                checkReturnWithoutSaving(InternFormulaParser.PARSER_INPUT_SYNTAX_ERROR)
            }
            else -> {
                formulaEditorEditText.setParseErrorCursorAndSelection()
                checkReturnWithoutSaving(InternFormulaParser.PARSER_INPUT_SYNTAX_ERROR)
            }
        }
    }

    private fun saveValidFormula(formulaElement: FormulaElement): Boolean {
        currentFormula.root = formulaElement
        formulaEditorEditText.formulaSaved()
        hasFormulaBeenChanged = true
        return true
    }

    private fun checkReturnWithoutSaving(errorType: Int): Boolean {
        return if (System.currentTimeMillis() <= confirmSwitchEditTextTimeStamp[0] + TIME_WINDOW
            && confirmSwitchEditTextCounter > 1
        ) {
            confirmSwitchEditTextTimeStamp[0] = 0
            confirmSwitchEditTextTimeStamp[1] = 0
            confirmSwitchEditTextCounter = 0
            ToastUtil.showSuccess(activity, R.string.formula_editor_changes_discarded)
            true
        } else {
            when (errorType) {
                InternFormulaParser.PARSER_INPUT_SYNTAX_ERROR -> ToastUtil.showError(
                    activity,
                    R.string.formula_editor_parse_fail
                )
                InternFormulaParser.PARSER_STACK_OVERFLOW -> ToastUtil.showError(
                    activity,
                    R.string.formula_editor_parse_fail_formula_too_long
                )
            }
            false
        }
    }

    private fun hasFileChanged(): Boolean {
        val currentCodeFile = File(
            projectManager.currentProject.directory,
            Constants.CODE_XML_FILE_NAME
        )
        val undoCodeFile = File(
            projectManager.currentProject.directory,
            Constants.UNDO_CODE_XML_FILE_NAME
        )
        if (currentCodeFile.exists() && undoCodeFile.exists()) {
            try {
                val currentFile = Files.readLines(currentCodeFile, StandardCharsets.UTF_8)
                val undoFile = Files.readLines(undoCodeFile, StandardCharsets.UTF_8)
                return currentFile != undoFile
            } catch (exception: IOException) {
                Log.e(TAG, "Comparing project files failed.", exception)
            }
        }
        return false
    }

    fun exitFormulaEditorFragment() {
        if (formulaEditorEditText.isPopupMenuVisible) {
            formulaEditorEditText.dismissPopupMenu()
            return
        }
        (activity as SpriteActivity?)?.setUndoMenuItemVisibility(false)
        if (hasFormulaBeenChanged || formulaEditorEditText.hasChanges()) {
            hasFormulaBeenChanged = if (saveFormulaIfPossible()) {
                false
            } else {
                return
            }
        }
        onUserDismiss()

        val fragment = requireActivity().supportFragmentManager.findFragmentByTag(ScriptFragment.TAG) as
                ScriptFragment? ?: return

        XstreamSerializer.getInstance().saveProject(projectManager.currentProject)
        if (hasFileChanged() || fragment.checkVariables()) {
            (activity as SpriteActivity?)?.setUndoMenuItemVisibility(true)
        }
    }

    @VisibleForTesting
    fun endFormulaEditor() {
        if (formulaEditorEditText.hasChanges()) {
            if (saveFormulaIfPossible()) {
                hasFormulaBeenChanged = false
                onUserDismiss()
            }
        } else {
            onUserDismiss()
        }
    }

    fun refreshFormulaPreviewString(newString: String?) {
        updateBrickView()
        formulaBrick.getTextView(currentBrickField).text = newString
    }

    private fun showCategoryListFragment(tag: String, actionbarResId: Int) {
        val fragment = CategoryListFragment()
        val bundle = Bundle()
        bundle.putString(
            CategoryListFragment.ACTION_BAR_TITLE_BUNDLE_ARGUMENT,
            requireActivity().getString(actionbarResId)
        )
        bundle.putString(CategoryListFragment.FRAGMENT_TAG_BUNDLE_ARGUMENT, tag)
        fragment.arguments = bundle
        fragment.onPrepareOptionsMenu(currentMenu!!)
        parentFragmentManager.beginTransaction()
            .hide(parentFragmentManager.findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG)!!)
            .add(R.id.fragment_container, fragment, tag)
            .addToBackStack(tag)
            .commit()
    }

    private fun showDataFragment() {
        val fragment = DataListFragment()
        fragment.setFormulaEditorDataInterface(this)
        val bundle = Bundle()
        bundle.putSerializable(
            DataListFragment.PARENT_SCRIPT_BRICK_BUNDLE_ARGUMENT,
            formulaBrick.script.scriptBrick
        )
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .hide(parentFragmentManager.findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG)!!)
            .add(R.id.fragment_container, fragment, DataListFragment.TAG)
            .addToBackStack(DataListFragment.TAG)
            .commit()
    }

    override fun onVariableRenamed(previousName: String?, newName: String?) {
        formulaEditorEditText.updateVariableReferences(previousName, newName)
    }

    override fun onListRenamed(previousName: String?, newName: String?) {
        formulaEditorEditText.updateListReferences(previousName, newName)
    }

    override fun onGlobalLayout() {
        requireView().viewTreeObserver.removeOnGlobalLayoutListener(this)
        val brickRect = Rect()
        val keyboardRec = Rect()
        formulaEditorBrick.getGlobalVisibleRect(brickRect)
        formulaEditorKeyboard.getGlobalVisibleRect(keyboardRec)
    }

    fun addResourceToActiveFormula(resource: Int) {
        formulaEditorEditText.handleKeyEvent(resource, "")
        val requiresCollisionPolygons =
            (resource == R.string.formula_editor_function_collides_with_edge
                || resource == R.string.formula_editor_function_touched)
        if (requiresCollisionPolygons) {
            projectManager.currentSprite.createCollisionPolygons()
        }
    }

    fun addUserListToActiveFormula(userListName: String?) {
        formulaEditorEditText.handleKeyEvent(
            InternFormulaKeyboardAdapter.FORMULA_EDITOR_USER_LIST_RESOURCE_ID,
            userListName
        )
    }

    private fun addUserVariableToActiveFormula(userVariableName: String?) {
        formulaEditorEditText.handleKeyEvent(
            InternFormulaKeyboardAdapter.FORMULA_EDITOR_USER_VARIABLE_RESOURCE_ID,
            userVariableName
        )
    }

    private fun addUserDefinedBrickInputToActiveFormula(userDefinedBrickInput: String?) {
        formulaEditorEditText.handleKeyEvent(
            InternFormulaKeyboardAdapter.FORMULA_EDITOR_USER_DEFINED_BRICK_INPUT_RESOURCE_ID,
            userDefinedBrickInput
        )
    }

    fun addCollideFormulaToActiveFormula(spriteName: String?) {
        formulaEditorEditText.handleKeyEvent(
            InternFormulaKeyboardAdapter.FORMULA_EDITOR_COLLIDE_RESOURCE_ID,
            spriteName
        )
    }

    private fun addStringToActiveFormula(string: String?) {
        formulaEditorEditText.handleKeyEvent(R.id.formula_editor_keyboard_string, string)
    }

    val selectedFormulaText: String
        get() = formulaEditorEditText.selectedTextFromInternFormula

    fun overrideSelectedText(string: String?) {
        formulaEditorEditText.overrideSelectedText(string)
    }

    fun setChosenCategoryItem(chosenCategoryItem: CategoryListItem?) {
        this.chosenCategoryItem = chosenCategoryItem
    }

    fun setChosenUserDataItem(chosenUserDataItem: UserData<*>?) {
        this.chosenUserDataItem = chosenUserDataItem
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            val actionBar = (activity as AppCompatActivity?)?.supportActionBar
            val isRestoringPreviouslyDestroyedActivity = actionBar == null
            if (!isRestoringPreviouslyDestroyedActivity) {
                actionBar?.setTitle(R.string.formula_editor_title)
                hideBottomBar(activity)
                updateButtonsOnKeyboardAndInvalidateOptionsMenu()
                updateBrickView()
            }
            if (chosenCategoryItem != null) {
                chosenCategoryItem?.nameResId?.let { addResourceToActiveFormula(it) }
                chosenCategoryItem = null
            }
            if (chosenUserDataItem != null) {
                when (chosenUserDataItem) {
                    is UserVariable -> {
                        addUserVariableToActiveFormula((chosenUserDataItem as UserVariable).name)
                    }
                    is UserList -> {
                        addUserListToActiveFormula((chosenUserDataItem as UserList).name)
                    }
                    is UserDefinedBrickInput -> {
                        addUserDefinedBrickInputToActiveFormula((chosenUserDataItem as UserDefinedBrickInput).name)
                    }
                }
                chosenUserDataItem = null
            }
        }
    }

    fun updateButtonsOnKeyboardAndInvalidateOptionsMenu() {
        requireActivity().invalidateOptionsMenu()
        val backspaceOnKeyboard =
            requireActivity().findViewById<ImageButton>(R.id.formula_editor_keyboard_delete)
        if (!formulaEditorEditText.isThereSomethingToDelete) {
            backspaceOnKeyboard.imageAlpha = 255 / 3
            backspaceOnKeyboard.isEnabled = false
        } else {
            backspaceOnKeyboard.imageAlpha = 255
            backspaceOnKeyboard.isEnabled = true
        }
    }

    val indexOfCorrespondingRegularExpression: Int
        get() = formulaEditorEditText.indexOfCorrespondingRegularExpression

    fun setSelectionToFirstParamOfRegularExpressionAtInternalIndex(indexOfRegularExpression: Int) {
        formulaEditorEditText.setSelectionToFirstParamOfRegularExpressionAtInternalIndex(
            indexOfRegularExpression
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity.removeTabLayout()
    }

    override fun onDetach() {
        activity.addTabLayout(SpriteActivity.FRAGMENT_SCRIPTS)
        super.onDetach()
    }

    companion object {
        val TAG: String = FormulaEditorFragment::class.java.simpleName
        private const val SET_FORMULA_ON_CREATE_VIEW = 0
        private const val SET_FORMULA_ON_SWITCH_EDIT_TEXT = 1
        private const val SET_FORMULA_ON_RETURN_FROM_VISUAL_PLACEMENT = 2
        private const val SET_FORMULA_ON_RETURN_FROM_COLOR_PICKER = 3
        private const val TIME_WINDOW = 2000
        const val REQUEST_GPS = 1
        const val REQUEST_PERMISSIONS_COMPUTE_DIALOG = 701

        @JvmField
        val FORMULA_EDITOR_FRAGMENT_TAG: String = FormulaEditorFragment::class.java.simpleName
        const val FORMULA_BRICK_BUNDLE_ARGUMENT = "formula_brick"
        const val FORMULA_FIELD_BUNDLE_ARGUMENT = "formula_field"
        const val DO_NOT_SHOW_WARNING = "DO_NOT_SHOW_WARNING"
        private fun showFragment(
            context: Context,
            formulaBrick: FormulaBrick,
            formulaField: FormulaField,
            showCustomView: Boolean
        ) {
            val activity = UiUtils.getActivityFromContextWrapper(context) ?: return
            var formulaEditorFragment = activity.supportFragmentManager
                .findFragmentByTag(FORMULA_EDITOR_FRAGMENT_TAG) as FormulaEditorFragment?
            if (formulaEditorFragment == null) {
                formulaEditorFragment = FormulaEditorFragment()
                formulaEditorFragment.showCustomView = showCustomView
                val bundle = Bundle()
                bundle.putSerializable(FORMULA_BRICK_BUNDLE_ARGUMENT, formulaBrick)
                bundle.putSerializable(FORMULA_FIELD_BUNDLE_ARGUMENT, formulaField)
                formulaEditorFragment.arguments = bundle
                activity.supportFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        formulaEditorFragment,
                        FORMULA_EDITOR_FRAGMENT_TAG
                    )
                    .addToBackStack(FORMULA_EDITOR_FRAGMENT_TAG)
                    .commit()
                hideBottomBar(activity)
            } else {
                formulaEditorFragment.showCustomView = false
                formulaEditorFragment.updateBrickView()
                formulaEditorFragment.setInputFormula(formulaField, SET_FORMULA_ON_SWITCH_EDIT_TEXT)
            }
        }

        @JvmStatic
        fun showFragment(context: Context, formulaBrick: FormulaBrick, formulaField: FormulaField) {
            showFragment(context, formulaBrick, formulaField, false)
        }

        @JvmStatic
        fun showCustomFragment(
            context: Context,
            formulaBrick: FormulaBrick,
            formulaField: FormulaField
        ) {
            showFragment(context, formulaBrick, formulaField, true)
        }
    }
}