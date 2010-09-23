package at.tugraz.ist.catroid.constructionSite.gui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import at.tugraz.ist.catroid.constructionSite.content.BrickDefine;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;
import at.tugraz.ist.catroid.constructionSite.gui.adapter.ToolBoxAdapter;
import at.tugraz.ist.catroid.R;

public class ToolBoxDialog extends Dialog{

	
	private Context mCtx;
	private Animation mSlide_in;
	private Animation mSlide_out;
	private ToolBoxAdapter mAdapter;
	private LinearLayout mToolboxLayout;
	private ContentManager mContentManager;
	
	private ArrayList<HashMap<String, String>> mContent;

	protected ListView mMainListView;	
	
	public ToolBoxDialog(Context context, ContentManager contentManager, 
			ArrayList<HashMap<String, String>> content) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
		setContentView(R.layout.dialog_toolbox);
		mCtx = context;
		mContentManager = contentManager;
		
		
		mSlide_in = AnimationUtils.loadAnimation(mCtx, R.anim.toolbox_in);
		mSlide_out = AnimationUtils.loadAnimation(mCtx, R.anim.toolbox_out);
		mSlide_out.setAnimationListener(new AnimationListener() {
			
			
			public void onAnimationStart(Animation animation) {		}
			
			public void onAnimationRepeat(Animation animation) {		}
			
			public void onAnimationEnd(Animation animation) {
				close();
			}
		}
	
		);
		
		mToolboxLayout = (LinearLayout) findViewById(R.id.toolbox_layout);
		mContent = content;

		mMainListView = (ListView) findViewById(R.id.toolboxListView);
		
		mAdapter = new ToolBoxAdapter(mCtx, mContent);
		mMainListView.setAdapter(mAdapter);

	}

	@Override
	public void show() {
		super.show();
		mToolboxLayout.startAnimation(mSlide_in);
	}

	@Override
	public void cancel() {
		mToolboxLayout.startAnimation(mSlide_out);
		
	}
	
	private void close() {
		super.cancel();
	}
	   
	public HashMap<String, String> getBrickClone(View v){
		return (HashMap<String, String>) mAdapter.getItem(mMainListView.getPositionForView(v)).clone();
	}
	
}
