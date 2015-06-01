/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
//import android.support.v7.app.ActionBarActivity --> this is deprecated
import android.support.v7.app.AppCompatActivity;

/**
 * Created by illya on 10/11/14.
 */
public class BaseListFragment extends ListFragment {

	protected AppCompatActivity getSupportActivity() {
		return (AppCompatActivity) getActivity();
	}

	protected ActionBar getSupportActionBar() {
		return getSupportActivity().getSupportActionBar();
	}
}
