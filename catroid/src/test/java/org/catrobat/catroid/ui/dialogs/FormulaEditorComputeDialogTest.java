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
package org.catrobat.catroid.ui.dialogs;

import android.os.Build;
import android.os.Bundle;

import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLooper;

import androidx.appcompat.app.AppCompatActivity;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {Build.VERSION_CODES.P})
public class FormulaEditorComputeDialogTest {

	@Test
	public void onStopStopsSensorListeners() {
		BluetoothDeviceService bluetoothDeviceService = Mockito.mock(BluetoothDeviceService.class);
		AppCompatActivity activity = Robolectric.buildActivity(AppCompatActivity.class).setup().get();
		TestFormulaEditorComputeDialog dialog = new TestFormulaEditorComputeDialog(activity, Mockito.mock(Scope.class));

		try (MockedStatic<SensorHandler> sensorHandler = Mockito.mockStatic(SensorHandler.class);
				MockedStatic<ServiceProvider> serviceProvider = Mockito.mockStatic(ServiceProvider.class)) {
			serviceProvider.when(() -> ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE))
					.thenReturn(bluetoothDeviceService);

			dialog.show();
			dialog.dismiss();
			ShadowLooper.shadowMainLooper().idle();

			sensorHandler.verify(() -> SensorHandler.unregisterListener(dialog), times(1));
			sensorHandler.verify(SensorHandler::stopSensorListeners, times(1));
			verify(bluetoothDeviceService, times(1)).pause();
		}
	}

	private static class TestFormulaEditorComputeDialog extends FormulaEditorComputeDialog {
		TestFormulaEditorComputeDialog(AppCompatActivity activity, Scope scope) {
			super(activity, scope);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// No UI setup needed for lifecycle verification.
		}
	}
}
