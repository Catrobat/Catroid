/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Script;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.actions.ExtendedActions;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.ui.fragment.FormulaEditorFragment;

import java.util.List;

public class LedBrick extends BrickBaseType implements OnClickListener, FormulaBrick {
	private static final long serialVersionUID = 1L;

	private Formula lightValue;

	private transient View prototypeView;

	public LedBrick( Sprite sprite, Formula lightValue ) {
		this.sprite = sprite;
		this.lightValue = lightValue;
	}

	public LedBrick( Sprite sprite ) {
		this.sprite = sprite;
		this.lightValue = new Formula( 0 );
	}

	@Override
	public Brick copyBrickForSprite( Sprite sprite, Script script ) {
		LedBrick copyBrick = (LedBrick) clone();
		copyBrick.sprite = sprite;
		return copyBrick;
	}

	@Override
	public View getView( Context context, int brickId, BaseAdapter baseAdapter ) {
		if ( animationState ) {
			return view;
		}

		if ( view == null ) {
			alphaValue = 0xFF;
		}

		view = View.inflate( context, R.layout.brick_led, null );
		view = getViewWithAlpha( alphaValue );

		setCheckboxView( R.id.brick_led_checkbox );

		final Brick brickInstance = this;
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checked = isChecked;
				adapter.handleCheck(brickInstance, isChecked);
			}
		});

		TextView textLed = (TextView) view.findViewById( R.id.brick_led_prototype_text_view );
		TextView editLed = (TextView) view.findViewById( R.id.brick_led_edit_text );

		lightValue.setTextFieldId( R.id.brick_led_edit_text );
		lightValue.refreshTextField( view );

		textLed.setVisibility( View.GONE );
		editLed.setVisibility( View.VISIBLE );
		editLed.setOnClickListener( this );

		return view;
	}

	@Override
	public View getViewWithAlpha( int alphaValue ) {
		if (view != null) {

			View layout = view.findViewById( R.id.brick_led_layout );
			Drawable background = layout.getBackground();
			background.setAlpha( alphaValue );

			TextView textLed = (TextView) view.findViewById( R.id.brick_led_prototype_text_view );
			TextView editLed = (TextView) view.findViewById( R.id.brick_led_edit_text );
			textLed.setTextColor( textLed.getTextColors().withAlpha( alphaValue ));
			editLed.setTextColor( textLed.getTextColors().withAlpha( alphaValue ));
			editLed.getBackground().setAlpha( alphaValue );

			this.alphaValue = (alphaValue);
		}

		return view;
	}

	@Override
	public void onClick(View v) {
		if ( checkbox.getVisibility() == View.VISIBLE ) {
			return;
		}
		FormulaEditorFragment.showFragment(view, this, lightValue);
	}

	@Override
	public Formula getFormula() {
        return lightValue;
    }

	@Override
	public List<SequenceAction> addActionToSequence( SequenceAction sequence ) {
		sequence.addAction( ExtendedActions.lights( this.sprite, lightValue ) );
		return null;
	}

	@Override
	public View getPrototypeView( Context context ) {
		prototypeView = View.inflate( context, R.layout.brick_led, null );
		TextView ledTextView = (TextView) prototypeView.findViewById( R.id.brick_led_prototype_text_view );
		ledTextView.setText( String.valueOf( lightValue.interpretBoolean( sprite )));
		return prototypeView;
	}

	@Override
	public Brick clone() {
		return new LedBrick( getSprite(), lightValue.clone() );
	}

	@Override
	public int getRequiredResources() {
        return CAMERA_LED;
    }
}
