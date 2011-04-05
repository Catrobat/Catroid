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

package at.tugraz.ist.catroid.ui.dialogs.brickdialogs;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import at.tugraz.ist.catroid.R;

public class EditDoubleDialog extends EditDialog implements OnClickListener {
    private double value;

    public EditDoubleDialog(Context context, EditText referencedEditText, double value) {
        super(context, referencedEditText);
        this.value = value;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editText.setText(String.valueOf(value));
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editText.setSelection((int) (Math.log10(value) + 1.0));

        Button closeButton = (Button) findViewById(R.id.dialogEditTextSubmit);
        closeButton.setOnClickListener(this);
    }

    public double getValue() {
        return value;
    }

    public void onClick(View v) {
        if (v.getId() == referencedEditText.getId()) {
            show();
        } else {
            value = Double.parseDouble((editText.getText().toString()));
            dismiss();
        }
    }
}
