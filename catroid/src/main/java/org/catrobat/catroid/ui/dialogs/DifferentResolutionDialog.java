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
package org.catrobat.catroid.ui.dialogs;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.dialogs.base.TextDialog;

public class DifferentResolutionDialog extends TextDialog {

	public static final String TAG = DifferentResolutionDialog.class.getSimpleName();
	private DifferentResolutionInterface differentResolutionInterface;

	public DifferentResolutionDialog(String text, DifferentResolutionInterface differentResolutionInterface) {
		super(R.string.warning, text, R.string.main_menu_continue, R.string.abort);
		this.differentResolutionInterface = differentResolutionInterface;
	}

	@Override
	protected boolean handlePositiveButtonClick() {
		differentResolutionInterface.showMergeDialog();
		return true;
	}

	@Override
	protected void handleNegativeButtonClick() {
	}

	public interface DifferentResolutionInterface {
		void showMergeDialog();
	}
}
