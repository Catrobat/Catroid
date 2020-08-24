/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

import org.catrobat.catroid.R;
import org.catrobat.catroid.io.ProjectLoadAndUpdate;
import org.jetbrains.annotations.NotNull;

public class ProjectLoadStringProvider implements ProjectLoadAndUpdate.UpdateStringProvider {
	private final String backgroundString;
	private final String loadErrorMessage;
	private final String outdatedErrorMessage;
	private final String compatibilityErrorMessage;
	private final String defaultSceneName;

	public ProjectLoadStringProvider(Context context) {
		this.backgroundString = context.getString(R.string.background);
		this.loadErrorMessage = context.getString(R.string.error_load_project);
		this.outdatedErrorMessage = context.getString(R.string.error_outdated_version);
		this.compatibilityErrorMessage =
				context.getString(R.string.error_project_compatibility);
		this.defaultSceneName = context.getString(R.string.default_scene_name, 1);
	}

	@NotNull
	@Override
	public String getBackgroundString() {
		return backgroundString;
	}

	@NotNull
	@Override
	public String getLoadErrorMessage() {
		return loadErrorMessage;
	}

	@NotNull
	@Override
	public String getOutdatedErrorMessage() {
		return outdatedErrorMessage;
	}

	@NotNull
	@Override
	public String getCompatibilityErrorMessage() {
		return compatibilityErrorMessage;
	}

	@NotNull
	@Override
	public String getDefaultSceneName() {
		return defaultSceneName;
	}
}
