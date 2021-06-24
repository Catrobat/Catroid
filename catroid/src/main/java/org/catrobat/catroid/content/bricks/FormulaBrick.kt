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
package org.catrobat.catroid.content.bricks

import android.content.Context
import android.graphics.PorterDuff
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.fragment.app.FragmentActivity
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.thoughtworks.xstream.annotations.XStreamAlias
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.bricks.Brick.FormulaField
import org.catrobat.catroid.content.bricks.Brick.ResourcesSet
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment.Companion.showFragment
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment
import org.catrobat.catroid.utils.Utils
import org.koin.java.KoinJavaComponent.inject

abstract class FormulaBrick : BrickBaseType(), View.OnClickListener {

    @JvmField
    @XStreamAlias("formulaList")
    var formulaMap = ConcurrentFormulaHashMap()

    @JvmField
	@Transient
    var brickFieldToTextViewIdMap: BiMap<FormulaField, Int> = HashBiMap.create(2)
    @Throws(IllegalArgumentException::class)
    fun getFormulaWithBrickField(formulaField: FormulaField): Formula? {
        return if (formulaMap.containsKey(formulaField)) {
            formulaMap[formulaField]
        } else {
            throw IllegalArgumentException(
                "Incompatible Brick Field: " + this.javaClass.simpleName
                    + " does not have BrickField." + formulaField.toString()
            )
        }
    }

    @Throws(IllegalArgumentException::class)
    fun setFormulaWithBrickField(formulaField: FormulaField, formula: Formula) {
        if (formulaMap.containsKey(formulaField)) {
            formulaMap.replace(formulaField, formula)
        } else {
            throw IllegalArgumentException(
                "Incompatible Brick Field: Cannot set BrickField."
                    + formulaField.toString() + " for " + this.javaClass.simpleName
            )
        }
    }

    protected fun addAllowedBrickField(formulaField: FormulaField, textViewResourceId: Int) {
        formulaMap.putIfAbsent(formulaField, Formula(0))
        brickFieldToTextViewIdMap[formulaField] = textViewResourceId
    }

    @CallSuper
    override fun addRequiredResources(requiredResourcesSet: ResourcesSet) {
        for (formula in formulaMap.values) {
            formula.addRequiredResources(requiredResourcesSet)
        }
    }

    fun replaceFormulaBrickField(oldFormulaField: FormulaField?, newFormulaField: FormulaField) {
        if (formulaMap.containsKey(oldFormulaField)) {
            val brickFormula = formulaMap[oldFormulaField]
            formulaMap.remove(oldFormulaField)
            formulaMap[newFormulaField] = brickFormula!!
        }
    }

    @Throws(CloneNotSupportedException::class)
    override fun clone(): Brick {
        val clone = super.clone() as FormulaBrick
        clone.formulaMap = formulaMap.clone()
        return clone
    }

    override fun getView(context: Context): View {
        super.getView(context)
        for ((key, value) in brickFieldToTextViewIdMap) {
            val formulaFieldView = view.findViewById<TextView>(value)
            formulaFieldView.text = getFormulaWithBrickField(key)?.clone()?.getTrimmedFormulaString(context)
        }
        return view
    }

    open fun setClickListeners() {
        for ((_, value) in brickFieldToTextViewIdMap) {
            val formulaFieldView = view.findViewById<TextView>(value)
            formulaFieldView.setOnClickListener(this)
        }
    }

    val formulas: List<Formula>
        get() = ArrayList(formulaMap.values)

    open fun getTextView(formulaField: FormulaField?): TextView {
        val brickFieldToTextView = brickFieldToTextViewIdMap[formulaField] ?: throw IllegalArgumentException(
            "Incompatible Brick Field: " + this.javaClass.simpleName
                + " does not have BrickField." + formulaField.toString()
        )
        return view.findViewById(brickFieldToTextView)

    }

    fun highlightTextView(formulaField: FormulaField?) {
        val formulaTextField = getTextView(formulaField)
        formulaTextField.background.mutate()
            .setColorFilter(
                view.context.resources
                    .getColor(R.color.brick_field_highlight), PorterDuff.Mode.SRC_ATOP
            )
    }

    override fun onClick(view: View) {
        saveCodeFile(view)
        showFormulaEditorToEditFormula(view)
    }

    open fun showFormulaEditorToEditFormula(view: View) {
        if (brickFieldToTextViewIdMap.inverse().containsKey(view.id)) {
            val formulaField = brickFieldToTextViewIdMap.inverse()[view.id] ?: return
            showFragment(view.context, this, formulaField)
        } else {
            showFragment(view.context, this, defaultBrickField)
        }
    }

    open val defaultBrickField: FormulaField
        get() = formulaMap.keys().nextElement()

    fun isBrickFieldANumber(formulaField: FormulaField): Boolean {
        val formula = getFormulaWithBrickField(formulaField) ?: return false
        return formula.isNumber
    }

    open fun getCustomView(context: Context?): View? {
        throw IllegalStateException("There is no custom view for the " + javaClass.simpleName + ".")
    }

    private fun getBrickFieldFromTextViewId(textViewId: Int): FormulaField? {
        return brickFieldToTextViewIdMap.inverse()[textViewId]
    }

    protected fun setSecondsLabel(view: View, formulaField: FormulaField) {
        val textView = view.findViewById<TextView>(R.id.brick_seconds_label)
        val context = textView.context
        if (isBrickFieldANumber(formulaField)) {
            try {
                val projectManager = inject(ProjectManager::class.java).value

                val scope = Scope(
                    projectManager.currentProject,
                    projectManager.currentSprite, null
                )
                val formulaValue = formulaMap[formulaField]!!
                    .interpretDouble(scope)
                textView.text = context.resources.getQuantityString(
                    R.plurals.second_plural,
                    Utils.convertDoubleToPluralInteger(formulaValue)
                )
                return
            } catch (e: InterpretationException) {
                Log.e(
                    javaClass.simpleName, "Interpretation of formula failed, "
                        + "fallback to quantity \"other\" for \"second(s)\" label.", e
                )
            }
        }
        textView.text = context.resources
            .getQuantityString(R.plurals.second_plural, Utils.TRANSLATION_PLURAL_OTHER_INTEGER)
    }

    fun updateUserDataReference(
        oldName: String?, newName: String?, item: UserData<*>?,
        renameAll: Boolean
    ) {
        for (formula in formulas) {
            when {
                renameAll -> {
                    formula.updateVariableName(oldName, newName)
                    formula.updateUserlistName(oldName, newName)
                }
                item is UserVariable -> {
                    formula.updateVariableName(oldName, newName)
                }
                else -> {
                    formula.updateUserlistName(oldName, newName)
                }
            }
        }
    }

    private fun saveCodeFile(view: View) {
        val scriptFragment = getScriptFragment(view) ?: return
        if (scriptFragment.copyProjectForUndoOption()) {
            (scriptFragment.activity as SpriteActivity).setUndoMenuItemVisibility(true)
            scriptFragment.setUndoBrickPosition(this)
        }
    }

    private fun getScriptFragment(view: View?): ScriptFragment? {
        val activity: FragmentActivity = UiUtils.getActivityFromView(view) ?: return null
        val currentFragment =
            activity.supportFragmentManager.findFragmentById(R.id.fragment_container)
        return if (currentFragment is ScriptFragment) {
            currentFragment
        } else null
    }

    open fun hasEditableFormulaField(): Boolean {
        return !brickFieldToTextViewIdMap.isEmpty()
    }
}