/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.content.bricks;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.ui.ScriptTabActivity;
import at.tugraz.ist.catroid.ui.dialogs.BrickTextDialog;

public class GlideToBrick implements Brick, OnClickListener {
	private static final long serialVersionUID = 1L;
	private int xDestination;
	private int yDestination;
	private int durationInMilliSeconds;
	private Sprite sprite;

	private transient View view;

	public GlideToBrick() {

	}

	public GlideToBrick(Sprite sprite, int xDestination, int yDestination, int durationInMilliSeconds) {
		this.sprite = sprite;
		this.xDestination = xDestination;
		this.yDestination = yDestination;
		this.durationInMilliSeconds = durationInMilliSeconds;
	}

	@Override
	public int getRequiredResources() {
		return NO_RESOURCES;
	}

	@Override
	public void execute() {
		/* That's the way how an action is made */
		//		Action action = MoveBy.$(xDestination, yDestination, this.durationInMilliSeconds / 1000);
		//		final CountDownLatch latch = new CountDownLatch(1);
		//		action = action.setCompletionListener(new OnActionCompleted() {
		//			public void completed(Action action) {
		//				latch.countDown();
		//			}
		//		});
		//		sprite.costume.action(action);
		//		try {
		//			latch.await();
		//		} catch (InterruptedException e) {
		//		}
		long startTime = System.currentTimeMillis();
		int duration = durationInMilliSeconds;
		while (duration > 0) {
			if (!sprite.isAlive(Thread.currentThread())) {
				break;
			}
			long timeBeforeSleep = System.currentTimeMillis();
			int sleep = 33;
			while (System.currentTimeMillis() <= (timeBeforeSleep + sleep)) {

				if (sprite.isPaused) {
					sleep = (int) ((timeBeforeSleep + sleep) - System.currentTimeMillis());
					long milliSecondsBeforePause = System.currentTimeMillis();
					while (sprite.isPaused) {
						if (sprite.isFinished) {
							return;
						}
						Thread.yield();
					}
					timeBeforeSleep = System.currentTimeMillis();
					startTime += System.currentTimeMillis() - milliSecondsBeforePause;
				}

				Thread.yield();
			}
			long currentTime = System.currentTimeMillis();
			duration -= (int) (currentTime - startTime);
			updatePositions((int) (currentTime - startTime), duration);
			startTime = currentTime;
		}

		if (!sprite.isAlive(Thread.currentThread())) {
			// -stay at last position
		} else {
			sprite.costume.aquireXYWidthHeightLock();
			sprite.costume.setXYPosition(xDestination, yDestination);
			sprite.costume.releaseXYWidthHeightLock();
		}
	}

	private void updatePositions(int timePassed, int duration) {
		sprite.costume.aquireXYWidthHeightLock();
		float xPosition = sprite.costume.getXPosition();
		float yPosition = sprite.costume.getYPosition();

		xPosition += ((float) timePassed / duration) * (xDestination - xPosition);
		yPosition += ((float) timePassed / duration) * (yDestination - yPosition);

		sprite.costume.setXYPosition(xPosition, yPosition);
		sprite.costume.releaseXYWidthHeightLock();
	}

	@Override
	public Sprite getSprite() {
		return this.sprite;
	}

	public int getDurationInMilliSeconds() {
		return durationInMilliSeconds;
	}

	@Override
	public View getView(Context context, int brickId, BaseAdapter adapter) {

		view = View.inflate(context, R.layout.brick_glide_to, null);

		TextView textX = (TextView) view.findViewById(R.id.brick_glide_to_x_text_view);
		EditText editX = (EditText) view.findViewById(R.id.brick_glide_to_x_edit_text);
		editX.setText(String.valueOf(xDestination));
		editX.setOnClickListener(this);

		TextView textY = (TextView) view.findViewById(R.id.brick_glide_to_y_text_view);
		EditText editY = (EditText) view.findViewById(R.id.brick_glide_to_y_edit_text);
		editY.setText(String.valueOf(yDestination));
		editY.setOnClickListener(this);

		TextView textDuration = (TextView) view.findViewById(R.id.brick_glide_to_duration_text_view);
		EditText editDuration = (EditText) view.findViewById(R.id.brick_glide_to_duration_edit_text);
		editDuration.setText(String.valueOf(durationInMilliSeconds / 1000.0));

		textX.setVisibility(View.GONE);
		editX.setVisibility(View.VISIBLE);
		textY.setVisibility(View.GONE);
		editY.setVisibility(View.VISIBLE);
		textDuration.setVisibility(View.GONE);
		editDuration.setVisibility(View.VISIBLE);

		editDuration.setOnClickListener(this);

		return view;
	}

	@Override
	public View getPrototypeView(Context context) {
		return View.inflate(context, R.layout.brick_glide_to, null);
	}

	@Override
	public Brick clone() {
		return new GlideToBrick(getSprite(), xDestination, yDestination, getDurationInMilliSeconds());
	}

	@Override
	public void onClick(final View view) {
		ScriptTabActivity activity = (ScriptTabActivity) view.getContext();

		BrickTextDialog editDialog = new BrickTextDialog() {
			@Override
			protected void initialize() {
				if (view.getId() == R.id.brick_glide_to_x_edit_text) {
					input.setText(String.valueOf(xDestination));
					input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
				} else if (view.getId() == R.id.brick_glide_to_y_edit_text) {
					input.setText(String.valueOf(yDestination));
					input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
				} else if (view.getId() == R.id.brick_glide_to_duration_edit_text) {
					input.setText(String.valueOf(durationInMilliSeconds / 1000.0));
					input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
							| InputType.TYPE_NUMBER_FLAG_SIGNED);
				}

				input.setSelectAllOnFocus(true);
			}

			@Override
			protected boolean handleOkButton() {
				try {
					if (view.getId() == R.id.brick_glide_to_x_edit_text) {
						xDestination = Integer.parseInt(input.getText().toString());
					} else if (view.getId() == R.id.brick_glide_to_y_edit_text) {
						yDestination = Integer.parseInt(input.getText().toString());
					} else if (view.getId() == R.id.brick_glide_to_duration_edit_text) {
						durationInMilliSeconds = (int) Math
								.round(Double.parseDouble(input.getText().toString()) * 1000);
					}
				} catch (NumberFormatException exception) {
					Toast.makeText(getActivity(), R.string.error_no_number_entered, Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		};
		
		editDialog.show(activity.getSupportFragmentManager(), "dialog_glide_to_brick");
	}
}
