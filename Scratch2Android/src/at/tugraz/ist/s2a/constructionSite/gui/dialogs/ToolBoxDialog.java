package at.tugraz.ist.s2a.constructionSite.gui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import at.tugraz.ist.s2a.R;
import at.tugraz.ist.s2a.constructionSite.content.ContentManager;
import at.tugraz.ist.s2a.constructionSite.gui.adapter.ToolBoxAdapter;

public class ToolBoxDialog extends Dialog{

	
	private Context mCtx;
	private Animation mSlide_in;
	private Animation mSlide_out;
	private ToolBoxAdapter mAdapter;
	private LinearLayout mToolboxLayout;
	private ContentManager mContentManager;
	
	private ArrayList<HashMap<String, String>> mContent;
	
	protected ListView mMainListView;	
	protected ArrayList<HashMap<String, String>> mContentArrayList;
	
	public ToolBoxDialog(Context context, ContentManager contentManager, 
			ArrayList<HashMap<String, String>> content) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		setContentView(R.layout.dialog_toolbox);
		mCtx = context;
		mContentManager = contentManager;
		mContent = content;
		
		mToolboxLayout = (LinearLayout) findViewById(R.id.toolbox_layout);
		mMainListView = (ListView) findViewById(R.id.toolboxListView);
		
		mAdapter = new ToolBoxAdapter(mCtx, mContent);
		mMainListView.setAdapter(mAdapter);
		mMainListView.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mContentManager.add(((HashMap<String, String>) mAdapter.getItem(arg2).clone()));
				dismiss();
			}
		});
		
		
		
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
	   
	
	
	

}
