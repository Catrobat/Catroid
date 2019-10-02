/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.io.asynctask;

import android.content.Context;
import android.os.AsyncTask;

import org.catrobat.catroid.content.LegoEV3Setting;
import org.catrobat.catroid.content.LegoNXTSetting;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Setting;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.devices.mindstorms.ev3.sensors.EV3Sensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.io.XstreamSerializer;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;

import java.lang.ref.WeakReference;

public class ProjectSaveTask extends AsyncTask<Void, Void, Boolean> {

	private Project project;

	private WeakReference<Context> weakContextReference;
	private WeakReference<ProjectSaveListener> weakListenerReference;

	public ProjectSaveTask(Project project, Context context) {
		this.project = project;
		this.weakContextReference = new WeakReference<>(context);
	}

	public ProjectSaveTask setListener(ProjectSaveListener listener) {
		weakListenerReference = new WeakReference<>(listener);
		return this;
	}

	public static boolean task(Project project, Context context) {
		saveLegoSettings(project, context);
		return XstreamSerializer.getInstance().saveProject(project);
	}

	private static void saveLegoSettings(Project project, Context context) {
		if (project == null) {
			return;
		}
		saveLegoNXTSettingsToProject(project, context);
		saveLegoEV3SettingsToProject(project, context);
	}

	private static void saveLegoNXTSettingsToProject(Project project, Context context) {
		Brick.ResourcesSet resourcesSet = project.getRequiredResources();
		if (!resourcesSet.contains(Brick.BLUETOOTH_LEGO_NXT)) {
			for (Object setting : project.getSettings().toArray()) {
				if (setting instanceof LegoNXTSetting) {
					project.getSettings().remove(setting);
					return;
				}
			}
			return;
		}

		NXTSensor.Sensor[] sensorMapping = SettingsFragment.getLegoNXTSensorMapping(context);
		for (Setting setting : project.getSettings()) {
			if (setting instanceof LegoNXTSetting) {
				((LegoNXTSetting) setting).updateMapping(sensorMapping);
				return;
			}
		}

		Setting mapping = new LegoNXTSetting(sensorMapping);
		project.getSettings().add(mapping);
	}

	private static void saveLegoEV3SettingsToProject(Project project, Context context) {
		Brick.ResourcesSet resourcesSet = project.getRequiredResources();
		if (!resourcesSet.contains(Brick.BLUETOOTH_LEGO_EV3)) {
			for (Object setting : project.getSettings().toArray()) {
				if (setting instanceof LegoEV3Setting) {
					project.getSettings().remove(setting);
					return;
				}
			}
			return;
		}

		EV3Sensor.Sensor[] sensorMapping = SettingsFragment.getLegoEV3SensorMapping(context);
		for (Setting setting : project.getSettings()) {
			if (setting instanceof LegoEV3Setting) {
				((LegoEV3Setting) setting).updateMapping(sensorMapping);
				return;
			}
		}

		Setting mapping = new LegoEV3Setting(sensorMapping);
		project.getSettings().add(mapping);
	}

	@Override
	protected Boolean doInBackground(Void... voids) {
		Context context = weakContextReference.get();
		if (context != null) {
			return task(project, context);
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean success) {
		if (weakListenerReference == null) {
			return;
		}
		ProjectSaveListener listener = weakListenerReference.get();
		if (listener != null) {
			listener.onSaveProjectComplete(success);
		}
	}

	public interface ProjectSaveListener {

		void onSaveProjectComplete(boolean success);
	}
}
