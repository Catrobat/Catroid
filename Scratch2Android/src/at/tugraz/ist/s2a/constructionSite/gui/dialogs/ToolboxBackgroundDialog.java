package at.tugraz.ist.s2a.constructionSite.gui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import at.tugraz.ist.s2a.R;
import at.tugraz.ist.s2a.R.anim;
import at.tugraz.ist.s2a.R.id;
import at.tugraz.ist.s2a.R.layout;
import at.tugraz.ist.s2a.constructionSite.content.BrickDefine;
import at.tugraz.ist.s2a.constructionSite.content.ContentManager;
import at.tugraz.ist.s2a.constructionSite.gui.adapter.ToolboxBackgroundAdapter;

public class ToolboxBackgroundDialog extends Dialog

{

	private Context mCtx;
	private Animation mSlide_in;
	private Animation mSlide_out;
	
	protected ListView mMainListView;
	private BaseAdapter mAdapter;
	public ArrayList<HashMap<String, String>> mContentArrayList;
	ContentManager mContentManager;
	   
	
	private LinearLayout mToolboxLayout;
	
	
	public ToolboxBackgroundDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener, int flagid) {
		super(context, cancelable, cancelListener);
		mCtx = context;
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		getWindow().setGravity(Gravity.LEFT);
		//getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.toolbox);
		this.setTitle("Baukasten");
		
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
		
		//Set Bricks
		mContentArrayList = new ArrayList<HashMap<String,String>>();
		mMainListView = (ListView) findViewById(R.id.toolboxListView);
		
		allBricks();
		mAdapter = new ToolboxBackgroundAdapter(mCtx, mContentArrayList);
		((ToolboxBackgroundAdapter)mAdapter).setContentManager(mContentManager);
		
		mMainListView.setAdapter(mAdapter);
		((ToolboxBackgroundAdapter)mAdapter).setDialog(this);
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
	
	/**
	 * test method
	 *
	 */
	public void allBricks(){
		
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "1");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_BACKGROUND));
        map.put(BrickDefine.BRICK_NAME, "");
        map.put(BrickDefine.BRICK_VALUE, "1");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "2");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.PLAY_SOUND));
        map.put(BrickDefine.BRICK_NAME, "");
        map.put(BrickDefine.BRICK_VALUE, "1");
        mContentArrayList.add(map);
        map = new HashMap<String, String>();
        map.put(BrickDefine.BRICK_ID, "3");
        map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
        map.put(BrickDefine.BRICK_NAME, "");
        map.put(BrickDefine.BRICK_VALUE, "1");
        mContentArrayList.add(map);
       }

	

	public void setContentManager(ContentManager contentManager){
		mContentManager = contentManager;
	}
	

	
}
