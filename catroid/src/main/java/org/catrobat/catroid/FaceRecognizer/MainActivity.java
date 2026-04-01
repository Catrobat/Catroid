package org.catrobat.catroid.FaceRecognizer;

import android.content.ClipData;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import org.catrobat.catroid.FaceRecognizer.env.FileUtils;
import org.catrobat.catroid.FaceRecognizer.env.Logger;
import org.catrobat.catroid.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final Logger LOGGER = new Logger();

    private Recognizer recognizer;
    private Snackbar initSnackbar;
    private boolean initialized = false, training = false;
    private AlertDialog dialog; // interactive popup

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileUtils.initializeRoot(this);

        initSnackbar = Snackbar.make(
                getWindow().getDecorView().findViewById(android.R.id.content),
                "Initializing...", Snackbar.LENGTH_INDEFINITE);

        initRecognizer();

        new Thread(() -> {
            while (!initialized) {
                try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            }
            runOnUiThread(this::showInteractivePopup);
        }).start();
    }

    private void initRecognizer() {
        new Thread(() -> {
            runOnUiThread(initSnackbar::show);

            // Ensure ROOT exists
            File rootDir = new File(FileUtils.ROOT);
            if (!rootDir.exists() && !rootDir.mkdirs()) {
                showError("Failed to create directory", "Directory creation failed");
                return;
            }

            AssetManager assets = getAssets();

            // Copy assets ONLY IF MISSING — never overwrite an existing label
            FileUtils.copyAssetIfMissing(assets, FileUtils.DATA_FILE);
            FileUtils.copyAssetIfMissing(assets, FileUtils.MODEL_FILE);
            FileUtils.copyAssetIfMissing(assets, FileUtils.LABEL_FILE);

            logLabel("onInit-beforeRecognizer");

            try {
                recognizer = Recognizer.getInstance(assets);

                // Sync DISK → Recognizer so new names appear immediately at runtime
                ArrayList<String> diskNames = FileUtils.readLabel(FileUtils.LABEL_FILE);
                CharSequence[] existing = recognizer.getClassNames();
                for (String nm : diskNames) {
                    if (nm == null || nm.trim().isEmpty()) continue;
                    if (!containsIgnoreCase(existing, nm)) {
                        int idx = recognizer.addPerson(nm.trim());
                        LOGGER.i("sync: added to recognizer \"" + nm + "\" @ " + idx);
                    }
                }

                initialized = true;
            } catch (Exception e) {
                LOGGER.e("Recognizer init failed", e);
                runOnUiThread(() -> {
                    initSnackbar.dismiss();
                    Snackbar.make(getWindow().getDecorView(), "Recognizer failed", Snackbar.LENGTH_SHORT).show();
                    finish();
                });
                return;
            }

            runOnUiThread(initSnackbar::dismiss);
        }).start();
    }

    private void showInteractivePopup() {
        if (!initialized) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(60, 40, 60, 40);

        TextView title = new TextView(this);
        title.setText("Select or Add Person");
        title.setTextSize(18);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 0, 0, 20);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        root.addView(title);

        ScrollView scrollView = new ScrollView(this);
        LinearLayout nameList = new LinearLayout(this);
        nameList.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(nameList);
        root.addView(scrollView);

        // SOURCE OF TRUTH: DISK label file
        ArrayList<String> names = FileUtils.readLabel(FileUtils.LABEL_FILE);

        // If empty, fall back to recognizer so user sees something
        if (names.isEmpty()) {
            CharSequence[] cs = recognizer.getClassNames();
            if (cs != null) for (int i = 0; i < cs.length; i++) {
                String nm = String.valueOf(cs[i]);
                if (i == 0 && "unknown".equalsIgnoreCase(nm)) continue;
                names.add(nm);
            }
        }

        for (String personName : names) {
            if (personName == null || personName.trim().isEmpty()) continue;

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(0, 8, 0, 8);
            row.setGravity(Gravity.CENTER_VERTICAL);

            TextView tv = new TextView(this);
            tv.setText(personName);
            tv.setTextColor(Color.DKGRAY);
            tv.setTextSize(16);
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            tv.setLayoutParams(tvParams);
            row.addView(tv);

            Button trainBtn = new Button(this);
            trainBtn.setAllCaps(false);
            trainBtn.setText("Train");
            trainBtn.setTextColor(Color.DKGRAY);
            trainBtn.setBackgroundColor(Color.LTGRAY);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = 12;
            trainBtn.setLayoutParams(lp);
            trainBtn.setOnClickListener(v -> {
                if (dialog != null) dialog.dismiss();
                int classIdx = ensureInRecognizer(personName);
                if (classIdx >= 1) performFileSearch(classIdx - 1);
                else Toast.makeText(this, "Could not map person", Toast.LENGTH_SHORT).show();
            });
            row.addView(trainBtn);

            ImageButton deleteBtn = new ImageButton(this);
            deleteBtn.setImageResource(android.R.drawable.ic_menu_delete);
            deleteBtn.setBackgroundColor(Color.TRANSPARENT);
            deleteBtn.setOnClickListener(v -> confirmDelete(personName));
            deleteBtn.setOnLongClickListener(v -> { confirmDelete(personName); return true; });
            row.addView(deleteBtn);

            row.setOnLongClickListener(v -> { confirmDelete(personName); return true; });

            nameList.addView(row);
        }

        Button addNewButton = new Button(this);
        addNewButton.setText("+ Add New");
        addNewButton.setBackgroundColor(Color.parseColor("#2196F3"));
        addNewButton.setTextColor(Color.WHITE);
        addNewButton.setAllCaps(false);
        addNewButton.setPadding(20, 20, 20, 20);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = 40;
        addNewButton.setLayoutParams(params);
        addNewButton.setOnClickListener(v -> {
            if (dialog != null) dialog.dismiss();
            showNewNameDialog();
        });

        root.addView(addNewButton);
        builder.setView(root);
        dialog = builder.create();
        dialog.show();
    }

    private void showNewNameDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter new name");

        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(60, 40, 60, 20);
        layout.addView(input);

        AlertDialog dlg = new AlertDialog.Builder(this)
                .setTitle("New Person")
                .setView(layout)
                .setPositiveButton("Add", (d, i) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        addPersonAndPersist(name);
                    } else {
                        Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dlg.show();
    }

    private void confirmDelete(String personName) {
        if (training) {
            Toast.makeText(this, "Training in progress. Try again later.", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Delete Person")
                .setMessage("Delete \"" + personName + "\" from label file?")
                .setPositiveButton("Delete", (d, w) -> deletePersonAndPersist(personName))
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---- Persist + Recognizer ------------------------------------------------

    private void addPersonAndPersist(String name) {
        new Thread(() -> {
            try {
                ArrayList<String> labels = FileUtils.readLabel(FileUtils.LABEL_FILE);
                if (labels.isEmpty()) labels.add("unknown"); // keep index 0 if needed

                for (String s : labels) {
                    if (s.equalsIgnoreCase(name)) {
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Already exists: " + name, Toast.LENGTH_SHORT).show();
                            showInteractivePopup();
                        });
                        return;
                    }
                }

                labels.add(name);
                FileUtils.rewriteLabelFile(labels);
                logLabel("after-add");

                int idx = ensureInRecognizer(name);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Added: " + name, Toast.LENGTH_SHORT).show();
                    showInteractivePopup();
                    if (idx >= 1) performFileSearch(idx - 1);
                });

            } catch (Exception e) {
                LOGGER.e("addPersonAndPersist failed", e);
                runOnUiThread(() ->
                        Snackbar.make(getWindow().getDecorView(), "Failed to add person", Snackbar.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void deletePersonAndPersist(String personName) {
        new Thread(() -> {
            try {
                ArrayList<String> labels = FileUtils.readLabel(FileUtils.LABEL_FILE);
                int idx = indexOfIgnoreCase(labels, personName);
                if (idx < 0) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Name not found.", Toast.LENGTH_SHORT).show());
                    return;
                }
                if (idx == 0 && "unknown".equalsIgnoreCase(labels.get(0))) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Cannot delete 'unknown'.", Toast.LENGTH_SHORT).show());
                    return;
                }

                labels.remove(idx);
                FileUtils.rewriteLabelFile(labels);
                logLabel("after-delete");

                runOnUiThread(() -> {
                    Toast.makeText(this, "Deleted: " + personName, Toast.LENGTH_SHORT).show();
                    showInteractivePopup();
                });
            } catch (Exception e) {
                LOGGER.e("deletePersonAndPersist failed", e);
                runOnUiThread(() ->
                        Snackbar.make(getWindow().getDecorView(), "Failed to delete", Snackbar.LENGTH_SHORT).show());
            }
        }).start();
    }

    /** Ensure name exists in recognizer; return class index or -1 on failure. */
    private int ensureInRecognizer(String name) {
        try {
            CharSequence[] cs = recognizer.getClassNames();
            for (int i = 0; i < (cs == null ? 0 : cs.length); i++) {
                if (name.equalsIgnoreCase(String.valueOf(cs[i]))) return i;
            }
            return recognizer.addPerson(name);
        } catch (Exception e) {
            LOGGER.e("ensureInRecognizer failed for " + name, e);
            return -1;
        }
    }

    private boolean containsIgnoreCase(CharSequence[] arr, String target) {
        if (arr == null) return false;
        for (CharSequence c : arr) if (target.equalsIgnoreCase(String.valueOf(c))) return true;
        return false;
    }

    // ---- Picker & Training ---------------------------------------------------

    public void performFileSearch(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!initialized || resultCode != RESULT_OK || data == null) {
            Snackbar.make(getWindow().getDecorView(), "Try again later", Snackbar.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Uri> uris = new ArrayList<>();
        ClipData clipData = data.getClipData();
        if (clipData != null) {
            for (int i = 0; i < clipData.getItemCount(); i++) {
                uris.add(clipData.getItemAt(i).getUri());
            }
        } else if (data.getData() != null) {
            uris.add(data.getData());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Training in Progress");
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        LinearLayout layout = new LinearLayout(this);
        layout.setPadding(60, 40, 60, 20);
        layout.setGravity(Gravity.CENTER);
        layout.addView(progressBar);
        builder.setView(layout).setCancelable(false);
        AlertDialog trainingDialog = builder.create();
        trainingDialog.show();

        training = true;

        new Thread(() -> {
            try {
                recognizer.updateData(requestCode, getContentResolver(), uris);
            } catch (Exception e) {
                LOGGER.e("Training exception", e);
            } finally {
                training = false;
                runOnUiThread(() -> {
                    trainingDialog.dismiss();
                    Toast.makeText(this, "Training complete!", Toast.LENGTH_SHORT).show();
                    if (dialog != null && dialog.isShowing()) dialog.dismiss();
                    showInteractivePopup();
                });
            }
        }).start();
    }

    // ---- Small helpers -------------------------------------------------------

    private int indexOfIgnoreCase(ArrayList<String> list, String target) {
        for (int i = 0; i < list.size(); i++) if (target.equalsIgnoreCase(list.get(i))) return i;
        return -1;
    }

    private void showError(String log, String userMsg) {
        LOGGER.e(log);
        runOnUiThread(() -> {
            if (initSnackbar != null) initSnackbar.dismiss();
            Snackbar.make(getWindow().getDecorView(), userMsg, Snackbar.LENGTH_SHORT).show();
        });
    }

    private void logLabel(String where) {
        try {
            File f = new File(FileUtils.ROOT, FileUtils.LABEL_FILE);
            String path = f.getAbsolutePath();
            String content;
            if (f.exists()) {
                try (FileInputStream fis = new FileInputStream(f)) {
                    byte[] b = new byte[(int) Math.min(f.length(), 4096)];
                    int n = fis.read(b);
                    content = new String(b, 0, Math.max(0, n), StandardCharsets.UTF_8);
                }
            } else content = "(missing)";
            LOGGER.i("[" + where + "] label path=" + path + " exists=" + f.exists() + " size=" + f.length() + " sample=" + content);
        } catch (Exception ignored) {}
    }
}
