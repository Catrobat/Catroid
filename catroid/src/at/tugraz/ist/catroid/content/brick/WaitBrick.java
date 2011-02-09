package at.tugraz.ist.catroid.content.brick;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class WaitBrick implements Brick {
	private static final long serialVersionUID = 1L;
	private int timeToWaitInMilliseconds;

	public WaitBrick(int timeToWaitInMilliseconds) {
		this.timeToWaitInMilliseconds = timeToWaitInMilliseconds;
	}

	public void execute() {
		// TODO Auto-generated method stub
		
	}

	public Sprite getSprite() {
		return null;
	}

	public View getView(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.construction_brick_wait, null);
		EditText timeToWaitEditText = (EditText)view.findViewById(R.id.InputValueEditText);
		timeToWaitEditText.setText(timeToWaitInMilliseconds + "");
		return view;
	}

}
