/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.catrobat.catroid.ui.dynamiclistview;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

import java.util.List;

public class DynamicListView extends ListView {

	UtilDynamicListView utilDynamicListView = new UtilDynamicListView(this);

	public DynamicListView(Context context) {
		super(context);
		utilDynamicListView.init(context);
	}

	public DynamicListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		utilDynamicListView.init(context);
	}

	public DynamicListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		utilDynamicListView.init(context);
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		utilDynamicListView.dispatchDraw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!utilDynamicListView.onTouchEvent(event)) {
			return false;
		}
		return super.onTouchEvent(event);
	}

	int getComputeVerticalScrollOffset() {
		return computeVerticalScrollOffset();
	}

	int getComputeVerticalScrollExtent() {
		return computeVerticalScrollExtent();
	}

	int getComputeVerticalScrollRange() {
		return computeVerticalScrollRange();
	}

	public void setDataList(List dataList) {
		utilDynamicListView.setDataList(dataList);
	}

	public void notifyListItemTouchActionUp() {
		utilDynamicListView.notifyListItemTouchActionUp();
	}
}
