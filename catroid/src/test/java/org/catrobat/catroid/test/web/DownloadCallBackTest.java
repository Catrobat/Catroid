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

package org.catrobat.catroid.test.web;

import org.catrobat.catroid.ui.recyclerview.fragment.MainMenuFragment;
import org.catrobat.catroid.utils.ProjectDownloadUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ProjectDownloadUtil.class})
public class DownloadCallBackTest {

	@Test
	public void testLandingPageUpdate() {
		ProjectDownloadUtil callback = PowerMockito.mock(ProjectDownloadUtil.class);
		MainMenuFragment fragment = PowerMockito.mock(MainMenuFragment.class);
		Mockito.doCallRealMethod().when(callback).setFragment(Mockito.any(MainMenuFragment.class));
		callback.setFragment(fragment);
		Mockito.doCallRealMethod().when(callback).onDownloadFinished(Mockito.anyString(),
				Mockito.anyString());
		callback.onDownloadFinished("name", "url");
		Mockito.verify(fragment, Mockito.times(1)).refreshData();
	}
}
