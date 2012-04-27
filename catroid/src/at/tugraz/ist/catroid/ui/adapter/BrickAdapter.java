/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.ui.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.BroadcastScript;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.WhenScript;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.content.bricks.BroadcastReceiverBrick;
import at.tugraz.ist.catroid.content.bricks.LoopBeginBrick;
import at.tugraz.ist.catroid.content.bricks.LoopEndBrick;
import at.tugraz.ist.catroid.content.bricks.WhenBrick;
import at.tugraz.ist.catroid.content.bricks.WhenStartedBrick;
import at.tugraz.ist.catroid.ui.ScriptActivity;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListView;
import at.tugraz.ist.catroid.ui.dragndrop.DragAndDropListener;

public class BrickAdapter extends BaseAdapter implements DragAndDropListener {

	public static final int FOCUS_BLOCK_DESCENDANTS = 2;

	private Context context;
	private Sprite sprite;
	// private BrickListAnimation brickListAnimation;
	private int dragTargetPosition;
	private Brick draggedBrick;
	private OnLongClickListener longClickListener;
	private View insertionView;
	private int currentScriptPosition;
	private boolean insertedBrick;
	private boolean insertLoop;
	private int pos;
	private int fromTest;

	public BrickAdapter(Context context, Sprite sprite, DragAndDropListView listView) {
		this.context = context;
		this.sprite = sprite;
		// brickListAnimation = new BrickListAnimation(this, listView);
		longClickListener = listView;
		insertionView = View.inflate(context, R.layout.brick_insert, null);
		insertedBrick = false;
		insertLoop = false;
	}

	public void drag(int from, int to) {
		fromTest = from;
		int scriptFrom = getScriptId(from);
		int scriptTo = getScriptId(to);
		//		Log.d("TESTING", "Drag called from: " + from + ", to: " + to);

		if (isBrick(to)) {
			if (draggedBrick == null) {
				if (isBrick(from)) {
					draggedBrick = (Brick) getItem(from);
				} else {
					Log.d("Warning", "BrickAdapter.drag() from was Script not Brick. should not happen!!!");
				}
				notifyDataSetChanged();
			}

			if (to != 0) {
				dragTargetPosition = to;
			} else {
				dragTargetPosition = 1;
			}

			ArrayList<Brick> brickList = sprite.getScript(getScriptId(from)).getBrickList();
			if (draggedBrick instanceof LoopBeginBrick) {
				LoopEndBrick loopEndBrick = ((LoopBeginBrick) draggedBrick).getLoopEndBrick();
				if (loopEndBrick != null) {

					//NEW
					Script script = ProjectManager.getInstance().getCurrentSprite().getScript(scriptTo);
					if (script.getBrickList().indexOf(((LoopBeginBrick) draggedBrick).getLoopEndBrick()) == -1) {
						dragTargetPosition = -1;
						return;
					}
					//NEW END
					if (getScriptPosition(to, scriptTo) >= brickList.indexOf(loopEndBrick)
							|| getScriptPosition(from, scriptFrom) >= brickList.indexOf(loopEndBrick)) {
						return;
					}
				} else {
					insertLoop = true;
				}
			} else if (draggedBrick instanceof LoopEndBrick) {
				LoopBeginBrick loopBeginBrick = ((LoopEndBrick) draggedBrick).getLoopBeginBrick();

				Script script = ProjectManager.getInstance().getCurrentSprite().getScript(scriptTo);
				if (script.getBrickList().indexOf(((LoopEndBrick) draggedBrick).getLoopBeginBrick()) == -1) {
					dragTargetPosition = -1;
					return;
				}
				if (getScriptPosition(to, scriptTo) <= brickList.indexOf(loopBeginBrick)
						|| getScriptPosition(from, scriptFrom) <= brickList.indexOf(loopBeginBrick)) {
					return;
				}
			}

			if (from != to) {
				sprite.getScript(scriptFrom).removeBrick(draggedBrick);

				sprite.getScript(scriptTo).addBrick(getScriptPosition(to, scriptTo), draggedBrick);
			}

		} else {

			if (to != 0) {
				dragTargetPosition = to;
			} else {
				dragTargetPosition = 1;
			}

			if (draggedBrick instanceof LoopBeginBrick) {
				if (((LoopBeginBrick) draggedBrick).getLoopEndBrick() != null) {
					dragTargetPosition = -1;
					return;
				}
			}

			if (draggedBrick instanceof LoopEndBrick) {
				dragTargetPosition = -1;
				return;
			}

			if (from < to) {

				if (draggedBrick instanceof LoopEndBrick) {
					dragTargetPosition = -1;
					return;
				}
				sprite.getScript(getScriptId(to)).addBrick(0, draggedBrick);
				sprite.getScript(scriptFrom).removeBrick(draggedBrick);
				if (currentScriptPosition != 0 && from < currentScriptPosition) {
					currentScriptPosition--;
				}
			} else if (from > to && to > 0) {

				if (draggedBrick instanceof LoopEndBrick) {
					dragTargetPosition = -1;
					return;
				}

				sprite.getScript(getScriptId(to) - 1).addBrick(
						sprite.getScript(getScriptId(to) - 1).getBrickList().size(), draggedBrick);
				sprite.getScript(scriptFrom).removeBrick(draggedBrick);
				if (!(currentScriptPosition < to)) {
					currentScriptPosition++;
				}
			}
		}

		notifyDataSetChanged();
	}

	public void drop(int to) {
		boolean toLastScript = false;
		boolean intersect = false;

		if (to < 0) {
			int nrScripts = ProjectManager.getInstance().getCurrentSprite().getNumberOfScripts();

			int newTo = 0;

			for (int i = 0; i < nrScripts; i++) {
				int tmp = ProjectManager.getInstance().getCurrentSprite().getScript(i).getBrickList().size();

				if (tmp == 0) {
					tmp++;
				}

				newTo += tmp;
			}
			to = newTo;
			toLastScript = true;
		}

		if (draggedBrick instanceof WhenBrick) {

			int sId = getScriptId(to);
			ProjectManager projectManager = ProjectManager.getInstance();
			Script newScript = new WhenScript(projectManager.getCurrentSprite());
			if (toLastScript) {
				projectManager.getCurrentSprite().addScript(newScript);
			} else {
				ArrayList<Brick> bricks = projectManager.getCurrentSprite().getScript(sId).getBrickList();

				ArrayList<Brick> tmpList = new ArrayList<Brick>();

				int brickToScript = 0;
				for (Brick brick : bricks) {

					if (brick instanceof WhenBrick) {
						if (intersectLoop(sId, brickToScript)) {
							DragAndDropListView listView = (DragAndDropListView) ((ScriptActivity) context)
									.findViewById(R.id.brick_list_view);
							int additional = 0;
							for (int i = 0; i < sId; i++) {
								additional += sprite.getScript(i).getBrickList().size();
							}
							setInsertedBrickpos(brickToScript + sId + 1 + additional);
							intersect = true;
							listView.setInsertedBrick(brickToScript + sId + 1 + additional);

						} else {
							projectManager.getCurrentSprite().getScript(sId).removeBrick(brick);
						}
						break;
					}
					brickToScript++;
				}

				if (!intersect) {

					for (Brick brick : bricks) {
						tmpList.add(brick);
					}

					projectManager.getCurrentSprite().addScript(sId + 1, newScript);

					for (int j = brickToScript; j < tmpList.size(); j++) {
						Brick brickToCopy = tmpList.get(j);
						projectManager.getCurrentSprite().getScript(sId + 1).addBrick(brickToCopy);
					}

					for (int i = bricks.size(); i > brickToScript; i--) {
						projectManager.getCurrentSprite().getScript(sId).removeBrick(bricks.get(bricks.size() - 1));
					}
				}
			}

		} else if (draggedBrick instanceof WhenStartedBrick) {

			int sId = getScriptId(to);
			ProjectManager projectManager = ProjectManager.getInstance();
			Script newScript = new StartScript(projectManager.getCurrentSprite());
			if (toLastScript) {
				projectManager.getCurrentSprite().addScript(newScript);
			} else {
				ArrayList<Brick> bricks = projectManager.getCurrentSprite().getScript(sId).getBrickList();

				ArrayList<Brick> tmpList = new ArrayList<Brick>();

				int brickToScript = 0;
				for (Brick brick : bricks) {

					if (brick instanceof WhenStartedBrick) {

						if (intersectLoop(sId, brickToScript)) {
							DragAndDropListView listView = (DragAndDropListView) ((ScriptActivity) context)
									.findViewById(R.id.brick_list_view);
							int additional = 0;
							for (int i = 0; i < sId; i++) {
								additional += sprite.getScript(i).getBrickList().size();
							}
							setInsertedBrickpos(brickToScript + sId + 1 + additional);
							intersect = true;
							listView.setInsertedBrick(brickToScript + sId + 1 + additional);

						} else {
							projectManager.getCurrentSprite().getScript(sId).removeBrick(brick);
						}
						break;
					}
					brickToScript++;
				}

				if (!intersect) {
					for (Brick brick : bricks) {
						tmpList.add(brick);
					}

					projectManager.getCurrentSprite().addScript(sId + 1, newScript);

					for (int j = brickToScript; j < tmpList.size(); j++) {
						Brick brickToCopy = tmpList.get(j);
						projectManager.getCurrentSprite().getScript(sId + 1).addBrick(brickToCopy);
					}

					for (int i = bricks.size(); i > brickToScript; i--) {
						projectManager.getCurrentSprite().getScript(sId).removeBrick(bricks.get(bricks.size() - 1));
					}
				}
			}
		} else if (draggedBrick instanceof BroadcastReceiverBrick) {
			int sId = getScriptId(to);
			ProjectManager projectManager = ProjectManager.getInstance();
			Script newScript = new BroadcastScript(projectManager.getCurrentSprite());
			if (toLastScript) {
				projectManager.getCurrentSprite().addScript(newScript);
			} else {
				ArrayList<Brick> bricks = projectManager.getCurrentSprite().getScript(sId).getBrickList();

				ArrayList<Brick> tmpList = new ArrayList<Brick>();

				int brickToScript = 0;
				for (Brick brick : bricks) {

					if (brick instanceof BroadcastReceiverBrick) {

						if (intersectLoop(sId, brickToScript)) {
							DragAndDropListView listView = (DragAndDropListView) ((ScriptActivity) context)
									.findViewById(R.id.brick_list_view);
							int additional = 0;
							for (int i = 0; i < sId; i++) {
								additional += sprite.getScript(i).getBrickList().size();
							}
							setInsertedBrickpos(brickToScript + sId + 1 + additional);
							intersect = true;
							listView.setInsertedBrick(brickToScript + sId + 1 + additional);

						} else {
							projectManager.getCurrentSprite().getScript(sId).removeBrick(brick);
						}
						break;
					}
					brickToScript++;
				}

				if (!intersect) {
					for (Brick brick : bricks) {
						tmpList.add(brick);
					}

					projectManager.getCurrentSprite().addScript(sId + 1, newScript);

					for (int j = brickToScript; j < tmpList.size(); j++) {
						Brick brickToCopy = tmpList.get(j);
						projectManager.getCurrentSprite().getScript(sId + 1).addBrick(brickToCopy);
					}

					for (int i = bricks.size(); i > brickToScript; i--) {
						projectManager.getCurrentSprite().getScript(sId).removeBrick(bricks.get(bricks.size() - 1));
					}
				}
			}
		} else if (draggedBrick instanceof LoopBeginBrick) {
			if (insertLoop) {
				if (toLastScript) {
					LoopEndBrick loopEndBrick = new LoopEndBrick(ProjectManager.getInstance().getCurrentSprite(),
							(LoopBeginBrick) draggedBrick);

					int del = -1;
					int nrScripts = sprite.getNumberOfScripts();
					for (int i = 0; i < nrScripts; i++) {
						boolean breaker = false;
						ArrayList<Brick> bricks = sprite.getScript(i).getBrickList();
						for (Brick brick : bricks) {
							if (brick == draggedBrick) {
								del = i;
								breaker = true;
								break;
							}
						}
						if (breaker) {
							break;
						}
					}
					if (del >= 0) {
						ProjectManager.getInstance().getCurrentSprite().getScript(del).removeBrick(draggedBrick);
					}
					ProjectManager.getInstance().getCurrentSprite().getScript(nrScripts - 1).addBrick(draggedBrick);
					ProjectManager.getInstance().getCurrentSprite().getScript(nrScripts - 1).addBrick(loopEndBrick);

					((LoopBeginBrick) draggedBrick).setLoopEndBrick(loopEndBrick);
					insertLoop = false;

				} else {
					LoopEndBrick loopEndBrick = new LoopEndBrick(ProjectManager.getInstance().getCurrentSprite(),
							(LoopBeginBrick) draggedBrick);

					int sId = getScriptId(to);
					int bId = ProjectManager.getInstance().getCurrentSprite().getScript(sId).getBrickList()
							.indexOf(draggedBrick) + 1;

					ProjectManager.getInstance().getCurrentSprite().getScript(sId).addBrick(bId, loopEndBrick);

					((LoopBeginBrick) draggedBrick).setLoopEndBrick(loopEndBrick);
					insertLoop = false;
				}
			}
		} else {

			if (toLastScript) {
				if (draggedBrick instanceof LoopEndBrick) {
					LoopBeginBrick loopBegin = ((LoopEndBrick) draggedBrick).getLoopBeginBrick();
					Script lScript = sprite.getScript((sprite.getNumberOfScripts() - 1));
					ArrayList<Brick> bricks = lScript.getBrickList();
					boolean begin = false;
					for (Brick brick : bricks) {
						if (brick.equals(loopBegin)) {
							begin = true;
							break;
						}
					}
					if (begin) {
						int sId = getScriptId(fromTest);
						int toSCript = sprite.getNumberOfScripts() - 1;
						sprite.getScript(sId).removeBrick(draggedBrick);
						sprite.getScript(toSCript).addBrick(draggedBrick);
					}
				} else {
					int sId = getScriptId(fromTest);
					int toSCript = sprite.getNumberOfScripts() - 1;
					sprite.getScript(sId).removeBrick(draggedBrick);
					sprite.getScript(toSCript).addBrick(draggedBrick);
				}
			}
		}

		draggedBrick = null;
		if (!intersect) {
			clearScriptBricks();
		}
		notifyDataSetChanged();
	}

	private int clearScriptBricks() {

		ProjectManager projectManager = ProjectManager.getInstance();
		int nrScripts = projectManager.getCurrentSprite().getNumberOfScripts();

		for (int i = 0; i < nrScripts; i++) {

			Script tmpScript = projectManager.getCurrentSprite().getScript(i);

			if (tmpScript.containsBrickOfType(BroadcastReceiverBrick.class) == true) {

				int brickIndex = tmpScript.containsBrickOfTypeReturnsFirstIndex(BroadcastReceiverBrick.class);
				tmpScript.removeBrick(tmpScript.getBrick(brickIndex));
			}

			if (tmpScript.containsBrickOfType(WhenStartedBrick.class) == true) {

				int brickIndex = tmpScript.containsBrickOfTypeReturnsFirstIndex(WhenStartedBrick.class);
				tmpScript.removeBrick(tmpScript.getBrick(brickIndex));
			}

			if (tmpScript.containsBrickOfType(WhenBrick.class) == true) {

				int brickIndex = tmpScript.containsBrickOfTypeReturnsFirstIndex(WhenBrick.class);
				tmpScript.removeBrick(tmpScript.getBrick(brickIndex));
			}

		}

		return 0;
	}

	public void remove(int index) {

		if (index < 0) {
			index = getBrickPosition();
		}

		if (index < currentScriptPosition) {
			currentScriptPosition--;
		}

		if (draggedBrick instanceof LoopBeginBrick) {
			int del = 0;

			LoopBeginBrick loopBeginBrick = (LoopBeginBrick) draggedBrick;
			LoopEndBrick loopEnd = loopBeginBrick.getLoopEndBrick();
			if (loopEnd != null) {
				int nrScripts = sprite.getNumberOfScripts();
				for (int i = 0; i < nrScripts; i++) {
					boolean breaker = false;
					ArrayList<Brick> bricks = sprite.getScript(i).getBrickList();
					for (Brick brick : bricks) {
						if (brick == loopEnd) {
							del = i;
							breaker = true;
							break;
						}
					}
					if (breaker) {
						break;
					}
				}

				sprite.getScript(del).removeBrick(draggedBrick);
				sprite.getScript(del).removeBrick(loopEnd);
			} else {
				int nrScripts = sprite.getNumberOfScripts();
				for (int i = 0; i < nrScripts; i++) {
					boolean breaker = false;
					ArrayList<Brick> bricks = sprite.getScript(i).getBrickList();
					for (Brick brick : bricks) {
						if (brick == draggedBrick) {
							del = i;
							breaker = true;
							break;
						}
					}
					if (breaker) {
						break;
					}
				}
				sprite.getScript(del).removeBrick(draggedBrick);
			}

		} else if (draggedBrick instanceof LoopEndBrick) {
			int del = 0;

			LoopEndBrick loopEndBrick = (LoopEndBrick) draggedBrick;
			LoopBeginBrick loopBegin = loopEndBrick.getLoopBeginBrick();

			int nrScripts = sprite.getNumberOfScripts();
			for (int i = 0; i < nrScripts; i++) {
				boolean breaker = false;
				ArrayList<Brick> bricks = sprite.getScript(i).getBrickList();
				for (Brick brick : bricks) {
					if (brick == loopBegin) {
						del = i;
						breaker = true;
						break;
					}
				}
				if (breaker) {
					break;
				}
			}

			sprite.getScript(del).removeBrick(loopBegin);
			sprite.getScript(del).removeBrick(draggedBrick);
		} else {
			sprite.getScript(getScriptId(index)).removeBrick(draggedBrick);
		}

		draggedBrick = null;

		notifyDataSetChanged();
	}

	public OnLongClickListener getOnLongClickListener() {
		return longClickListener;
	}

	public int getCount() {

		int count = 0;
		for (int i = 0; i < sprite.getNumberOfScripts(); i++) {
			count += getBrickCount(i) + 1;
		}
		return count;
	}

	public Object getItem(int element) {

		int count = 0;
		while (element > getBrickCount(count)) {
			element -= getBrickCount(count) + 1;
			count++;
		}
		if (element == 0) {
			return sprite.getScript(count);
		} else {
			return sprite.getScript(count).getBrick(element - 1);
		}
	}

	public int getBrickCount(int scriptIndex) {
		return sprite.getScript(scriptIndex).getBrickList().size();
	}

	public long getItemId(int index) {
		return index;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		if (getItem(position) instanceof Brick) {
			View currentBrickView;

			if (getItem(position) instanceof WhenBrick) {
				WhenBrick brick = (WhenBrick) getItem(position);
				currentBrickView = brick.getPrototypeView(context);
			} else if (getItem(position) instanceof BroadcastReceiverBrick) {
				BroadcastReceiverBrick brick = (BroadcastReceiverBrick) getItem(position);
				currentBrickView = brick.getPrototypeView(context);
			} else {
				Brick brick = (Brick) getItem(position);
				currentBrickView = brick.getView(context, position, this);
			}

			if (draggedBrick != null && dragTargetPosition == position) {
				return insertionView;
			}

			// Hack!!!
			// if wrapper isn't used the longClick event won't be triggered
			//ViewGroup wrapper = (ViewGroup) View.inflate(context, R.layout.construction_brick_wrapper, null);
			ViewGroup wrapper = (ViewGroup) View.inflate(context, R.layout.brick_wrapper, null);
			if (currentBrickView.getParent() != null) {
				((ViewGroup) currentBrickView.getParent()).removeView(currentBrickView);
			}

			if (draggedBrick != null && dragTargetPosition == 0) {
				return null;
			}

			wrapper.addView(currentBrickView);
			wrapper.setOnLongClickListener(longClickListener);

			if (position == pos) {
				if (insertedBrick) {
					insertedBrick = false;
					DragAndDropListView listView = (DragAndDropListView) ((ScriptActivity) context)
							.findViewById(R.id.brick_list_view);
					listView.onLongClick(currentBrickView);
					return insertionView;
				}
			}

			return wrapper;

		} else {

			View view = null;

			if (getItem(position) instanceof BroadcastScript) {
				view = new BroadcastReceiverBrick(sprite, (BroadcastScript) getItem(position)).getView(context,
						position, this);
			} else if (getItem(position) instanceof StartScript) {
				view = new WhenStartedBrick(sprite, (Script) getItem(position)).getView(context, position, this);
			} else if (getItem(position) instanceof WhenScript) {
				view = new WhenBrick(sprite, (WhenScript) getItem(position)).getView(context, position, this);
			}
			return view;
		}
	}

	public int getScriptId(int index) {
		int count = 0;
		while (index > getBrickCount(count)) {

			index -= getBrickCount(count) + 1;
			count++;
		}
		return count;
	}

	private boolean isBrick(int index) {
		Object obj = getItem(index);
		if (obj instanceof Brick) {
			return true;
		} else {
			return false;
		}
	}

	private int getScriptPosition(int index, int script) {

		int scriptCount = 0;

		while (scriptCount < script) {
			index -= sprite.getScript(scriptCount).getBrickList().size() + 1;
			scriptCount++;
		}

		return --index;
	}

	public int getScriptCount() {
		return sprite.getNumberOfScripts();
	}

	public void setTouchedScript(int index) {
		if (!(index == currentScriptPosition)) {
			if (!(index < 0)) {
				if (!isBrick(index)) {
					if (draggedBrick == null) {
						ProjectManager.getInstance().setCurrentScript(sprite.getScript(getScriptId(index)));
						setCurrentScriptPosition(index);
					}
				}
			}
		}
	}

	public void setCurrentScriptPosition(int position) {
		currentScriptPosition = position;
	}

	public int getSpriteSize() {
		return sprite.getNumberOfScripts();
	}

	private int getBrickPosition() {
		int brickCount = 0;

		for (int count = 0; count < sprite.getNumberOfScripts(); count++) {
			brickCount += sprite.getScript(count).getBrickList().size() + 1;
		}

		return brickCount - 1;
	}

	// Just for Testing
	public int getChildCountFromLastGroup() {
		return getBrickCount(getScriptCount() - 1);
	}

	public int getGroupCount() {
		return getScriptCount();
	}

	public Brick getChild(int groupPosition, int childPosition) {

		return (Brick) getItem(getScriptId(groupPosition) + (childPosition + 1));
	}

	public int rearangeBricks(int pos) {

		Script script = ProjectManager.getInstance().getCurrentScript();
		Brick brick = script.getBrick(script.getBrickList().size() - 1);
		ProjectManager.getInstance().getCurrentScript().removeBrick(brick);

		int sId = getScriptId(pos);

		if (sId == 0 && pos > 0) {
			pos--;
		}

		for (int i = 0; i < sId; i++) {
			pos -= (sprite.getScript(i).getBrickList().size() + 1);
		}

		sprite.getScript(sId).addBrick(pos, brick);

		int newPos = 0;
		for (int i = 0; i < sId; i++) {
			newPos += (sprite.getScript(i).getBrickList().size() + 1);
		}
		newPos += pos + 1;

		notifyDataSetChanged();

		return newPos;
	}

	public void setInsertedBrickpos(int Npos) {
		insertedBrick = true;
		pos = Npos;
	}

	public boolean intersectLoop(int sId, int to) {

		Script dropScript = sprite.getScript(sId);
		ArrayList<Brick> bricks = dropScript.getBrickList();

		int loopBegin = 0;
		int loopEnd = 0;
		int globalCount = 0;
		for (Brick brick : bricks) {
			if (brick instanceof LoopBeginBrick) {
				loopBegin = globalCount;
			} else if (brick instanceof LoopEndBrick) {
				loopEnd = globalCount;
			}
			globalCount++;
			if (loopEnd != 0 && loopBegin < to && to <= loopEnd) {
				return true;
			}
		}

		return false;
	}
}
