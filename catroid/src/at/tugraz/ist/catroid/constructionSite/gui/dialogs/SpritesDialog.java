package at.tugraz.ist.catroid.constructionSite.gui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;
import at.tugraz.ist.catroid.constructionSite.gui.adapter.SpritesAdapter;
import at.tugraz.ist.catroid.R;


public class SpritesDialog extends Dialog implements Observer

{

	private Context mCtx;
	private Animation mSlide_in;
	private Animation mSlide_out;
	
	public ListView mMainListView;
	public EditText mSpriteName;
	public Button mSpriteButton;   
	private SpritesAdapter mAdapter;
	public ArrayList<String> mContentArrayList;
	ContentManager mContentManager;
	
	private RelativeLayout mToolboxLayout;
	
	public SpritesDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener, int flagid) {
		super(context, cancelable, cancelListener);
		mCtx = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		getWindow().setGravity(Gravity.TOP);
		//getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.dialog_sprites_list);
		//this.setTitle("Objekte");
		
		mSlide_in = AnimationUtils.loadAnimation(mCtx, R.anim.toolboxsprites_in);
		mSlide_out = AnimationUtils.loadAnimation(mCtx, R.anim.toolboxsprites_out);
		mSlide_out.setAnimationListener(new AnimationListener() {
			
			
			public void onAnimationStart(Animation animation) {		}
			
			public void onAnimationRepeat(Animation animation) {		}
			
			public void onAnimationEnd(Animation animation) {
				close();
			}
		}
	
		);
		
		mToolboxLayout = (RelativeLayout) findViewById(R.id.toolboxsprites_layout);
		
		mContentArrayList = mContentManager.getAllContentNameList();
		
		mMainListView = (ListView) findViewById(R.id.spritesListView);
		
		mAdapter = new SpritesAdapter(mCtx, mContentArrayList);
		mContentManager.setObserver(this);
		
		mMainListView.setAdapter(mAdapter);
		
		mMainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mContentManager.switchSprite(arg2);
				dismiss();
			}
		});
		mSpriteName = (EditText) findViewById(R.id.newsprite);
		mSpriteButton = (Button) findViewById(R.id.NewSpriteButton);
		mSpriteButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				mContentManager.addSprite(new Pair<String,ArrayList<HashMap<String,String>>>(mSpriteName.getText().toString(), new ArrayList<HashMap<String,String>>()));
				mAdapter.notifyDataSetChanged();
				dismiss();
				}

		});
	}

	@Override
	public void show() {
		mAdapter.notifyDataSetChanged();
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
	public void testSet(){
        mContentArrayList.add("Sprite");
        
	}

	

	public void setContentManager(ContentManager contentManager){
		mContentManager = contentManager;
	}
	

	public void update(Observable observable, Object data) {
		mAdapter.notifyDataSetChanged();	
	}
	
}
