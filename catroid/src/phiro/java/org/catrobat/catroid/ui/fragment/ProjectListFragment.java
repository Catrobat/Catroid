/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.ui.fragment;

import android.os.Bundle;

import org.catrobat.catroid.phiro.io.OnCopyPhiroProjectsCompleteListener;
import org.catrobat.catroid.phiro.transfers.CopyPhiroProjectsTask;

public class ProjectListFragment extends BaseProjectListFragment implements OnCopyPhiroProjectsCompleteListener {

	public static final String[] PHIRO_PROJECTS = {
			"BETT dice phiro program",
			"BETT DIRECTION DETECT",
			"BETT DRIVE APP",
			"BETT FACE DETECTION",
			"phiro draw sequential mode",
			"loudness control"
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		loadPhiroProjects();
		super.onActivityCreated(savedInstanceState);
	}

	private void loadPhiroProjects() {
		CopyPhiroProjectsTask copyPhiroProjectsTask = new CopyPhiroProjectsTask(getActivity(), this);
		copyPhiroProjectsTask.execute(PHIRO_PROJECTS);
	}

	@Override
	public void copyingCompleted() {
		initializeList();
	}
}
