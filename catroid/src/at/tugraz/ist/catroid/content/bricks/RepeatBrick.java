/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.view.View;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.dialogs.EditIntegerDialog;

import com.thoughtworks.xstream.annotations.XStreamOmitField;

public class RepeatBrick extends LoopBeginBrick implements OnDismissListener {
	private static final long serialVersionUID = 1L;
	private int timesToRepeat;
	public static final transient int BRICK_BEHAVIOUR = Brick.NORMAL_BRICK | Brick.BACKGROUND_BRICK
			| Brick.IS_LOOP_BEGIN_BRICK;
	public static final transient int BRICK_RESSOURCES = Brick.NO_RESOURCES;

	@XStreamOmitField
	private transient View view;

	public RepeatBrick(Sprite sprite, int timesToRepeat) {
		super();
		this.sprite = sprite;
		this.timesToRepeat = timesToRepeat;
	}

	@Override
	public void execute() {
		loopEndBrick.setTimesToRepeat(timesToRepeat);
		super.setFirstStartTime();
	}

	@Override
	public Brick clone() {
		return new RepeatBrick(getSprite(), timesToRepeat);
	}

	public View getView(Context context, int brickId, BaseExpandableListAdapter adapter) {

		if (view == null) {
			view = View.inflate(context, R.layout.toolbox_brick_repeat, null);
		}

		EditText edit = (EditText) view.findViewById(R.id.toolbox_brick_repeat_edit_text);
		edit.setText(timesToRepeat + "");

		EditIntegerDialog dialog = new EditIntegerDialog(context, edit, timesToRepeat, false);
		dialog.setOnDismissListener(this);
		dialog.setOnCancelListener((OnCancelListener) context);

		edit.setOnClickListener(dialog);
		return view;
	}

	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.toolbox_brick_repeat, null);
	}

	public void onDismiss(DialogInterface dialog) {
		timesToRepeat = ((EditIntegerDialog) dialog).getValue();
		dialog.cancel();
	}
}