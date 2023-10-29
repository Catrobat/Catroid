/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.ui.recyclerview.fragment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView

import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.LegoSensorType
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.dialogs.LegoSensorPortConfigDialog
import org.catrobat.catroid.ui.dialogs.regexassistant.RegularExpressionAssistantDialog
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter.CategoryListItem
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter.CategoryListItemType
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.settingsfragments.RaspberryPiSettingsFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.utils.AddUserListDialog
import org.catrobat.catroid.utils.MobileServiceAvailability
import org.koin.java.KoinJavaComponent.get
import org.koin.java.KoinJavaComponent.inject
import java.util.Locale

class CategoryListFragment : Fragment(), CategoryListRVAdapter.OnItemClickListener {

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parent = inflater.inflate(R.layout.fragment_list_view, container, false)
        recyclerView = parent.findViewById(R.id.recycler_view)
        setHasOptionsMenu(true)
        return parent
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initializeAdapter()
    }

    override fun onResume() {
        super.onResume()
        val arguments = arguments ?: return
        val appCompatActivity = activity as AppCompatActivity? ?: return
        val supportActionBar = appCompatActivity.supportActionBar
        if (supportActionBar != null) {
            val title = arguments.getString(ACTION_BAR_TITLE_BUNDLE_ARGUMENT)
            supportActionBar.title = title
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        for (index in 0 until menu.size()) {
            menu.getItem(index).isVisible = false
        }
        val appCompatActivity = activity as AppCompatActivity? ?: return
        appCompatActivity.menuInflater.inflate(R.menu.menu_formulareditor_category, menu)
        val supportActionBar = appCompatActivity.supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onItemClick(item: CategoryListItem?) {
        when (item!!.type) {
            CategoryListRVAdapter.NXT -> showLegoSensorPortConfigDialog(
                item.nameResId,
                Constants.NXT
            )

            CategoryListRVAdapter.EV3 -> showLegoSensorPortConfigDialog(
                item.nameResId,
                Constants.EV3
            )

            CategoryListRVAdapter.COLLISION -> showSelectSpriteDialog()
            CategoryListRVAdapter.DEFAULT -> if (LIST_FUNCTIONS.contains(item.nameResId)) {
                onUserListFunctionSelected(item)
            } else if (R.string.formula_editor_function_regex_assistant == item.nameResId) {
                regularExpressionAssistantActivityOnButtonClick()
            } else {
                val formulaEditorFragment =
                    childFragmentManager.findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG) as
                        FormulaEditorFragment?
                formulaEditorFragment?.setChosenCategoryItem(item)
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.wiki_help) {
            onOptionsMenuClick(tag)
        }
        return true
    }

    fun onOptionsMenuClick(tag: String?) {
        val language: String = getLanguage(requireActivity())
        when (tag) {
            FUNCTION_TAG -> startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(Constants.CATROBAT_FUNCTIONS_WIKI_URL + language)
                )
            )

            LOGIC_TAG -> startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(Constants.CATROBAT_LOGIC_WIKI_URL + language)
                )
            )

            OBJECT_TAG -> startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(Constants.CATROBAT_OBJECT_WIKI_URL + language)
                )
            )

            SENSOR_TAG -> startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(Constants.CATROBAT_SENSORS_WIKI_URL + language)
                )
            )
        }
    }

    fun getHelpUrl(tag: String?, activity: SpriteActivity): String? {
        val language: String = getLanguage(activity)
        when (tag) {
            FUNCTION_TAG -> return Constants.CATROBAT_FUNCTIONS_WIKI_URL + language
            LOGIC_TAG -> return Constants.CATROBAT_LOGIC_WIKI_URL + language
            OBJECT_TAG -> return Constants.CATROBAT_OBJECT_WIKI_URL + language
            SENSOR_TAG -> return Constants.CATROBAT_SENSORS_WIKI_URL + language
        }
        return null
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    private fun getLanguage(activity: Activity): String {
        var language = "?language="
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(activity.applicationContext)
        val languageTag = sharedPreferences.getString(SharedPreferenceKeys
                                                                   .LANGUAGE_TAG_KEY, "")
        val mLocale: Locale = if (languageTag == SharedPreferenceKeys.DEVICE_LANGUAGE) {
            Locale.forLanguageTag(CatroidApplication.defaultSystemLanguage)
        } else {
            if (listOf(*SharedPreferenceKeys.LANGUAGE_TAGS).contains(languageTag))
                languageTag?.let { Locale.forLanguageTag(it) }
            else Locale.forLanguageTag(CatroidApplication.defaultSystemLanguage)
        }!!
        language += mLocale.language
        return language
    }

    private fun addResourceToActiveFormulaInFormulaEditor(categoryListItem: CategoryListItem?): FormulaEditorFragment? {
        val formulaEditorFragment: FormulaEditorFragment? = childFragmentManager
            .findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG) as FormulaEditorFragment?
        formulaEditorFragment?.addResourceToActiveFormula(categoryListItem!!.nameResId)

        return formulaEditorFragment
    }

    private fun addResourceToActiveFormulaInFormulaEditor(
        categoryListItem: CategoryListItem,
        lastUserList: UserList
    ) {
        addResourceToActiveFormulaInFormulaEditor(categoryListItem)!!.addUserListToActiveFormula(
            lastUserList.name
        )
        requireActivity().onBackPressed()
    }

    private fun onUserListFunctionSelected(item: CategoryListItem) {
        val activity = activity
        val builder = TextInputDialog.Builder(requireActivity())
        val projectUserList = projectManager.currentProject.userLists
        val spriteUserList = projectManager.currentSprite.userLists
        insertLastUserListToActiveFormula(
            item, projectUserList, spriteUserList,
            activity, builder
        )
    }

    @VisibleForTesting
    fun insertLastUserListToActiveFormula(
        categoryListItem: CategoryListItem,
        projectUserList: List<UserList>,
        spriteUserList: List<UserList>,
        activity: FragmentActivity?,
        builder: TextInputDialog.Builder
    ) {
        if (spriteUserList.isEmpty() && projectUserList.isEmpty()) {
            showNewUserListDialog(
                categoryListItem, projectUserList, spriteUserList,
                activity, builder
            )
            return
        }
        if (spriteUserList.isNotEmpty()) {
            addResourceToActiveFormulaInFormulaEditor(
                categoryListItem,
                spriteUserList[spriteUserList.size - 1]
            )
            return
        }
        addResourceToActiveFormulaInFormulaEditor(
            categoryListItem,
            projectUserList[projectUserList.size - 1]
        )
    }

    private fun getRegularExpressionItem(): CategoryListItem? {
        var regexItem: CategoryListItem? = null
        val itemList = getFunctionItems()
        for (item in itemList) {
            if (item.nameResId == R.string.formula_editor_function_regex) {
                regexItem = item
            }
        }
        return regexItem
    }

    private fun regularExpressionAssistantActivityOnButtonClick() {
        val indexOfCorrespondingRegularExpression: Int
        val formulaEditorFragment = getFormulaEditorFragment()
        if (formulaEditorFragment != null) {
            indexOfCorrespondingRegularExpression =
                formulaEditorFragment.indexOfCorrespondingRegularExpression
            if (indexOfCorrespondingRegularExpression >= 0) {
                formulaEditorFragment.setSelectionToFirstParamOfRegularExpressionAtInternalIndex(
                    indexOfCorrespondingRegularExpression
                )
            } else {
                addResourceToActiveFormulaInFormulaEditor(getRegularExpressionItem())
            }
            requireActivity().onBackPressed()
            openRegularExpressionAssistant()
        }
    }

    private fun getFormulaEditorFragment(): FormulaEditorFragment? {
        return childFragmentManager
            .findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG)
            as FormulaEditorFragment?
    }

    private fun openRegularExpressionAssistant() {
        RegularExpressionAssistantDialog(context, childFragmentManager).createAssistant()
    }

    private fun showNewUserListDialog(
        categoryListItem: CategoryListItem,
        projectUserList: List<UserList>,
        spriteUserList: List<UserList>,
        activity: FragmentActivity?,
        builder: TextInputDialog.Builder
    ) {
        val userListDialog = AddUserListDialog(builder)
        userListDialog.show(
            requireActivity().getString(R.string.data_label),
            requireActivity().getString(R.string.ok),
            object : AddUserListDialog.Callback {
                override fun onPositiveButton(
                    dialog: DialogInterface,
                    textInput: String
                ) {
                    val userList = UserList(textInput)
                    userListDialog.addUserList(
                        dialog,
                        userList,
                        projectUserList,
                        spriteUserList
                    )
                    addResourceToActiveFormulaInFormulaEditor(
                        categoryListItem,
                        userList
                    )
                }

                override fun onNegativeButton() {
                    activity!!.onBackPressed()
                }
            })
    }

    private fun showLegoSensorPortConfigDialog(itemNameResId: Int, @LegoSensorType type: Int) {
        LegoSensorPortConfigDialog.Builder(requireContext(), type, itemNameResId)
            .setPositiveButton(
                getString(R.string.ok),
                LegoSensorPortConfigDialog.OnClickListener { dialog: DialogInterface?, selectedPort: Int, selectedSensor: Enum<*>? ->
                    if (type == Constants.NXT) {
                        SettingsFragment.setLegoMindstormsNXTSensorMapping(
                            activity,
                            selectedSensor as NXTSensor.Sensor?,
                            SettingsFragment.NXT_SENSORS[selectedPort]
                        )
                    } else if (type == Constants.EV3) {
                        SettingsFragment.setLegoMindstormsEV3SensorMapping(
                            activity,
                            selectedSensor as EV3Sensor.Sensor?,
                            SettingsFragment.EV3_SENSORS[selectedPort]
                        )
                    }
                    val formulaEditor = childFragmentManager
                        .findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG) as FormulaEditorFragment?
                    val sensorPortsId =
                        if (type == Constants.NXT) R.array.formula_editor_nxt_ports else R.array.formula_editor_ev3_ports
                    val sensorPorts = resources.obtainTypedArray(sensorPortsId)
                    try {
                        val resourceId = sensorPorts.getResourceId(selectedPort, 0)
                        if (resourceId != 0) {
                            formulaEditor!!.addResourceToActiveFormula(resourceId)
                            formulaEditor.updateButtonsOnKeyboardAndInvalidateOptionsMenu()
                        }
                    } finally {
                        sensorPorts.recycle()
                    }
                    requireActivity().onBackPressed()
                })
            .show()
    }

    private fun showSelectSpriteDialog() {
        val currentSprite = projectManager.currentSprite
        val sprites = projectManager.currentlyEditedScene.spriteList
        val selectableSprites: MutableList<Sprite> = ArrayList()
        for (sprite in sprites) {
            selectableSprites.add(sprite)
        }
        val selectableSpriteNames = arrayOfNulls<String>(selectableSprites.size)
        for (i in selectableSprites.indices) {
            selectableSpriteNames[i] = selectableSprites[i].name
        }
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.formula_editor_function_collision)
            .setItems(
                selectableSpriteNames,
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    val selectedSprite = selectableSprites[which]
                    currentSprite.createCollisionPolygons()
                    selectedSprite.createCollisionPolygons()
                    (childFragmentManager
                        .findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG) as
                        FormulaEditorFragment?)
                        ?.addCollideFormulaToActiveFormula(selectedSprite.name)
                    requireActivity().onBackPressed()
                })
            .show()
    }

    private fun initializeAdapter() {
        val argument = requireArguments().getString(FRAGMENT_TAG_BUNDLE_ARGUMENT)
        val items: List<CategoryListItem> = if (OBJECT_TAG == argument) {
            getObjectItems()
        } else if (FUNCTION_TAG == argument) {
            getFunctionItems()
        } else if (LOGIC_TAG == argument) {
            getLogicItems()
        } else if (SENSOR_TAG == argument) {
            getSensorItems()
        } else {
            throw IllegalArgumentException("Argument for CategoryListFragment null or unknown:" +
                " $argument")
        }
        val adapter = CategoryListRVAdapter(items)
        adapter.setOnItemClickListener(this)
        recyclerView!!.adapter = adapter
    }

    private fun addHeader(
        subCategory: List<CategoryListItem>,
        header: String
    ): List<CategoryListItem> {
        subCategory[0].header = header
        return subCategory
    }

    private fun toCategoryListItems(nameResIds: List<Int>): MutableList<CategoryListItem> {
        return toCategoryListItems(nameResIds, null, CategoryListRVAdapter.DEFAULT)
    }

    private fun toCategoryListItems(
        nameResIds: List<Int>,
        paramResIds: List<Int>
    ): List<CategoryListItem> {
        return toCategoryListItems(nameResIds, paramResIds, CategoryListRVAdapter.DEFAULT)
    }

    private fun toCategoryListItems(
        nameResIds: List<Int>,
        @CategoryListItemType type: Int
    ): List<CategoryListItem> {
        return toCategoryListItems(nameResIds, null, type)
    }

    private fun toCategoryListItems(
        nameResIds: List<Int>, paramResIds: List<Int>?,
        @CategoryListItemType type: Int
    ): MutableList<CategoryListItem> {
        require(!(paramResIds != null && paramResIds.size != nameResIds.size)) {
            "Sizes of paramResIds and nameResIds parameters do not fit" }
        val result: MutableList<CategoryListItem> = ArrayList()
        for (i in nameResIds.indices) {
            var param = ""
            if (paramResIds != null) {
                param = getString(paramResIds[i])
            }
            result.add(CategoryListItem(nameResIds[i], getString(nameResIds[i]) + param, type))
        }
        return result
    }

    private fun getObjectItems(): List<CategoryListItem> {
        val result: MutableList<CategoryListItem> = ArrayList()
        result.addAll(getObjectGeneralPropertiesItems())
        result.addAll(getObjectPhysicalPropertiesItems())
        return result
    }

    private fun getFunctionItems(): List<CategoryListItem> {
        val result: MutableList<CategoryListItem> = ArrayList()
        result.addAll(
            addHeader(
                toCategoryListItems(MATH_FUNCTIONS, MATH_PARAMS),
                getString(R.string.formula_editor_functions_maths)
            )
        )
        result.addAll(
            addHeader(
                toCategoryListItems(STRING_FUNCTIONS, STRING_PARAMS),
                getString(R.string.formula_editor_functions_strings)
            )
        )
        result.addAll(
            addHeader(
                toCategoryListItems(LIST_FUNCTIONS, LIST_PARAMS),
                getString(R.string.formula_editor_functions_lists)
            )
        )
        return result
    }

    private fun getLogicItems(): List<CategoryListItem> {
        val result: MutableList<CategoryListItem> = ArrayList()
        result.addAll(
            addHeader(
                toCategoryListItems(LOGIC_BOOL),
                getString(R.string.formula_editor_logic_boolean)
            )
        )
        result.addAll(
            addHeader(
                toCategoryListItems(LOGIC_COMPARISON),
                getString(R.string.formula_editor_logic_comparison)
            )
        )
        return result
    }

    private fun getSensorItems(): List<CategoryListItem> {
        val result: MutableList<CategoryListItem> = ArrayList()
        result.addAll(getNxtSensorItems())
        result.addAll(getEv3SensorItems())
        result.addAll(getPhiroSensorItems())
        result.addAll(getArduinoSensorItems())
        result.addAll(getDroneSensorItems())
        result.addAll(getRaspberrySensorItems())
        result.addAll(getNfcItems())
        result.addAll(getCastGamepadSensorItems())
        result.addAll(getSpeechRecognitionItems())
        result.addAll(getFaceSensorItems())
        result.addAll(getPoseSensorItems())
        result.addAll(getTextSensorItems())
        result.addAll(getObjectDetectionSensorItems())
        result.addAll(getDeviceSensorItems())
        result.addAll(getTouchDetectionSensorItems())
        result.addAll(getDateTimeSensorItems())
        return result
    }

    private fun getObjectGeneralPropertiesItems(): List<CategoryListItem> {
        val resIds: MutableList<Int> = ArrayList(OBJECT_GENERAL_PROPERTIES)
        val currentScene = projectManager.currentlyEditedScene
        val currentSprite = projectManager.currentSprite
        if (currentSprite == currentScene.backgroundSprite) {
            resIds.addAll(OBJECT_BACKGROUND)
        } else {
            resIds.addAll(OBJECT_LOOK)
        }
        val result = toCategoryListItems(resIds)
        result.addAll(
            toCategoryListItems(
                OBJECT_COLOR_COLLISION.subList(1, 2),
                OBJECT_COLOR_PARAMS.subList(1, 2)
            )
        )
        return addHeader(result, getString(R.string.formula_editor_object_look))
    }

    private fun getObjectPhysicalPropertiesItems(): List<CategoryListItem> {
        val result = toCategoryListItems(OBJECT_PHYSICAL_1)
        result.addAll(
            toCategoryListItems(
                OBJECT_PHYSICAL_COLLISION,
                CategoryListRVAdapter.COLLISION
            )
        )
        result.addAll(toCategoryListItems(OBJECT_PHYSICAL_2))
        result.addAll(toCategoryListItems(OBJECT_COLOR_COLLISION, OBJECT_COLOR_PARAMS))
        return addHeader(result, getString(R.string.formula_editor_object_movement))
    }

    private fun getNxtSensorItems(): List<CategoryListItem> {
        return if (SettingsFragment.isMindstormsNXTSharedPreferenceEnabled(
                requireActivity().applicationContext))
            addHeader(
                toCategoryListItems(SENSORS_NXT, CategoryListRVAdapter.NXT),
                getString(R.string.formula_editor_device_lego_nxt)
            )
        else emptyList()
    }

    private fun getEv3SensorItems(): List<CategoryListItem> {
        return if (SettingsFragment.isMindstormsEV3SharedPreferenceEnabled(
                requireActivity().applicationContext))
            addHeader(
                toCategoryListItems(SENSORS_EV3, CategoryListRVAdapter.EV3),
                getString(R.string.formula_editor_device_lego_ev3)
            )
        else emptyList()
    }

    private fun getPhiroSensorItems(): List<CategoryListItem> {
        return if (SettingsFragment.isPhiroSharedPreferenceEnabled(
                requireActivity().applicationContext))
            addHeader(
                toCategoryListItems(SENSORS_PHIRO),
                getString(R.string.formula_editor_device_phiro)
            )
        else emptyList()
    }

    private fun getArduinoSensorItems(): List<CategoryListItem> {
        return if (SettingsFragment.isArduinoSharedPreferenceEnabled(
                requireActivity().applicationContext))
            addHeader(
                toCategoryListItems(SENSORS_ARDUINO, SENSORS_ARDUINO_PARAMS),
                getString(R.string.formula_editor_device_arduino)
            )
        else emptyList()
    }

    private fun getDroneSensorItems(): List<CategoryListItem> {
        return if (SettingsFragment.isDroneSharedPreferenceEnabled(
                requireActivity().applicationContext))
            addHeader(
                toCategoryListItems(SENSORS_DRONE),
                getString(R.string.formula_editor_device_drone)
            )
        else emptyList()
    }

    private fun getRaspberrySensorItems(): List<CategoryListItem> {
        return if (RaspberryPiSettingsFragment.isRaspiSharedPreferenceEnabled(
                requireActivity().applicationContext))
            addHeader(
                toCategoryListItems(SENSORS_RASPBERRY, SENSORS_RASPBERRY_PARAMS),
                getString(R.string.formula_editor_device_raspberry)
            )
        else emptyList()
    }

    private fun getNfcItems(): List<CategoryListItem> {
        return if (SettingsFragment.isNfcSharedPreferenceEnabled(
                requireActivity().applicationContext))
            addHeader(
                toCategoryListItems(SENSORS_NFC),
                getString(R.string.formula_editor_device_nfc)
            )
        else emptyList()
    }

    private fun getCastGamepadSensorItems(): List<CategoryListItem> {
        return if (projectManager.currentProject.isCastProject)
            addHeader(
                toCategoryListItems(SENSORS_CAST_GAMEPAD),
                getString(R.string.formula_editor_device_cast)
            )
        else emptyList()
    }

    private fun getDeviceSensorItems(): List<CategoryListItem> {
        val deviceSensorItems: MutableList<CategoryListItem> = ArrayList(
            toCategoryListItems(SENSORS_DEFAULT)
        )
        val sensorHandler = SensorHandler.getInstance(activity)
        deviceSensorItems.addAll(
            toCategoryListItems(
                SENSORS_COLOR_AT_XY,
                SENSORS_COLOR_AT_XY_PARAMS
            )
        )
        deviceSensorItems.addAll(
            toCategoryListItems(
                SENSORS_COLOR_EQUALS_COLOR,
                SENSORS_COLOR_EQUALS_COLOR_PARAMS
            )
        )
        deviceSensorItems.addAll(
            if (sensorHandler.accelerationAvailable()) toCategoryListItems(
                SENSORS_ACCELERATION
            ) else emptyList()
        )
        deviceSensorItems.addAll(
            if (sensorHandler.inclinationAvailable()) toCategoryListItems(
                SENSORS_INCLINATION
            ) else emptyList()
        )
        deviceSensorItems.addAll(
            if (sensorHandler.compassAvailable()) toCategoryListItems(
                SENSORS_COMPASS
            ) else emptyList()
        )
        deviceSensorItems.addAll(toCategoryListItems(SENSORS_GPS))
        deviceSensorItems.addAll(toCategoryListItems(SENSOR_USER_LANGUAGE))
        return addHeader(deviceSensorItems, getString(R.string.formula_editor_device_sensors))
    }

    private fun getTouchDetectionSensorItems(): List<CategoryListItem> {
        return addHeader(
            toCategoryListItems(SENSORS_TOUCH, SENSORS_TOUCH_PARAMS),
            getString(R.string.formula_editor_device_touch_detection)
        )
    }

    private fun getDateTimeSensorItems(): List<CategoryListItem> {
        return addHeader(
            toCategoryListItems(SENSORS_DATE_TIME),
            getString(R.string.formula_editor_device_date_and_time)
        )
    }

    private fun getSpeechRecognitionItems(): List<CategoryListItem> {
        return if (SettingsFragment.isAISpeechRecognitionSharedPreferenceEnabled(
                requireActivity().applicationContext))
            addHeader(
                toCategoryListItems(SENSORS_SPEECH_RECOGNITION),
                getString(R.string.formula_editor_speech_recognition)
            )
        else emptyList()
    }

    private fun getFaceSensorItems(): List<CategoryListItem> {
        return if (SettingsFragment.isAIFaceDetectionSharedPreferenceEnabled(
                requireActivity().applicationContext))
            addHeader(
                toCategoryListItems(SENSORS_FACE_DETECTION, SENSORS_FACE_DETECTION_PARAMS),
                getString(R.string.formula_editor_device_face_detection)
            )
        else emptyList()
    }

    private fun getPoseSensorItems(): List<CategoryListItem> {
        val mobileServiceAvailability = get(
            MobileServiceAvailability::class.java
        )
        val isHMSAvailable =
            mobileServiceAvailability.isHmsAvailable(requireActivity().applicationContext)
        val isPoseDetectionEnabled = SettingsFragment.isAIPoseDetectionSharedPreferenceEnabled(
            requireActivity().applicationContext
        )
        return if (isPoseDetectionEnabled && isHMSAvailable) {
            addHeader(
                toCategoryListItems(
                    SENSORS_POSE_DETECTION_HUAWEI,
                    SENSORS_POSE_DETECTION_PARAMS_HUAWEI
                ),
                getString(R.string.formula_editor_device_pose_detection)
            )
        } else if (isPoseDetectionEnabled) {
            addHeader(
                toCategoryListItems(
                    SENSORS_POSE_DETECTION,
                    SENSORS_POSE_DETECTION_PARAMS
                ),
                getString(R.string.formula_editor_device_pose_detection)
            )
        } else {
            emptyList()
        }
    }

    private fun getTextSensorItems(): List<CategoryListItem> {
        return if (SettingsFragment.isAITextRecognitionSharedPreferenceEnabled(
                requireActivity().applicationContext))
            addHeader(
                toCategoryListItems(SENSORS_TEXT_RECOGNITION, SENSORS_TEXT_RECOGNITION_PARAMS),
                getString(R.string.formula_editor_device_text_recognition)
            )
        else emptyList()
    }

    private  fun getObjectDetectionSensorItems(): List<CategoryListItem> {
        return if (SettingsFragment.isAIObjectDetectionSharedPreferenceEnabled(
                requireActivity().applicationContext))
            addHeader(
                toCategoryListItems(SENSORS_OBJECT_DETECTION),
                getString(R.string.formula_editor_device_object_recognition)
            )
        else emptyList()
    }


    companion object {
        const val OBJECT_TAG = "objectFragment"
        const val FUNCTION_TAG = "functionFragment"
        const val LOGIC_TAG = "logicFragment"
        const val SENSOR_TAG = "sensorFragment"
        const val ACTION_BAR_TITLE_BUNDLE_ARGUMENT = "actionBarTitle"
        const val FRAGMENT_TAG_BUNDLE_ARGUMENT = "fragmentTag"
        val TAG: String = CategoryListFragment::class.java.simpleName

        private val OBJECT_GENERAL_PROPERTIES = listOf(
            R.string.formula_editor_object_rotation_look,
            R.string.formula_editor_object_transparency,
            R.string.formula_editor_object_brightness,
            R.string.formula_editor_object_color
        )
        private val OBJECT_LOOK = listOf(
            R.string.formula_editor_object_look_number,
            R.string.formula_editor_object_look_name, R.string.formula_editor_object_number_of_looks
        )
        private val OBJECT_BACKGROUND = listOf(
            R.string.formula_editor_object_background_number,
            R.string.formula_editor_object_background_name,
            R.string.formula_editor_object_number_of_backgrounds
        )
        private val OBJECT_PHYSICAL_1 = listOf(
            R.string.formula_editor_object_x,
            R.string.formula_editor_object_y, R.string.formula_editor_object_size,
            R.string.formula_editor_object_rotation, R.string.formula_editor_object_rotation_look,
            R.string.formula_editor_object_layer
        )
        private val OBJECT_PHYSICAL_COLLISION = listOf(R.string.formula_editor_function_collision)
        private val OBJECT_PHYSICAL_2 = listOf(
            R.string.formula_editor_function_collides_with_edge,
            R.string.formula_editor_function_touched,
            R.string.formula_editor_object_x_velocity, R.string.formula_editor_object_y_velocity,
            R.string.formula_editor_object_angular_velocity
        )
        private val MATH_FUNCTIONS = listOf(
            R.string.formula_editor_function_sin,
            R.string.formula_editor_function_cos, R.string.formula_editor_function_tan,
            R.string.formula_editor_function_ln, R.string.formula_editor_function_log,
            R.string.formula_editor_function_pi, R.string.formula_editor_function_sqrt,
            R.string.formula_editor_function_rand, R.string.formula_editor_function_abs,
            R.string.formula_editor_function_round, R.string.formula_editor_function_mod,
            R.string.formula_editor_function_arcsin, R.string.formula_editor_function_arccos,
            R.string.formula_editor_function_arctan, R.string.formula_editor_function_arctan2,
            R.string.formula_editor_function_exp, R.string.formula_editor_function_power,
            R.string.formula_editor_function_floor, R.string.formula_editor_function_ceil,
            R.string.formula_editor_function_max, R.string.formula_editor_function_min,
            R.string.formula_editor_function_if_then_else
        )
        private val MATH_PARAMS = listOf(
            R.string.formula_editor_function_sin_parameter,
            R.string.formula_editor_function_cos_parameter,
            R.string.formula_editor_function_tan_parameter,
            R.string.formula_editor_function_ln_parameter,
            R.string.formula_editor_function_log_parameter,
            R.string.formula_editor_function_pi_parameter,
            R.string.formula_editor_function_sqrt_parameter,
            R.string.formula_editor_function_rand_parameter,
            R.string.formula_editor_function_abs_parameter,
            R.string.formula_editor_function_round_parameter,
            R.string.formula_editor_function_mod_parameter,
            R.string.formula_editor_function_arcsin_parameter,
            R.string.formula_editor_function_arccos_parameter,
            R.string.formula_editor_function_arctan_parameter,
            R.string.formula_editor_function_arctan2_parameter,
            R.string.formula_editor_function_exp_parameter,
            R.string.formula_editor_function_power_parameter,
            R.string.formula_editor_function_floor_parameter,
            R.string.formula_editor_function_ceil_parameter,
            R.string.formula_editor_function_max_parameter,
            R.string.formula_editor_function_min_parameter,
            R.string.formula_editor_function_if_then_else_parameter
        )
        private val STRING_FUNCTIONS = listOf(
            R.string.formula_editor_function_length,
            R.string.formula_editor_function_letter,
            R.string.formula_editor_function_subtext, R.string.formula_editor_function_join,
            R.string.formula_editor_function_join3, R.string.formula_editor_function_regex,
            R.string.formula_editor_function_regex_assistant,
            R.string.formula_editor_function_flatten
        )
        private val STRING_PARAMS = listOf(
            R.string.formula_editor_function_length_parameter,
            R.string.formula_editor_function_letter_parameter,
            R.string.formula_editor_function_subtext_parameter,
            R.string.formula_editor_function_join_parameter,
            R.string.formula_editor_function_join3_parameter,
            R.string.formula_editor_function_regex_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_flatten_parameter
        )
        private val LIST_FUNCTIONS = listOf(
            R.string.formula_editor_function_number_of_items,
            R.string.formula_editor_function_list_item, R.string.formula_editor_function_contains,
            R.string.formula_editor_function_index_of_item, R.string.formula_editor_function_flatten
        )
        private val LIST_PARAMS = listOf(
            R.string.formula_editor_function_number_of_items_parameter,
            R.string.formula_editor_function_list_item_parameter,
            R.string.formula_editor_function_contains_parameter,
            R.string.formula_editor_function_index_of_item_parameter,
            R.string.formula_editor_function_flatten_parameter
        )
        private val LOGIC_BOOL = listOf(
            R.string.formula_editor_logic_and,
            R.string.formula_editor_logic_or, R.string.formula_editor_logic_not,
            R.string.formula_editor_function_true, R.string.formula_editor_function_false
        )
        private val LOGIC_COMPARISON = listOf(
            R.string.formula_editor_logic_equal,
            R.string.formula_editor_logic_notequal, R.string.formula_editor_logic_lesserthan,
            R.string.formula_editor_logic_leserequal, R.string.formula_editor_logic_greaterthan,
            R.string.formula_editor_logic_greaterequal
        )
        private val SENSORS_DEFAULT = listOf(
            R.string.formula_editor_sensor_loudness,
            R.string.formula_editor_function_touched, R.string.formula_editor_sensor_stage_width,
            R.string.formula_editor_sensor_stage_height
        )
        private val OBJECT_COLOR_COLLISION = listOf(
            R.string.formula_editor_function_collides_with_color,
            R.string.formula_editor_function_color_touches_color
        )
        private val OBJECT_COLOR_PARAMS = listOf(
            R.string.formula_editor_function_collides_with_color_parameter,
            R.string.formula_editor_function_color_touches_color_parameter
        )

        private val SENSORS_COLOR_AT_XY = listOf(R.string.formula_editor_sensor_color_at_x_y)
        private val SENSORS_COLOR_AT_XY_PARAMS =
            listOf(R.string.formula_editor_sensor_color_at_x_y_parameter)

        private val SENSORS_COLOR_EQUALS_COLOR =
            listOf(R.string.formula_editor_sensor_color_equals_color)
        private val SENSORS_COLOR_EQUALS_COLOR_PARAMS =
            listOf(R.string.formula_editor_sensor_color_equals_color_parameter)

        private val SENSORS_ACCELERATION = listOf(
            R.string.formula_editor_sensor_x_acceleration,
            R.string.formula_editor_sensor_y_acceleration,
            R.string.formula_editor_sensor_z_acceleration
        )
        private val SENSORS_INCLINATION = listOf(
            R.string.formula_editor_sensor_x_inclination,
            R.string.formula_editor_sensor_y_inclination
        )
        private val SENSORS_COMPASS = listOf(R.string.formula_editor_sensor_compass_direction)
        private val SENSORS_GPS = listOf(
            R.string.formula_editor_sensor_latitude,
            R.string.formula_editor_sensor_longitude,
            R.string.formula_editor_sensor_location_accuracy,
            R.string.formula_editor_sensor_altitude
        )
        private val SENSOR_USER_LANGUAGE = listOf(R.string.formula_editor_sensor_user_language)
        private val SENSORS_TOUCH = listOf(
            R.string.formula_editor_function_finger_x,
            R.string.formula_editor_function_finger_y,
            R.string.formula_editor_function_is_finger_touching,
            R.string.formula_editor_function_multi_finger_x,
            R.string.formula_editor_function_multi_finger_y,
            R.string.formula_editor_function_is_multi_finger_touching,
            R.string.formula_editor_function_index_of_last_finger,
            R.string.formula_editor_function_number_of_current_touches,
            R.string.formula_editor_function_index_of_current_touch
        )
        private val SENSORS_TOUCH_PARAMS = listOf(
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_touch_parameter,
            R.string.formula_editor_function_touch_parameter,
            R.string.formula_editor_function_touch_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_touch_parameter
        )
        private val SENSORS_FACE_DETECTION = listOf(
            R.string.formula_editor_sensor_face_detected,
            R.string.formula_editor_sensor_face_size,
            R.string.formula_editor_sensor_face_x_position,
            R.string.formula_editor_sensor_face_y_position,
            R.string.formula_editor_sensor_second_face_detected,
            R.string.formula_editor_sensor_second_face_size,
            R.string.formula_editor_sensor_second_face_x_position,
            R.string.formula_editor_sensor_second_face_y_position
        )
        private val SENSORS_FACE_DETECTION_PARAMS = listOf(
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter
        )
        private val SENSORS_POSE_DETECTION = listOf(
            R.string.formula_editor_sensor_nose_x,
            R.string.formula_editor_sensor_nose_y,
            R.string.formula_editor_sensor_left_eye_inner_x,
            R.string.formula_editor_sensor_left_eye_inner_y,
            R.string.formula_editor_sensor_left_eye_center_x,
            R.string.formula_editor_sensor_left_eye_center_y,
            R.string.formula_editor_sensor_left_eye_outer_x,
            R.string.formula_editor_sensor_left_eye_outer_y,
            R.string.formula_editor_sensor_right_eye_inner_x,
            R.string.formula_editor_sensor_right_eye_inner_y,
            R.string.formula_editor_sensor_right_eye_center_x,
            R.string.formula_editor_sensor_right_eye_center_y,
            R.string.formula_editor_sensor_right_eye_outer_x,
            R.string.formula_editor_sensor_right_eye_outer_y,
            R.string.formula_editor_sensor_left_ear_x,
            R.string.formula_editor_sensor_left_ear_y,
            R.string.formula_editor_sensor_right_ear_x,
            R.string.formula_editor_sensor_right_ear_y,
            R.string.formula_editor_sensor_mouth_left_corner_x,
            R.string.formula_editor_sensor_mouth_left_corner_y,
            R.string.formula_editor_sensor_mouth_right_corner_x,
            R.string.formula_editor_sensor_mouth_right_corner_y,
            R.string.formula_editor_sensor_left_shoulder_x,
            R.string.formula_editor_sensor_left_shoulder_y,
            R.string.formula_editor_sensor_right_shoulder_x,
            R.string.formula_editor_sensor_right_shoulder_y,
            R.string.formula_editor_sensor_left_elbow_x,
            R.string.formula_editor_sensor_left_elbow_y,
            R.string.formula_editor_sensor_right_elbow_x,
            R.string.formula_editor_sensor_right_elbow_y,
            R.string.formula_editor_sensor_left_wrist_x,
            R.string.formula_editor_sensor_left_wrist_y,
            R.string.formula_editor_sensor_right_wrist_x,
            R.string.formula_editor_sensor_right_wrist_y,
            R.string.formula_editor_sensor_left_pinky_knuckle_x,
            R.string.formula_editor_sensor_left_pinky_knuckle_y,
            R.string.formula_editor_sensor_right_pinky_knuckle_x,
            R.string.formula_editor_sensor_right_pinky_knuckle_y,
            R.string.formula_editor_sensor_left_index_knuckle_x,
            R.string.formula_editor_sensor_left_index_knuckle_y,
            R.string.formula_editor_sensor_right_index_knuckle_x,
            R.string.formula_editor_sensor_right_index_knuckle_y,
            R.string.formula_editor_sensor_left_thumb_knuckle_x,
            R.string.formula_editor_sensor_left_thumb_knuckle_y,
            R.string.formula_editor_sensor_right_thumb_knuckle_x,
            R.string.formula_editor_sensor_right_thumb_knuckle_y,
            R.string.formula_editor_sensor_left_hip_x,
            R.string.formula_editor_sensor_left_hip_y,
            R.string.formula_editor_sensor_right_hip_x,
            R.string.formula_editor_sensor_right_hip_y,
            R.string.formula_editor_sensor_left_knee_x,
            R.string.formula_editor_sensor_left_knee_y,
            R.string.formula_editor_sensor_right_knee_x,
            R.string.formula_editor_sensor_right_knee_y,
            R.string.formula_editor_sensor_left_ankle_x,
            R.string.formula_editor_sensor_left_ankle_y,
            R.string.formula_editor_sensor_right_ankle_x,
            R.string.formula_editor_sensor_right_ankle_y,
            R.string.formula_editor_sensor_left_heel_x,
            R.string.formula_editor_sensor_left_heel_y,
            R.string.formula_editor_sensor_right_heel_x,
            R.string.formula_editor_sensor_right_heel_y,
            R.string.formula_editor_sensor_left_foot_index_x,
            R.string.formula_editor_sensor_left_foot_index_y,
            R.string.formula_editor_sensor_right_foot_index_x,
            R.string.formula_editor_sensor_right_foot_index_y
        )
        private val SENSORS_POSE_DETECTION_PARAMS = listOf(
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter
        )
        private val SENSORS_POSE_DETECTION_HUAWEI = listOf(
            R.string.formula_editor_sensor_head_top_x,
            R.string.formula_editor_sensor_head_top_y,
            R.string.formula_editor_sensor_neck_x,
            R.string.formula_editor_sensor_neck_y,
            R.string.formula_editor_sensor_left_shoulder_x,
            R.string.formula_editor_sensor_left_shoulder_y,
            R.string.formula_editor_sensor_right_shoulder_x,
            R.string.formula_editor_sensor_right_shoulder_y,
            R.string.formula_editor_sensor_left_elbow_x,
            R.string.formula_editor_sensor_left_elbow_y,
            R.string.formula_editor_sensor_right_elbow_x,
            R.string.formula_editor_sensor_right_elbow_y,
            R.string.formula_editor_sensor_left_wrist_x,
            R.string.formula_editor_sensor_left_wrist_y,
            R.string.formula_editor_sensor_right_wrist_x,
            R.string.formula_editor_sensor_right_wrist_y,
            R.string.formula_editor_sensor_left_hip_x,
            R.string.formula_editor_sensor_left_hip_y,
            R.string.formula_editor_sensor_right_hip_x,
            R.string.formula_editor_sensor_right_hip_y,
            R.string.formula_editor_sensor_left_knee_x,
            R.string.formula_editor_sensor_left_knee_y,
            R.string.formula_editor_sensor_right_knee_x,
            R.string.formula_editor_sensor_right_knee_y,
            R.string.formula_editor_sensor_left_ankle_x,
            R.string.formula_editor_sensor_left_ankle_y,
            R.string.formula_editor_sensor_right_ankle_x,
            R.string.formula_editor_sensor_right_ankle_y
        )
        private val SENSORS_POSE_DETECTION_PARAMS_HUAWEI = listOf(
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter
        )
        private val SENSORS_TEXT_RECOGNITION = listOf(
            R.string.formula_editor_sensor_text_from_camera,
            R.string.formula_editor_sensor_text_blocks_number,
            R.string.formula_editor_function_text_block_x,
            R.string.formula_editor_function_text_block_y,
            R.string.formula_editor_function_text_block_size,
            R.string.formula_editor_function_text_block_from_camera,
            R.string.formula_editor_function_text_block_language_from_camera
        )
        private val SENSORS_TEXT_RECOGNITION_PARAMS = listOf(
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_no_parameter,
            R.string.formula_editor_function_text_block_parameter,
            R.string.formula_editor_function_text_block_parameter,
            R.string.formula_editor_function_text_block_parameter,
            R.string.formula_editor_function_text_block_parameter,
            R.string.formula_editor_function_text_block_parameter
        )
        private val SENSORS_OBJECT_DETECTION = listOf(
            R.string.formula_editor_function_get_id_of_detected_object,
            R.string.formula_editor_function_object_with_id_visible
        )
        private val SENSORS_DATE_TIME = listOf(
            R.string.formula_editor_sensor_timer,
            R.string.formula_editor_sensor_date_year, R.string.formula_editor_sensor_date_month,
            R.string.formula_editor_sensor_date_day, R.string.formula_editor_sensor_date_weekday,
            R.string.formula_editor_sensor_time_hour, R.string.formula_editor_sensor_time_minute,
            R.string.formula_editor_sensor_time_second
        )
        private val SENSORS_NXT = listOf(
            R.string.formula_editor_sensor_lego_nxt_touch,
            R.string.formula_editor_sensor_lego_nxt_sound,
            R.string.formula_editor_sensor_lego_nxt_light,
            R.string.formula_editor_sensor_lego_nxt_light_active,
            R.string.formula_editor_sensor_lego_nxt_ultrasonic
        )
        private val SENSORS_EV3 = listOf(
            R.string.formula_editor_sensor_lego_ev3_sensor_touch,
            R.string.formula_editor_sensor_lego_ev3_sensor_infrared,
            R.string.formula_editor_sensor_lego_ev3_sensor_color,
            R.string.formula_editor_sensor_lego_ev3_sensor_color_ambient,
            R.string.formula_editor_sensor_lego_ev3_sensor_color_reflected,
            R.string.formula_editor_sensor_lego_ev3_sensor_hitechnic_color,
            R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_c,
            R.string.formula_editor_sensor_lego_ev3_sensor_nxt_temperature_f,
            R.string.formula_editor_sensor_lego_ev3_sensor_nxt_light,
            R.string.formula_editor_sensor_lego_ev3_sensor_nxt_light_active,
            R.string.formula_editor_sensor_lego_ev3_sensor_nxt_sound,
            R.string.formula_editor_sensor_lego_ev3_sensor_nxt_ultrasonic
        )
        private val SENSORS_PHIRO = listOf(
            R.string.formula_editor_phiro_sensor_front_left,
            R.string.formula_editor_phiro_sensor_front_right,
            R.string.formula_editor_phiro_sensor_side_left,
            R.string.formula_editor_phiro_sensor_side_right,
            R.string.formula_editor_phiro_sensor_bottom_left,
            R.string.formula_editor_phiro_sensor_bottom_right
        )
        private val SENSORS_ARDUINO = listOf(
            R.string.formula_editor_function_arduino_read_pin_value_analog,
            R.string.formula_editor_function_arduino_read_pin_value_digital
        )
        private val SENSORS_ARDUINO_PARAMS = listOf(
            R.string.formula_editor_function_pin_default_parameter,
            R.string.formula_editor_function_pin_default_parameter
        )
        private val SENSORS_DRONE = listOf(
            R.string.formula_editor_sensor_drone_battery_status,
            R.string.formula_editor_sensor_drone_emergency_state,
            R.string.formula_editor_sensor_drone_flying,
            R.string.formula_editor_sensor_drone_initialized,
            R.string.formula_editor_sensor_drone_usb_active,
            R.string.formula_editor_sensor_drone_usb_remaining_time,
            R.string.formula_editor_sensor_drone_camera_ready,
            R.string.formula_editor_sensor_drone_record_ready,
            R.string.formula_editor_sensor_drone_recording,
            R.string.formula_editor_sensor_drone_num_frames
        )
        private val SENSORS_RASPBERRY =
            listOf(R.string.formula_editor_function_raspi_read_pin_value_digital)
        private val SENSORS_RASPBERRY_PARAMS =
            listOf(R.string.formula_editor_function_pin_default_parameter)
        private val SENSORS_NFC = listOf(
            R.string.formula_editor_nfc_tag_id,
            R.string.formula_editor_nfc_tag_message
        )
        private val SENSORS_CAST_GAMEPAD = listOf(
            R.string.formula_editor_sensor_gamepad_a_pressed,
            R.string.formula_editor_sensor_gamepad_b_pressed,
            R.string.formula_editor_sensor_gamepad_up_pressed,
            R.string.formula_editor_sensor_gamepad_down_pressed,
            R.string.formula_editor_sensor_gamepad_left_pressed,
            R.string.formula_editor_sensor_gamepad_right_pressed
        )

        private val SENSORS_SPEECH_RECOGNITION =
            listOf(R.string.formula_editor_listening_language_sensor)
    }
}