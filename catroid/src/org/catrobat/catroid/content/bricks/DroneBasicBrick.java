package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.catrobat.catroid.R;

public abstract class DroneBasicBrick extends BrickBaseType {

    @Override
    public View getView(Context context, int brickId, BaseAdapter baseAdapter) {
        if (animationState) {
            return view;
        }
        if (view == null) {
            alphaValue = 255;
        }
        view = View.inflate(context, R.layout.brick_drone, null);
        view = getViewWithAlpha(alphaValue);

        setCheckboxView(R.id.brick_drone_checkbox);
        final Brick brickInstance = this;
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked = isChecked;
                adapter.handleCheck(brickInstance, isChecked);
            }
        });

        TextView label = (TextView) view.findViewById(R.id.brick_drone_label);
        label.setText(getBrickLabel(view));

        return view;
    }

    @Override
    public View getPrototypeView(Context context) {
        View prototypeView = View.inflate(context, R.layout.brick_drone, null);

        TextView label = (TextView) prototypeView.findViewById(R.id.brick_drone_label);
        label.setText(getBrickLabel(prototypeView));

        return prototypeView;
    }

    @Override
    public View getViewWithAlpha(int alphaValue) {
        if (view != null) {
            View layout = view.findViewById(R.id.brick_drone_layout);
            Drawable background = layout.getBackground();
            background.setAlpha(alphaValue);
            this.alphaValue = (alphaValue);

            TextView label = (TextView) view.findViewById(R.id.brick_drone_label);
            label.setText(getBrickLabel(view));
        }

        return view;
    }

    @Override
    public int getRequiredResources() {
        return super.getRequiredResources() | Brick.ARDRONE_SUPPORT;
    }

    protected abstract String getBrickLabel(View view);
}
