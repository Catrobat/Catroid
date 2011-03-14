//package at.tugraz.ist.catroid.constructionSite.gui.dialogs;
//
//import java.util.HashMap;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.net.Uri;
//import android.view.Gravity;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.animation.Animation;
//import android.view.animation.Animation.AnimationListener;
//import android.view.animation.AnimationUtils;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import at.tugraz.ist.catroid.R;
//import at.tugraz.ist.catroid.constructionSite.content.BrickDefine;
//import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
//import at.tugraz.ist.catroid.constructionSite.gui.adapter.ProgrammAdapter;
//import at.tugraz.ist.catroid.content.script.Script;
//import at.tugraz.ist.catroid.utils.Utils;
//
//public class ContextMenuDialog extends Dialog {
//
//    private Context mCtx;
//    private Animation mSlide_in;
//    private Animation mSlide_out;
//    private RelativeLayout mToolboxLayout;
//    private ProjectManager mContentManager;
//
//    private Button mCancelButton;
//    private Button mUpButton;
//    private Button mDownButton;
//    private Button mInfoButton;
//    private Button mDeleteButton;
//
//    private int mPositionOfView;
//    private ListView mElementListView;
//    private final Script script;
//
//    //TODO: the positions are wrong! mPositionOfView
//    
//    private class UpButtonListener implements View.OnClickListener {
//        public void onClick(View v) {
//            mContentManager.moveBrickUpInList(mPositionOfView, script);
//        }
//    }
//    
//    private class DownButtonListener implements View.OnClickListener {
//        public void onClick(View v) {
//            mContentManager.moveBrickDownInList(mPositionOfView, script);
//        }
//    }
//    
//    private class InfoButtonListener implements View.OnClickListener {
//        public void onClick(View v) {
//            showBrickInfo();
//        }
//    }
//    
//    private class DeleteButtonListener implements View.OnClickListener {
//        public void onClick(View v) {
//            //ImageContainer.getInstance().deleteImage(mContentManager.getCurrentSpriteCommandList().get(mPositionOfView).get(BrickDefine.BRICK_VALUE));
//            //ImageContainer.getInstance().deleteImage(mContentManager.getCurrentSpriteCommandList().get(mPositionOfView).get(BrickDefine.BRICK_VALUE_1));
//            mContentManager.removeBrick(mPositionOfView,script);
//            cancel();
//        }
//    }
//
//    private void showBrickInfo() {
//        // TODO: Link to proper Wiki once it's available
//        String wikiUrl = mCtx.getString(R.string.wiki_url);
//
//        @SuppressWarnings("unchecked")
//        HashMap<String, String> brick = (HashMap<String, String>) mElementListView.getItemAtPosition(mPositionOfView);
//        String brickName = brick.get(BrickDefine.BRICK_NAME);
//
//        String url = wikiUrl + "/" + brickName;
//
//        Utils.displayWebsite(mCtx, Uri.parse(url));
//    }
//
//    public ContextMenuDialog(Context context, ProjectManager contentManager, Script script) {
//        super(context);
//        this.script = script;
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//        getWindow().setGravity(Gravity.LEFT);
//        setContentView(R.layout.context_menu);
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.dimAmount = 0.0f;
//        getWindow().setAttributes(lp);
//
//        mCtx = context;
//        mContentManager = contentManager;
//
//        mSlide_in = AnimationUtils.loadAnimation(mCtx, R.anim.toolbox_in);
//        mSlide_out = AnimationUtils.loadAnimation(mCtx, R.anim.toolbox_out);
//        mSlide_out.setAnimationListener(new AnimationListener() {
//
//            public void onAnimationStart(Animation animation) {
//            }
//
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            public void onAnimationEnd(Animation animation) {
//                close();
//            }
//        }
//
//        );
//
//        mToolboxLayout = (RelativeLayout) findViewById(R.id.ContextMenuRelativeLayout);
//        mCancelButton = (Button) findViewById(R.id.ContextMenuCancelButton);
//        mCancelButton.setOnClickListener(new Button.OnClickListener() {
//
//            public void onClick(View v) {
//                cancel();
//            }
//        });
//        mUpButton = (Button) findViewById(R.id.ContextMenuUpButton);
//        mUpButton.setOnClickListener(new UpButtonListener());
//        
//        mDownButton = (Button) findViewById(R.id.ContextMenuDownButton);
//        mDownButton.setOnClickListener(new DownButtonListener());
//        
//        mInfoButton = (Button) findViewById(R.id.ContextMenuInfoButton);
//        mInfoButton.setOnClickListener(new InfoButtonListener());
//        
//        mDeleteButton = (Button) findViewById(R.id.ContextMenuDeleteButton);
//        mDeleteButton.setOnClickListener(new DeleteButtonListener());
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        cancel();
//        return super.onTouchEvent(event);
//    }
//
//    public void show(View element, int position, ListView listView) {
//        super.show();
//        mPositionOfView = position;
//        mToolboxLayout.startAnimation(mSlide_in);
//        mElementListView = listView;
//        //((ConstructionSiteListViewAdapter) mElementListView.getAdapter()).setAnimationOnPosition(mPositionOfView);
//        ((ProgrammAdapter) mElementListView.getAdapter()).notifyDataSetChanged();
//    }
//
//    @Override
//    public void cancel() {
//        //((ConstructionSiteListViewAdapter) mElementListView.getAdapter()).setAnimationOnPosition(-1);
//        ((ProgrammAdapter) mElementListView.getAdapter()).notifyDataSetChanged();
//        mToolboxLayout.startAnimation(mSlide_out);
//    }
//
//    private void close() {
//        super.cancel();
//    }
//
//}
