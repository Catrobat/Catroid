package org.catrobat.catroid.content.bricks;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.catrobat.catroid.R;

import java.util.ArrayList;

public abstract class DroneSpinnerBrick extends BrickBaseType {

    protected transient AdapterView<?> adapterView;
    protected String selectedMessage;
    protected int spinnerPosition = 0;

    @Override
    public View getView(final Context context, int brickId, BaseAdapter baseAdapter) {

        if (animationState) {
            return view;
        }
        if (view == null) {
            alphaValue = 255;
        }

        view = View.inflate(context, R.layout.brick_drone_spinner, null);
        setCheckboxView(R.id.brick_drone_spinner_checkbox);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checked = isChecked;
                adapter.handleCheck(DroneSpinnerBrick.this, isChecked);
            }
        });

        Spinner spinner = (Spinner) view.findViewById(R.id.brick_drone_spinner_ID);
        spinner.setFocusableInTouchMode(false);
        spinner.setFocusable(false);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(view.getContext(),
                android.R.layout.simple_spinner_item, getSpinnerItems(view));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        if (checkbox.getVisibility() == View.VISIBLE) {
            spinner.setClickable(false);
            spinner.setEnabled(false);
        } else {
            spinner.setClickable(true);
            spinner.setEnabled(true);
        }

        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(spinnerPosition);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMessage = parent.getItemAtPosition(position).toString();
                spinnerPosition = position;
                adapterView = parent;
                Log.d("DroneSpinnerBrick: ", "selected message = " +
                        selectedMessage + " on position: " + spinnerPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        TextView label = (TextView) view.findViewById(R.id.brick_drone_spinner_label);
        label.setText(getBrickLabel(view));

        return view;
    }

    @Override
    public View getPrototypeView(Context context) {
        View prototypeView = View.inflate(context, R.layout.brick_drone_spinner, null);

        Spinner spinner = (Spinner) prototypeView.findViewById(R.id.brick_drone_spinner_ID);
        spinner.setFocusableInTouchMode(false);
        spinner.setFocusable(false);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(prototypeView.getContext(),
                android.R.layout.simple_spinner_item, getSpinnerItems(prototypeView));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(spinnerPosition);

        TextView label = (TextView) prototypeView.findViewById(R.id.brick_drone_spinner_label);
        label.setText(getBrickLabel(prototypeView));

        return prototypeView;
    }

    @Override
    public View getViewWithAlpha(int alphaValue) {

        if (view != null) {

            View layout = view.findViewById(R.id.brick_drone_spinner_layout);
            Drawable background = layout.getBackground();
            background.setAlpha(alphaValue);
            this.alphaValue = (alphaValue);

            TextView label = (TextView) view.findViewById(R.id.brick_drone_spinner_label);
            label.setText(getBrickLabel(view));
        }
        return view;
    }

    public void setSpinnerPosition(int spinnerPosition) {
        this.spinnerPosition = spinnerPosition;
    }

    @Override
    public int getRequiredResources() {
        return ARDRONE_SUPPORT;
    }

    protected abstract String getBrickLabel(View view);

    protected abstract ArrayList<String> getSpinnerItems(View view);
}
