package org.catrobat.catroid.uiespresso.facerecognizer

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import org.catrobat.catroid.content.actions.FaceNameTrainAction

/** Minimal window used to exercise the real face-training dialogs. */
class TestHostActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(FrameLayout(this))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (FaceNameTrainAction.ownsRequestCode(requestCode)) {
            FaceNameTrainAction.currentInstance?.handleResult(requestCode, resultCode, data)
        }
    }
}
