package org.catrobat.catroid.FaceRecognizer;

import android.content.ContentResolver;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import org.catrobat.catroid.FaceRecognizer.env.FileUtils;
import org.catrobat.catroid.FaceRecognizer.ml.BlazeFace;
import org.catrobat.catroid.FaceRecognizer.ml.FaceNet;
import org.catrobat.catroid.FaceRecognizer.ml.LibSVM;

import java.io.FileDescriptor;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Recognizer {
    public static final String TAG = "Recognizer";

    public class Recognition {
        private final String id;         // A unique identifier for the recognized item
        private final String title;      // Display name for the recognition
        private final Float confidence;  // Confidence score for recognition
        private RectF location;          // Location within the image

        Recognition(final String id, final String title, final Float confidence, final RectF location) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
            this.location = location;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Float getConfidence() {
            return confidence;
        }

        public RectF getLocation() {
            return new RectF(location);
        }

        @Override
        public String toString() {
            String resultString = "";
            if (id != null) resultString += "[" + id + "] ";
            if (title != null) resultString += title + " ";
            if (confidence != null) resultString += String.format("(%.1f%%) ", confidence * 100.0f);
            if (location != null) resultString += location + " ";
            return resultString.trim();
        }
    }

    private static Recognizer recognizer;

    private BlazeFace blazeFace;
    private FaceNet faceNet;
    private LibSVM svm;

    private List<String> classNames;

    private Recognizer() {}

    public static Recognizer getInstance(AssetManager assetManager) throws Exception {
        if (recognizer != null) return recognizer;

        recognizer = new Recognizer();
        recognizer.blazeFace = BlazeFace.create(assetManager);
        recognizer.faceNet = FaceNet.create(assetManager);
        recognizer.svm = LibSVM.getInstance();

        recognizer.classNames = FileUtils.readLabel(FileUtils.LABEL_FILE);
        if (recognizer.classNames == null) {
            recognizer.classNames = new ArrayList<>();
            Log.e(TAG, "classNames is null. Initialized as empty list.");
        }

        return recognizer;
    }

    public CharSequence[] getClassNames() {
        if (classNames == null) {
            Log.e(TAG, "classNames is null in getClassNames. Returning default value.");
            return new CharSequence[]{"+ add new person"};
        }

        CharSequence[] cs = new CharSequence[classNames.size() + 1];
        int idx = 1;

        cs[0] = "+ add new person";
        for (String name : classNames) {
            cs[idx++] = name;
        }

        return cs;
    }

    public List<Recognition> recognizeImage(Bitmap bitmap, Matrix matrix) {
        synchronized (this) {
            List<RectF> faces = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                faces = blazeFace.detect(bitmap);
            }
            final List<Recognition> mappedRecognitions = new LinkedList<>();

            for (RectF rectF : faces) {
                Rect rect = new Rect();
                rectF.round(rect);

                FloatBuffer buffer = faceNet.getEmbeddings(bitmap, rect);
                LibSVM.Prediction prediction = svm.predict(buffer);

                matrix.mapRect(rectF);
                int index = prediction.getIndex();

                String name = index < classNames.size() ? classNames.get(index) : "Unknown";
                Recognition result =
                        new Recognition("" + index, name, prediction.getProb(), rectF);
                mappedRecognitions.add(result);
            }
            return mappedRecognitions;
        }
    }

    public void updateData(int label, ContentResolver contentResolver, ArrayList<Uri> uris) throws Exception {
        synchronized (this) {
            ArrayList<float[]> list = new ArrayList<>();

            for (Uri uri : uris) {
                Bitmap bitmap = getBitmapFromUri(contentResolver, uri);
                List<RectF> faces = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    faces = blazeFace.detect(bitmap);
                }

                Rect rect = new Rect();
                if (!faces.isEmpty()) {
                    faces.get(0).round(rect);
                }

                float[] emb_array = new float[FaceNet.EMBEDDING_SIZE];
                faceNet.getEmbeddings(bitmap, rect).get(emb_array);
                list.add(emb_array);
            }

            svm.train(label, list);
        }
    }

    public int addPerson(String name) {
        FileUtils.appendText(name, FileUtils.LABEL_FILE);
        if (classNames == null) {
            classNames = new ArrayList<>();
        }
        classNames.add(name);

        return classNames.size();
    }
    public void deletePerson(int index) {
        if (index >= 0 && index < classNames.size()) {
            classNames.remove(index);
            FileUtils.rewriteLabelFile(new ArrayList<>(classNames));
            // Optional: reset/retrain SVM model if needed
        }
    }

    private Bitmap getBitmapFromUri(ContentResolver contentResolver, Uri uri) throws Exception {
        ParcelFileDescriptor parcelFileDescriptor =
                contentResolver.openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        return bitmap;
    }
    public void close() {
        blazeFace.close();
        faceNet.close();
    }
}
