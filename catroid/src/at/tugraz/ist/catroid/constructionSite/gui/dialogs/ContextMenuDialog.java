package at.tugraz.ist.catroid.constructionSite.gui.dialogs;

import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.BrickDefine;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;
import at.tugraz.ist.catroid.constructionSite.gui.adapter.ConstructionSiteListViewAdapter;
import at.tugraz.ist.catroid.utils.ImageContainer;

public class ContextMenuDialog extends Dialog {

	private Context mCtx;
	private Animation mSlide_in;
	private Animation mSlide_out;
	private RelativeLayout mToolboxLayout;
	private ContentManager mContentManager;

	private Button mCancelButton;
	private Button mUpButton;
	private Button mDownButton;
	private Button mInfoButton;
	private Button mDeleteButton;

	private int mPositionOfView;
	private ListView mElementListView;
	
	private void showBrickInfo() {
		// TODO: Link to proper Wiki once it's available
		String wikiUrl = mCtx.getString(R.string.wiki_url);

		@SuppressWarnings("unchecked")
		HashMap<String, String> brick = (HashMap<String, String>) mElementListView.getItemAtPosition(mPositionOfView);
		String brickName = brick.get(BrickDefine.BRICK_NAME);

		String url = wikiUrl + "/" + brickName;

		Intent websiteIntent = new Intent(Intent.ACTION_VIEW);
		websiteIntent.setData(Uri.parse(url));
		mCtx.startActivity(websiteIntent);
	}

	public ContextMenuDialog(Context context, ContentManager contentManager) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		getWindow().setGravity(Gravity.LEFT);
		setContentView(R.layout.context_menu);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.dimAmount = 0.0f;
		getWindow().setAttributes(lp);

		mCtx = context;
		mContentManager = contentManager;

		mSlide_in = AnimationUtils.loadAnimation(mCtx, R.anim.toolbox_in);
		mSlide_out = AnimationUtils.loadAnimation(mCtx, R.anim.toolbox_out);
		mSlide_out.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
				close();
			}
		}

		);

		mToolboxLayout = (RelativeLayout) findViewById(R.id.ContextMenuRelativeLayout);
		mCancelButton = (Button) findViewById(R.id.ContextMenuCancelButton);
		mCancelButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				cancel();
			}
		});
		mUpButton = (Button) findViewById(R.id.ContextMenuUpButton);
		mUpButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				if (mContentManager.moveBrickUpInList(mPositionOfView)) {
					mPositionOfView--;
					((ConstructionSiteListViewAdapter) mElementListView.getAdapter()).setAnimationOnPosition(mPositionOfView);
				}
			}
		});
		mDownButton = (Button) findViewById(R.id.ContextMenuDownButton);
		mDownButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				if (mContentManager.moveBrickDownInList(mPositionOfView)) {
					mPositionOfView++;
					((ConstructionSiteListViewAdapter) mElementListView.getAdapter()).setAnimationOnPosition(mPositionOfView);
				}
			}
		});
		mInfoButton = (Button) findViewById(R.id.ContextMenuInfoButton);
		mInfoButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				showBrickInfo();
			}
		});
		mDeleteButton = (Button) findViewById(R.id.ContextMenuDeleteButton);
		mDeleteButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				ImageContainer.getInstance().deleteImage(mContentManager.getCurrentSpriteList().get(mPositionOfView).get(BrickDefine.BRICK_VALUE));
				ImageContainer.getInstance().deleteImage(mContentManager.getCurrentSpriteList().get(mPositionOfView).get(BrickDefine.BRICK_VALUE_1));
				mContentManager.removeBrick(mPositionOfView);

				cancel();
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		cancel();
		return super.onTouchEvent(event);
	}

	public void show(View element, int position, ListView listView) {
		super.show();
		mPositionOfView = position;
		mToolboxLayout.startAnimation(mSlide_in);
		mElementListView = listView;
		((ConstructionSiteListViewAdapter) mElementListView.getAdapter()).setAnimationOnPosition(mPositionOfView);
		((ConstructionSiteListViewAdapter) mElementListView.getAdapter()).notifyDataSetChanged();
	}

	@Override
	public void cancel() {
		((ConstructionSiteListViewAdapter) mElementListView.getAdapter()).setAnimationOnPosition(-1);
		((ConstructionSiteListViewAdapter) mElementListView.getAdapter()).notifyDataSetChanged();
		mToolboxLayout.startAnimation(mSlide_out);
	}

	private void close() {
		super.cancel();
	}

}
