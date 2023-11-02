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
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.dialogs.LegoSensorPortConfigDialog
import org.catrobat.catroid.ui.dialogs.regexassistant.RegularExpressionAssistantDialog
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.CategoryListRVAdapter.CategoryListItem
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.utils.AddUserListDialog
import org.koin.java.KoinJavaComponent.inject
import java.util.Locale

class CategoryListFragment : Fragment(), CategoryListRVAdapter.OnItemClickListener {

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private val categoryListItems: CategoryListItems = CategoryListItems()
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
            CategoryListRVAdapter.DEFAULT ->
                if (categoryListItems.getListFunctions().contains(item.nameResId)) {
                onUserListFunctionSelected(item)
            } else if (R.string.formula_editor_function_regex_assistant == item.nameResId) {
                regularExpressionAssistantActivityOnButtonClick()
            } else {
                val formulaEditorFragment =
                    parentFragmentManager.findFragmentByTag(FormulaEditorFragment
                                                    .FORMULA_EDITOR_FRAGMENT_TAG) as
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

    private fun onOptionsMenuClick(tag: String?) {
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

    private fun getSharedPreferences(context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    private fun getLanguage(activity: Activity): String {
        var language = "?language="
        val sharedPreferences: SharedPreferences =
            getSharedPreferences(activity.applicationContext)
        val languageTag = sharedPreferences.getString(SharedPreferenceKeys.LANGUAGE_TAG_KEY, "")
        val mLocale: Locale = if (languageTag == SharedPreferenceKeys.DEVICE_LANGUAGE) {
            Locale.forLanguageTag(CatroidApplication.defaultSystemLanguage)
        } else {
            if (SharedPreferenceKeys.LANGUAGE_TAGS.contains(languageTag)) {
                languageTag?.let { Locale.forLanguageTag(it) }
            } else Locale.forLanguageTag(CatroidApplication.defaultSystemLanguage)
        }!!
        language += mLocale.language
        return language
    }

    private fun addResourceToActiveFormulaInFormulaEditor(
        categoryListItem: CategoryListItem?,
        lastUserList: UserList? = null
    ) {
        val formulaEditorFragment: FormulaEditorFragment? = parentFragmentManager
            .findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG) as FormulaEditorFragment?
        formulaEditorFragment?.addResourceToActiveFormula(categoryListItem!!.nameResId)

        if (lastUserList != null) {
            formulaEditorFragment!!.addUserListToActiveFormula(
                lastUserList.name
            )
            requireActivity().onBackPressed()
        }
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
        val itemList = categoryListItems.getFunctionItems(requireActivity())
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
        return parentFragmentManager
            .findFragmentByTag(FormulaEditorFragment.FORMULA_EDITOR_FRAGMENT_TAG)
            as FormulaEditorFragment?
    }

    private fun openRegularExpressionAssistant() {
        RegularExpressionAssistantDialog(context, parentFragmentManager).createAssistant()
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
            activity?.getString(R.string.data_label),
            activity?.getString(R.string.ok),
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
                LegoSensorPortConfigDialog.OnClickListener { _: DialogInterface?, selectedPort:
                Int, selectedSensor: Enum<*>? ->
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
                    val formulaEditor = parentFragmentManager
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
                    (parentFragmentManager
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
            categoryListItems.getObjectItems(requireActivity())
        } else if (FUNCTION_TAG == argument) {
            categoryListItems.getFunctionItems(requireActivity())
        } else if (LOGIC_TAG == argument) {
            categoryListItems.getLogicItems(requireActivity())
        } else if (SENSOR_TAG == argument) {
            categoryListItems.getSensorItems(requireActivity())
        } else {
            throw IllegalArgumentException("Argument for CategoryListFragment null or unknown:" +
                " $argument")
        }
        val adapter = CategoryListRVAdapter(items)
        adapter.setOnItemClickListener(this)
        recyclerView!!.adapter = adapter
    }

    companion object {
        const val OBJECT_TAG = "objectFragment"
        const val FUNCTION_TAG = "functionFragment"
        const val LOGIC_TAG = "logicFragment"
        const val SENSOR_TAG = "sensorFragment"
        const val ACTION_BAR_TITLE_BUNDLE_ARGUMENT = "actionBarTitle"
        const val FRAGMENT_TAG_BUNDLE_ARGUMENT = "fragmentTag"
        val TAG: String = CategoryListFragment::class.java.simpleName
    }
}
