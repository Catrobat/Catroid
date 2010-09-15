package at.tugraz.ist.catroid.constructionSite.gui.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.constructionSite.content.BrickDefine;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;
import at.tugraz.ist.catroid.constructionSite.gui.adapter.ConstructionSiteListViewAdapter;
import at.tugraz.ist.catroid.constructionSite.gui.adapter.ToolBoxAdapter;
import at.tugraz.ist.catroid.utils.ImageContainer;
import at.tugraz.ist.catroid.R;

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
	private ListView mElementListeView;
	
	
	public ContextMenuDialog(Context context, ContentManager contentManager) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		getWindow().setGravity(Gravity.LEFT);
		setContentView(R.layout.context_menu);
		WindowManager.LayoutParams lp = getWindow().getAttributes();  
		lp.dimAmount=0.0f;  
		getWindow().setAttributes(lp); 
		
		
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
				if(mContentManager.moveBrickUpInList(mPositionOfView)){
					mPositionOfView--;
					((ConstructionSiteListViewAdapter)mElementListeView.getAdapter()).setAnimationOnPosition(mPositionOfView);
				}
			}
		});
		mDownButton = (Button) findViewById(R.id.ContextMenuDownButton);
		mDownButton.setOnClickListener(new Button.OnClickListener() {
			
			
			public void onClick(View v) {
				if(mContentManager.moveBrickDownInList(mPositionOfView)){
					mPositionOfView++;
					((ConstructionSiteListViewAdapter)mElementListeView.getAdapter()).setAnimationOnPosition(mPositionOfView);
				}
			}
		});
		mInfoButton = (Button) findViewById(R.id.ContextMenuInfoButton);
		mInfoButton.setOnClickListener(new Button.OnClickListener() {
			
			
			public void onClick(View v) {
				Toast.makeText(mCtx, "Information following soon", Toast.LENGTH_SHORT).show();
				
			}
		});
		mDeleteButton = (Button) findViewById(R.id.ContextMenuDeleteButton);
		mDeleteButton.setOnClickListener(new Button.OnClickListener() {
			
			
			public void onClick(View v) {
				ImageContainer.getInstance()
					.deleteImage(mContentManager.getCurrentSpriteList().get(mPositionOfView).get(BrickDefine.BRICK_VALUE));
				ImageContainer.getInstance()
					.deleteImage(mContentManager.getCurrentSpriteList().get(mPositionOfView).get(BrickDefine.BRICK_VALUE_1));
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
		mElementListeView = listView;
		((ConstructionSiteListViewAdapter)mElementListeView.getAdapter()).setAnimationOnPosition(mPositionOfView);
		((ConstructionSiteListViewAdapter)mElementListeView.getAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public void cancel() {
		((ConstructionSiteListViewAdapter)mElementListeView.getAdapter()).setAnimationOnPosition(-1);
		((ConstructionSiteListViewAdapter)mElementListeView.getAdapter()).notifyDataSetChanged();
		mToolboxLayout.startAnimation(mSlide_out);
	}
	
	private void close() {
		super.cancel();
	}
	
}
