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
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ScriptSequenceAction;

import java.util.List;

public class AdMobShowBannerBrick extends BrickBaseType implements AdapterView.OnItemSelectedListener {

	private AdMobBannerPositionEnum positionEnum;
	private AdMobBannerSizeEnum sizeEnum;
	private String posName;
	private String sizeName;

	public AdMobShowBannerBrick(AdMobBannerPositionEnum position, AdMobBannerSizeEnum bannerSizeEnum) {
		this.positionEnum = position;
		this.posName = positionEnum.name();
		this.sizeEnum = bannerSizeEnum;
		this.sizeName = sizeEnum.name();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
			case R.id.brick_admob_show_banner_position:
				positionEnum = AdMobBannerPositionEnum.values()[position];
				posName = positionEnum.name();
				break;
			case R.id.brick_admob_show_banner_size:
				sizeEnum = AdMobBannerSizeEnum.values()[position];
				sizeName = sizeEnum.name();
				break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	private Object readResolvePosition() {
		if (posName != null) {
			positionEnum = AdMobBannerPositionEnum.valueOf(posName);
		}
		return this;
	}

	private Object readResolveSize() {
		if (sizeName != null) {
			sizeEnum = AdMobBannerSizeEnum.valueOf(sizeName);
		}
		return this;
	}

	@Override
	public View getView(Context context) {
		super.getView(context);

		ArrayAdapter<CharSequence> bannerPositionAdapter = ArrayAdapter.createFromResource(context, R.array.brick_admob_banner_position,
				android.R.layout.simple_spinner_item);
		bannerPositionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner bannerPosition = (Spinner) view.findViewById(R.id.brick_admob_show_banner_position);
		bannerPosition.setOnItemSelectedListener(this);
		bannerPosition.setAdapter(bannerPositionAdapter);

		if (positionEnum == null) {
			readResolvePosition();
		}
		bannerPosition.setSelection(positionEnum.ordinal(), true);

		ArrayAdapter<CharSequence> bannerSizeAdapter = ArrayAdapter.createFromResource(context, R.array.brick_admob_banner_size,
				android.R.layout.simple_spinner_item);
		bannerSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner bannerSize = (Spinner) view.findViewById(R.id.brick_admob_show_banner_size);
		bannerSize.setOnItemSelectedListener(this);
		bannerSize.setAdapter(bannerSizeAdapter);

		if (sizeEnum == null) {
			readResolveSize();
		}
		bannerSize.setSelection(sizeEnum.ordinal(), true);
		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		View prototypeView = super.getPrototypeView(context);
		Spinner bannerPosition = (Spinner) prototypeView.findViewById(R.id.brick_admob_show_banner_position);

		ArrayAdapter<CharSequence> bannerPositionAdapter = ArrayAdapter.createFromResource(context, R.array.brick_admob_banner_position,
				android.R.layout.simple_spinner_item);
		bannerPositionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		bannerPosition.setAdapter(bannerPositionAdapter);
		bannerPosition.setSelection(positionEnum.ordinal());

		Spinner bannerSize = (Spinner) prototypeView.findViewById(R.id.brick_admob_show_banner_size);
		ArrayAdapter<CharSequence> bannerSizeAdapter = ArrayAdapter.createFromResource(context, R.array.brick_admob_banner_size,
				android.R.layout.simple_spinner_item);
		bannerSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		bannerSize.setAdapter(bannerSizeAdapter);
		bannerSize.setSelection(sizeEnum.ordinal());
		return prototypeView;
	}

	@Override
	public int getViewResource() {
		return R.layout.brick_admob_show_banner;
	}

	@Override
	public List<ScriptSequenceAction> addActionToSequence(Sprite sprite, ScriptSequenceAction sequence) {
		sequence.addAction(sprite.getActionFactory().createAdMobShowBannerAction(positionEnum, sizeEnum));
		return null;
	}

	public enum AdMobBannerPositionEnum {
		TOP, BOTTOM
	}

	public enum AdMobBannerSizeEnum {
		BANNER, SMART_BANNER, LARGE_BANNER
	}
}
