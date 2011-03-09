package at.tugraz.ist.catroid.constructionSite.gui.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ListView;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.constructionSite.gui.adapter.ToolBoxAdapter;
import at.tugraz.ist.catroid.content.brick.gui.Brick;
import at.tugraz.ist.catroid.content.brick.gui.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.gui.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.brick.gui.HideBrick;
import at.tugraz.ist.catroid.content.brick.gui.IfTouchedBrick;
import at.tugraz.ist.catroid.content.brick.gui.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.gui.PlaySoundBrick;
import at.tugraz.ist.catroid.content.brick.gui.SetCostumeBrick;
import at.tugraz.ist.catroid.content.brick.gui.ShowBrick;
import at.tugraz.ist.catroid.content.brick.gui.WaitBrick;
import at.tugraz.ist.catroid.content.script.Script;

public class ToolBoxDialog extends Dialog {

	private Animation slideInAnimation;
	private Animation slideOutAnimation;
	private ToolBoxAdapter adapter;
	private LinearLayout layout;

	private List<Brick> brickList;
	private ListView listView;

	private void setupBrickPrototypes() {
		brickList = new ArrayList<Brick>();
		brickList.add(new PlaySoundBrick(""));
		brickList.add(new WaitBrick(1000));
		brickList.add(new HideBrick(null));
		brickList.add(new ShowBrick(null));
		brickList.add(new PlaceAtBrick(null, 100, 200));
		brickList.add(new SetCostumeBrick(null));
		brickList.add(new GoNStepsBackBrick(null, 1));
		brickList.add(new ComeToFrontBrick(null, null));
		brickList.add(new IfTouchedBrick(null, new Script()));
	}

	public ToolBoxDialog(Context context, ProjectManager contentManager) {
		super(context);
		setupBrickPrototypes();

		// adjust window
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
		setContentView(R.layout.dialog_toolbox);

		// initialize animations
		slideInAnimation = AnimationUtils.loadAnimation(context, R.anim.toolbox_in);
		slideOutAnimation = AnimationUtils.loadAnimation(context, R.anim.toolbox_out);
		slideOutAnimation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				close();
			}
		});

		layout = (LinearLayout) findViewById(R.id.toolbox_layout);
		listView = (ListView) findViewById(R.id.toolboxListView);
		listView.setAdapter(new ToolBoxAdapter(context, brickList));

	}

	@Override
	public void show() {
		super.show();
		layout.startAnimation(slideInAnimation);
	}

	@Override
	public void cancel() {
		layout.startAnimation(slideOutAnimation);

	}

	private void close() {
		super.cancel();
	}

	public Brick getBrickClone(View v) {
		return adapter.getItem(listView.getPositionForView(v)).clone();
	}

}
