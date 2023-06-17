/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.achievements;

import android.content.Intent;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.ui.BaseActivity;

import java.util.Objects;

public class AchievementActivity extends BaseActivity {



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.achievement);

		ImageView imageView = findViewById(R.id.BigAchievementImage);
		TextView titleView =  findViewById(R.id.BigAchievementTitle);
		TextView descriptionView = findViewById(R.id.BigAchievementDescription);

		Intent intent = this.getIntent();
		if(intent != null)
		{
			String title = intent.getStringExtra("Title");
			String description = intent.getStringExtra("Description");
			int imageId = intent.getIntExtra("Image", R.drawable.ic_main_menu_achievements_button);
			boolean unlocked = intent.getBooleanExtra("Unlocked", false);
			imageView.setImageResource(imageId);
			titleView.setText(title);
			descriptionView.setText(description);
			ColorMatrix colorMatrix = new ColorMatrix();
			colorMatrix.setSaturation(0);
			imageView.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
			if(unlocked)
			{
				imageView.clearColorFilter();
			}
		}


		setSupportActionBar(findViewById(R.id.toolbar));
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle(R.string.achievement_title);
	}


}

