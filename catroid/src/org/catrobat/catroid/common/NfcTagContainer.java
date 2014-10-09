/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.common;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.catrobat.catroid.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NfcTagContainer {
    private static final String TAG = NfcTagContainer.class.getSimpleName();

    private static ArrayAdapter<String> tagNameAdapter = null;
    private static List<String> tagNameList = new ArrayList<String>();
    private static Map<String, String> mapUidToTagName = new HashMap<String, String>();

    private NfcTagContainer() {
        throw new AssertionError();
    }

    //TODO: rename to getNfcAdapter/getTagAdapter
    public static ArrayAdapter<String> getMessageAdapter(Context context) {
        if (tagNameAdapter == null) {
            tagNameAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, tagNameList);
            //TODO: use .add() instead of .insert()
            tagNameAdapter.insert(context.getString(R.string.new_nfc_tag),0);
            tagNameAdapter.insert(context.getString(R.string.brick_when_nfc_default_all),1);

            tagNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        return tagNameAdapter;
    }


    public static void addTagName(String uid, String tagName) {
        if (tagName == null || tagName.isEmpty()) {
            return;
        }

        addTagName(tagName);

        Log.d(TAG,"adding" +uid + " - " + tagName);
        mapUidToTagName.put(uid, tagName);
    }

    //TODO: Rename
    public static int getPositionOfMessageInAdapter(Context context, String tagName) {
        if (tagNameAdapter == null) {
            getMessageAdapter(context);
        }
        return tagNameAdapter.getPosition(tagName);
    }

    public static String getNameForUid(String uid) {
        return mapUidToTagName.get(uid);
    }

    public static String getFirst(Context context) {
        return getMessageAdapter(context).getItem(1);
    }

    public static void addTagName(String tagName) {
        if (tagName == null || tagName.isEmpty()) {
            return;
        }

        if (!tagNameList.contains(tagName)) {
            tagNameList.add(tagName);
        }
    }

    //TODO: add removeUnmappedTags - see MessageContainer.java - removeUnusedMessages()
}
