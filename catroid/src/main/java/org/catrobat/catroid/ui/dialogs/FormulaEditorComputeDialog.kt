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
package org.catrobat.catroid.ui.dialogs

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.bluetooth.base.BluetoothDevice
import org.catrobat.catroid.camera.CameraManager
import org.catrobat.catroid.camera.VisualDetectionHandler
import org.catrobat.catroid.common.CatroidService
import org.catrobat.catroid.common.ServiceProvider
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType
import org.catrobat.catroid.formulaeditor.SensorCustomEvent
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.SensorLoudness
import org.catrobat.catroid.utils.NumberFormats
import org.catrobat.catroid.utils.ShowTextUtils.AndroidStringProvider

open class FormulaEditorComputeDialog(
    private val context: Context,
    private val scope: Scope
) : AlertDialog(context), SensorEventListener, SensorCustomEventListener {
    private var formulaToCompute: Formula? = null
    private var computeTextView: TextView? = null
    private var camerManager: CameraManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val projectManager = ProjectManager.getInstance()
        if (projectManager.isCurrentProjectLandscapeMode) {
            setContentView(R.layout.dialog_formulaeditor_compute_landscape)
            computeTextView = findViewById(R.id.formula_editor_compute_dialog_textview_landscape_mode)
        } else {
            setContentView(R.layout.dialog_formulaeditor_compute)
            computeTextView = findViewById(R.id.formula_editor_compute_dialog_textview)
        }
        showFormulaResult(scope, AndroidStringProvider(context))
    }

    fun setFormula(formula: Formula) {
        formulaToCompute = formula

        val resourcesSet = Brick.ResourcesSet()
        formula.addRequiredResources(resourcesSet)

        if (resourcesSet.contains(Brick.MICROPHONE)) {
            SensorHandler.getInstance(context).setSensorLoudness(SensorLoudness())
        }

        if (resourcesSet.contains(Brick.BLUETOOTH_LEGO_NXT)) {
            val btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE)
            btService.connectDevice(BluetoothDevice.LEGO_NXT, context)
        }

        if (resourcesSet.contains(Brick.BLUETOOTH_LEGO_EV3)) {
            val btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE)
            btService.connectDevice(BluetoothDevice.LEGO_EV3, context)
        }

        if (resourcesSet.contains(Brick.BLUETOOTH_SENSORS_ARDUINO)) {
            val btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE)
            btService.connectDevice(BluetoothDevice.ARDUINO, context)
        }

        if (resourcesSet.contains(Brick.BLUETOOTH_PHIRO)) {
            val btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE)
            btService.connectDevice(BluetoothDevice.PHIRO, context)
        }

        if (formula.containsElement(ElementType.SENSOR)) {
            SensorHandler.startSensorListener(context)
            SensorHandler.registerListener(this)
        }

        if (requiresVisualDetection(resourcesSet)) {
            camerManager = CameraManager(context as Activity)
            camerManager?.startDetection()
            VisualDetectionHandler.addListener(this)
        }
    }

    override fun onStop() {
        VisualDetectionHandler.removeListener(this)
        SensorHandler.unregisterListener(this)
        SensorHandler.stopSensorListeners()
        ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).pause()
        super.onStop()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dismiss()
        return true
    }

    private fun requiresVisualDetection(resourcesSet: Brick.ResourcesSet): Boolean {
        return resourcesSet.contains(Brick.FACE_DETECTION) ||
            resourcesSet.contains(Brick.OBJECT_DETECTION) ||
            resourcesSet.contains(Brick.POSE_DETECTION) ||
            resourcesSet.contains(Brick.TEXT_DETECTION)
    }

    private fun showFormulaResult(scope: Scope, stringProvider: Formula.StringProvider) {
        val textView = computeTextView ?: return
        val formula = formulaToCompute ?: return
        val result = formula.getUserFriendlyString(stringProvider, scope)
        setDialogTextView(textView, NumberFormats.trimTrailingCharacters(result))
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onSensorChanged(event: SensorEvent?) {
        showFormulaResult(scope, AndroidStringProvider(context))
    }

    private fun setDialogTextView(textView: TextView, newString: String) {
        textView.post {
            textView.text = newString

            val params = textView.layoutParams
            val height = textView.lineCount * textView.lineHeight
            val heightMargin = (height * TEXT_HEIGHT_MARGIN_FACTOR).toInt()
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = height + heightMargin
            textView.layoutParams = params
        }
    }

    override fun onCustomSensorChanged(event: SensorCustomEvent?) {
        Log.d("FormulaCompute", "Custom sensor: ${event?.sensor} = ${event?.value}")
        showFormulaResult(scope, AndroidStringProvider(context))
    }

    companion object {
        private const val TEXT_HEIGHT_MARGIN_FACTOR = 0.5
    }
}
