package at.tugraz.ist.catroid.content.brick;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.sprite.Sprite;

public class WaitBrick implements Brick {
	private static final long serialVersionUID = 1L;

	public WaitBrick(int timeToWaitInMilliseconds) {
		
	}

	public void execute() {
		// TODO Auto-generated method stub
		
	}

	public Sprite getSprite() {
		return null;
	}

	/* (non-Javadoc)
	 * @see at.tugraz.ist.catroid.content.brick.Brick#getView(android.content.Context)
	 */
	public View getView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.construction_brick_simple_text_view, null);
		TextView textView = (TextView) view.findViewById(R.id.OneElementBrick);
		textView.setText(R.string.come_to_front_main_adapter);
		return view;
	}

}
