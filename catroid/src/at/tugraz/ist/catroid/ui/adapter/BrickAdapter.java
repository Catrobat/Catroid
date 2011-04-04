/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.adapter;

/**
 * @author DENISE, DANIEL
 *
 */

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.content.brick.IfStartedBrick;
import at.tugraz.ist.catroid.content.brick.IfTouchedBrick;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.ui.dragndrop.DragNDropListView;
import at.tugraz.ist.catroid.ui.dragndrop.DragNDropListView.DropListener;
import at.tugraz.ist.catroid.ui.dragndrop.DragNDropListView.RemoveListener;


public class BrickAdapter extends BaseExpandableListAdapter implements DropListener, RemoveListener, OnGroupClickListener {

	private Context context;
	private Sprite sprite;
	private boolean animateChildren;

	public BrickAdapter(Context context, Sprite sprite, DragNDropListView listView) {
		this.context = context;
		this.sprite = sprite;
	}
    
    public Brick getChild(int groupPosition, int childPosition) {
        return sprite.getScriptList().get(groupPosition).getBrickList().get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Brick brick = getChild(groupPosition, childPosition);
        
        View currentBrickView = brick.getView(context, childPosition, this);
        if(!animateChildren)
            return currentBrickView;
        AnimationSet set =  new AnimationSet(true);
        Animation currentAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.ABSOLUTE, -80*(childPosition+1),Animation.RELATIVE_TO_SELF, 0.0f
            );
        currentAnimation.setAnimationListener(new AnimationListener() {
            
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                
            }
            
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
                
            }
            
            public void onAnimationEnd(Animation animation) {
                animateChildren = false;
                
            }
        });
        set.addAnimation(currentAnimation);
        Animation alpha = new AlphaAnimation(0.0f, 1.0f);
        set.addAnimation(alpha);
        set.setDuration(800);
        set.setFillBefore(true);
        set.setFillAfter(true);
        set.setStartTime(AnimationUtils.currentAnimationTimeMillis()+200);
        currentBrickView.setAnimation(set);
           
        return currentBrickView;
    }

    public int getChildrenCount(int groupPosition) {
        return sprite.getScriptList().get(groupPosition).getBrickList().size();
    }

    public Script getGroup(int groupPosition) {
        return sprite.getScriptList().get(groupPosition);
    }

    public int getGroupCount() {
        return sprite.getScriptList().size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View v;
        if(getGroup(groupPosition).isTouchScript()) {
            System.out.println("group: "+groupPosition+" touch script");
            v = new IfTouchedBrick(sprite, getGroup(groupPosition)).getPrototypeView(context);
        } else {
            System.out.println("group: "+groupPosition+" no touch script");
            v = new IfStartedBrick(sprite, getGroup(groupPosition)).getPrototypeView(context);
        }
        return v;
    }

    public boolean hasStableIds() {
        return false;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void drop(int from, int to) {
        if(from == to)
            return;
        System.out.println("drop from: "+from+" to: "+to);
        ArrayList<Brick> brickList = sprite.getScriptList().get(getGroupCount()-1).getBrickList();
        Brick removedBrick = brickList.remove(from);
        brickList.add(to, removedBrick);
        notifyDataSetChanged();
    }       
 
    public void remove(int which) {
        ArrayList<Brick> brickList = sprite.getScriptList().get(getGroupCount()-1).getBrickList();
        brickList.remove(which);
        notifyDataSetChanged();
        
    }
    
    public boolean onGroupClick(final ExpandableListView parent, View v, final int groupPosition, long id) {
        if(groupPosition == getGroupCount()-1)
            return false;
        
        animateChildren = true;
        Animation up_animation = new TranslateAnimation(
              Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
              Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, -(float)(getGroupCount()-groupPosition-1)
        );
        up_animation.setDuration(1200);  
        up_animation.setFillAfter(true);
        getChildFromAbsolutePosition(parent, getGroupCount()-1).startAnimation(up_animation);
     
        doCollapseAnimation(parent);
        
        Animation down_animation = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, (float)(getGroupCount()-groupPosition-1)
        );
        down_animation.setDuration(1200);
        down_animation.setFillAfter(true);
        
        down_animation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
                System.out.println("onAnimationStart");   
            }
            public void onAnimationRepeat(Animation animation) {
                System.out.println("onAnimationRepeat");
            }
            public void onAnimationEnd(Animation animation) {
                System.out.println("onAnimationEnd");
                doReordering(parent, groupPosition);
            }
        });
        getChildFromAbsolutePosition(parent, groupPosition).startAnimation(down_animation);
        
        return true;
    }       
   
    private void doCollapseAnimation(ExpandableListView parent) {
        int visibleGroups = getGroupCount()-parent.getFirstVisiblePosition();
        int visibleChilds = parent.getChildCount()-visibleGroups;
        
        Animation currentAnimation = new AlphaAnimation(1.0f, 0.0f);
        currentAnimation.setDuration(1200);
        currentAnimation.setFillAfter(true);
        for(int i=0;i<visibleChilds;++i) {
            getChildFromAbsolutePosition(parent, getGroupCount()+i).startAnimation(currentAnimation);
        }
    }
    
    private View getChildFromAbsolutePosition(ExpandableListView parent, int absolutePosition) {
        int displayedPosition = absolutePosition-parent.getFirstVisiblePosition();
        return parent.getChildAt(displayedPosition);
    }
    
    private void doReordering(ExpandableListView parent, int groupPosition) {
        for(int i=0;i<getGroupCount();++i)
            parent.collapseGroup(i);
        Script currentScript = sprite.getScriptList().get(groupPosition);
        boolean scriptDeleted = sprite.getScriptList().remove(currentScript);
        if(scriptDeleted) {
            sprite.getScriptList().add(currentScript);
            System.out.println("script reorder OK");
        }
        
        ProjectManager.getInstance().setCurrentScript(currentScript);
        
        notifyDataSetChanged();
        parent.expandGroup(getGroupCount()-1);
        
    }
    
    public int getChildCountFromLastGroup() {
        return getChildrenCount(getGroupCount()-1);
    }
    
    //padding zu eingeklaptem brick
    // animationen fertig
}
    