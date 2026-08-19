package org.catrobat.catroid.FaceRecognizer;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.catrobat.catroid.FaceRecognizer.env.FileUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * On device face training and recognition.
 *
 * Training  : photo -> largest face -> FaceNet embedding -> stored in "data" under the
 *             person index, name stored in "label".
 * Detection : camera frame -> largest face -> FaceNet embedding -> cosine distance
 *             against every stored embedding -> name of the closest person.
 */
public class Recognizer {
    public static final String TAG = "Recognizer";

    /** Photos are downscaled to this longest side before detection, to bound memory. */
    /* FaceNet finally consumes only 160x160. 960 keeps ample face detail while
       cutting gallery bitmap/rotation memory by about 44% versus 1280. */
    private static final int MAX_ENROL_SIDE = 960;

    /** Reject pathological panoramas/broken metadata before allocating a bitmap. */
    private static final long MAX_SOURCE_PIXELS = 120_000_000L;

    /** Below this many photos, recognition is unreliable. */
    public static final int RECOMMENDED_PHOTOS = 5;
    /** A lone usable bracket frame must be exceptionally clear to identify anyone. */
    private static final float SINGLE_FRAME_MIN_SIMILARITY = 0.75f;

    public static class Result {
        public final int index;
        public final String name;
        public final float confidence;

        Result(int index, String name, float confidence) {
            this.index = index;
            this.name = name;
            this.confidence = confidence;
        }
    }

    private static Recognizer instance;
    /** Prevent two Select presses from leaving concurrent dataset jobs alive. */
    private static volatile EnrolTask activeEnrolmentTask;

    private FaceEmbedder embedder;
    private final FaceDatabase database = new FaceDatabase();

    private Recognizer() { }

    public static synchronized Recognizer getInstance(Context context) throws Exception {
        if (instance == null) {
            FileUtils.init(context);
            Recognizer r = new Recognizer();
            r.embedder = FaceEmbedder.create(context.getAssets());
            r.database.load();
            instance = r;
        }
        return instance;
    }

    // ---------------- People ----------------

    public synchronized float getMinSimilarity() {
        return FaceDatabase.minSimilarity;
    }

    public synchronized float getMinMargin() {
        return FaceDatabase.minMargin;
    }

    public synchronized void setThresholds(float similarity, float margin) {
        database.setThresholds(similarity, margin);
    }

    public synchronized List<String> getClassNames() {
        database.ensureFresh();
        return database.getNames();
    }

    public synchronized int getPhotoCount(int index) {
        return database.getEmbeddingCount(index);
    }

    public synchronized int addPerson(String name) {
        int existing = database.indexOf(name);
        if (existing >= 0) {
            return existing;
        }
        int index = database.addPerson(name);
        database.save();
        return index;
    }

    public synchronized void deletePerson(int index) {
        database.deletePerson(index);
        database.save();
    }

    // ---------------- Training ----------------

    /** Reports enrolment progress so the dialog can show a real progress bar. */
    public interface ProgressListener {
        void onPhoto(int done, int total);
    }

    /** Result of reading a batch of photos, including why each one failed. */
    public static class EnrolResult {
        public final List<float[]> embeddings = new ArrayList<>();
        public final List<String> report = new ArrayList<>();
    }

    /** Result callback for non-blocking gallery enrolment. Runs on the main thread. */
    public interface EnrolCallback {
        void onFinished(EnrolResult result);
    }

    /** Handle retained by the Activity so onStop() can terminate gallery work. */
    public static final class EnrolTask {
        private volatile boolean cancelled;
        private Thread worker;

        public void cancel() {
            cancelled = true;
            Thread t = worker;
            if (t != null) {
                t.interrupt();
            }
        }

        public boolean isCancelled() {
            return cancelled;
        }
    }

    /**
     * Gallery training without blocking Android's UI thread. Progress and completion
     * are delivered on the main thread. Keep the returned task and cancel it from
     * Activity/Fragment onStop().
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public EnrolTask extractEmbeddingsAsync(ContentResolver resolver, List<Uri> uris,
            ProgressListener listener, EnrolCallback callback) {
        final EnrolTask task = new EnrolTask();
        final Handler main = new Handler(Looper.getMainLooper());
        final List<Uri> safeUris = (uris == null)
                ? new ArrayList<>() : new ArrayList<>(uris);
        task.worker = new Thread(() -> {
            ProgressListener safeProgress = (done, total) -> {
                if (listener != null && !task.cancelled) {
                    main.post(() -> {
                        if (!task.cancelled) {
                            listener.onPhoto(done, total);
                        }
                    });
                }
            };
            EnrolResult result;
            try {
                result = extractEmbeddings(resolver, safeUris, safeProgress);
            } catch (Throwable t) {
                Log.e(TAG, "Background enrolment failed", t);
                result = new EnrolResult();
                result.report.add("Training failed: " + t.getClass().getSimpleName());
            }
            final EnrolResult deliveredResult = result;
            if (callback != null && !task.cancelled) {
                main.post(() -> {
                    if (!task.cancelled) {
                        callback.onFinished(deliveredResult);
                    }
                });
            }
        }, "face_enrolment");
        task.worker.start();
        return task;
    }

    /**
     * Cold-start-safe enrolment. Unlike calling getInstance() from an Activity
     * first, this loads BlazeFace, FaceNet and the database on the worker thread.
     * Use this method for the first and all later gallery training operations.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static EnrolTask startEnrolmentAsync(Context context,
            ContentResolver resolver, List<Uri> uris,
            ProgressListener listener, EnrolCallback callback) {
        final EnrolTask task = new EnrolTask();
        EnrolTask previous = activeEnrolmentTask;
        if (previous != null) {
            previous.cancel();
        }
        activeEnrolmentTask = task;
        final Handler main = new Handler(Looper.getMainLooper());
        final Context appContext = context == null ? null : context.getApplicationContext();
        final List<Uri> safeUris = uris == null
                ? new ArrayList<>() : new ArrayList<>(uris);
        task.worker = new Thread(() -> {
            EnrolResult result;
            try {
                if (appContext == null) {
                    throw new IllegalArgumentException("Context is null");
                }
                Recognizer recognizer = Recognizer.getInstance(appContext);
                ProgressListener safeProgress = (done, total) -> {
                    if (listener != null && !task.cancelled) {
                        main.post(() -> {
                            if (!task.cancelled) {
                                listener.onPhoto(done, total);
                            }
                        });
                    }
                };
                result = recognizer.extractEmbeddings(resolver, safeUris, safeProgress);
            } catch (Throwable t) {
                Log.e(TAG, "Cold-start background enrolment failed", t);
                result = new EnrolResult();
                result.report.add("Training failed: " + t.getClass().getSimpleName());
            }
            final EnrolResult delivered = result;
            if (activeEnrolmentTask == task) {
                activeEnrolmentTask = null;
            }
            if (callback != null && !task.cancelled) {
                main.post(() -> {
                    if (!task.cancelled) {
                        callback.onFinished(delivered);
                    }
                });
            }
        }, "face_enrolment_cold_start");
        task.worker.start();
        return task;
    }

    /**
     * Reads the picked photos and returns one embedding per photo that contained a
     * usable face, plus a line per photo explaining what happened.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public synchronized EnrolResult extractEmbeddings(ContentResolver resolver, List<Uri> uris) {
        return extractEmbeddings(resolver, uris, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public synchronized EnrolResult extractEmbeddings(ContentResolver resolver,
            List<Uri> uris, ProgressListener listener) {
        EnrolResult result = new EnrolResult();
        if (resolver == null || uris == null) {
            return result;
        }

        int photo = 0;
        for (Uri uri : uris) {
            if (Thread.currentThread().isInterrupted()) {
                result.report.add("Training cancelled");
                break;
            }
            photo++;
            if (listener != null) {
                listener.onPhoto(photo, uris.size());
            }
            Bitmap bitmap = decodeScaled(resolver, uri, MAX_ENROL_SIDE);
            if (bitmap == null) {
                result.report.add(photo + ": " + lastDecodeProblem);
                Log.w(TAG, "Could not decode " + uri);
                continue;
            }

            try {
                FaceEmbedder.Face face = embedder.findBestFace(bitmap);
                if (face == null) {
                    result.report.add(photo + ": " + embedder.lastProblem
                            + " (" + bitmap.getWidth() + "x" + bitmap.getHeight() + ")");
                    continue;
                }

                List<float[]> variants;
                try {
                    // Several crop tightnesses and their mirrors, all stored. A live
                    // capture only has to be close to one of them.
                    variants = embedder.embedVariants(face.frame, face.box, true,
                            face.leftEye, face.rightEye);
                } finally {
                    face.release(bitmap);
                }

                if (variants.isEmpty()) {
                    result.report.add(photo + ": " + embedder.lastProblem);
                } else {
                    // A variant almost identical to one already stored can never win
                    // a nearest neighbour comparison the other would not, so it is
                    // pure cost. Dropping them shrinks the data file by about a third.
                    int kept = 0;
                    for (float[] candidate : variants) {
                        if (!tooSimilarToStored(result.embeddings, candidate)) {
                            result.embeddings.add(candidate);
                            kept++;
                        }
                    }
                    variants = result.embeddings.subList(
                            result.embeddings.size() - kept, result.embeddings.size());
                    result.report.add(photo + ": OK, face "
                            + face.box.width() + "x" + face.box.height() + " px, "
                            + variants.size() + " views");
                }
            } catch (Throwable t) {
                // One bad photo must never kill the app, especially OutOfMemoryError.
                Log.e(TAG, "Photo " + photo + " failed", t);
                result.report.add(photo + ": " + t.getClass().getSimpleName());
            } finally {
                bitmap.recycle();
            }
        }
        Log.i(TAG, "Enrolment report: " + result.report);
        return result;
    }

    /**
     * Embeds the largest face in a live camera frame, for camera based enrolment.
     * Uses exactly the same crop and normalisation as detection, which is the whole
     * point: a photo enrolled this way and a photo detected later differ only by
     * the person, not by the camera or the pipeline.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public synchronized List<float[]> embedFrame(Bitmap frame) {
        if (frame == null || frame.isRecycled()) {
            return new ArrayList<>();
        }
        return embedder.embedAllVariants(frame, true);
    }

    public synchronized String getEmbedderProblem() {
        return embedder == null ? "" : embedder.lastProblem;
    }

    private static final float DUPLICATE_THRESHOLD = 0.995f;

    private static boolean tooSimilarToStored(List<float[]> stored, float[] candidate) {
        for (float[] existing : stored) {
            float dot = 0f;
            for (int i = 0; i < existing.length && i < candidate.length; i++) {
                dot += existing[i] * candidate[i];
            }
            if (dot > DUPLICATE_THRESHOLD) {
                return true;
            }
        }
        return false;
    }

    /** Stores the embeddings under an existing person and writes both files. */
    public synchronized int addEmbeddings(int index, List<float[]> embeddings) {
        database.addEmbeddings(index, embeddings);
        database.save();
        return database.getEmbeddingCount(index);
    }

    // ---------------- Detection ----------------

    /**
     * Recognises the largest face in the frame. Returns null when no face is found or
     * when no enrolled person is close enough.
     *
     * @param mirrorToo also try the mirrored face, needed for front camera frames
     */
    /**
     * Accumulates scores across several camera frames. One frame can be blurry,
     * badly exposed or caught mid blink. Averaging over a few frames removes most
     * of that noise and is the largest single accuracy gain available here.
     */
    public static class Session {
        private float[] totals;
        private int framesWithFace;
        private int framesTried;

        public int getFramesWithFace() {
            return framesWithFace;
        }

        public int getFramesTried() {
            return framesTried;
        }
    }

    /** Plain text summary of the last finished session. Shown to the user. */
    private String lastSummary = "";

    public synchronized String getLastSummary() {
        return lastSummary;
    }

    public synchronized Session newSession() {
        database.ensureFresh();
        return new Session();
    }

    /**
     * Scores one frame into the session. Returns true if a face was found and used.
     * The frame is not modified and is not recycled.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public synchronized boolean addFrame(Session session, Bitmap frame, boolean mirrorToo) {
        if (session == null || frame == null || frame.isRecycled()) {
            return false;
        }
        session.framesTried++;

        FaceEmbedder.Face face = embedder.findBestFace(frame);
        if (face == null) {
            Log.i(TAG, "Frame " + session.framesTried + ": " + embedder.lastProblem);
            return false;
        }
        // Reject tiny/background detections.
        int shortestSide = Math.min(face.frame.getWidth(), face.frame.getHeight());
        float faceFraction = (float) Math.min(face.box.width(), face.box.height())
                / (float) shortestSide;

        if (faceFraction < 0.12f) {
            Log.i(TAG, "Frame " + session.framesTried
                    + " rejected: detected face is too small, fraction=" + faceFraction);
            face.release(frame);
            return false;
        }

        String qualityProblem = faceQualityProblem(face.frame, face.box);
        if (qualityProblem != null) {
            Log.i(TAG, "Frame " + session.framesTried + " rejected: " + qualityProblem);
            face.release(frame);
            return false;
        }

        float[] bestScores;
        int variantCount;
        try {
            // Test time augmentation. The captured face is embedded at several crop
            // tightnesses and mirrored, and each is scored. The best fit wins, which
            // absorbs framing differences between the gallery photos and the camera.
            List<float[]> variants = embedder.embedVariants(face.frame, face.box, true,
                    face.leftEye, face.rightEye);
            variantCount = variants.size();
            bestScores = database.scoreAllVariants(variants);
        } finally {
            face.release(frame);
        }

        if (bestScores == null) {
            return false;
        }

        Log.i(TAG, "Frame " + session.framesTried + " face "
                + face.box.width() + "x" + face.box.height()
                + " (" + variantCount + " views): "
                + database.describeScoreArray(bestScores));

        if (session.totals == null) {
            session.totals = new float[bestScores.length];
        }
        if (session.totals.length != bestScores.length) {
            // Training changed under us. Start the tally again.
            session.totals = new float[bestScores.length];
            session.framesWithFace = 0;
        }
        for (int i = 0; i < bestScores.length; i++) {
            session.totals[i] += bestScores[i];
        }
        session.framesWithFace++;
        return true;
    }

    /** Rejects silhouette/clipped crops before they can collapse to a false identity. */
    private static String faceQualityProblem(Bitmap bitmap, Rect box) {
        if (bitmap == null || box == null || box.width() <= 0 || box.height() <= 0) {
            return "invalid face crop";
        }
        int step = Math.max(1, Math.min(box.width(), box.height()) / 80);
        long sum = 0L;
        long sumSquares = 0L;
        int dark = 0;
        int bright = 0;
        int count = 0;
        for (int y = box.top; y < box.bottom; y += step) {
            for (int x = box.left; x < box.right; x += step) {
                int p = bitmap.getPixel(x, y);
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;
                int luma = (77 * r + 150 * g + 29 * b) >> 8;
                sum += luma;
                sumSquares += (long) luma * luma;
                if (luma < 20) dark++;
                if (luma > 245) bright++;
                count++;
            }
        }
        if (count == 0) return "empty face crop";
        float mean = (float) sum / count;
        float variance = Math.max(0f, (float) sumSquares / count - mean * mean);
        float deviation = (float) Math.sqrt(variance);
        float darkRatio = (float) dark / count;
        float brightRatio = (float) bright / count;
        if (mean < 30f || darkRatio > 0.65f) {
            return String.format(java.util.Locale.US,
                    "face is a dark silhouette (mean %.1f, dark %.0f%%)",
                    mean, darkRatio * 100f);
        }
        if (mean > 230f || brightRatio > 0.60f) {
            return String.format(java.util.Locale.US,
                    "face highlights are clipped (mean %.1f, bright %.0f%%)",
                    mean, brightRatio * 100f);
        }
        if (deviation < 18f) {
            return String.format(java.util.Locale.US,
                    "face has too little visible detail (contrast %.1f)", deviation);
        }
        return null;
    }

    /**
     * Reads the session so far without ending it. Lets a capture stop early when
     * the first frame is already a clear match, which usually halves the time.
     */
    public synchronized Result peekSession(Session session) {
        if (session == null || session.getFramesWithFace() == 2) {
            return null;
        }
        return finishSession(session);
    }

    /** Averages the session and applies the thresholds once. Null means Unknown. */
    /** True when the stored photos were made by a different embedding pipeline. */
    public synchronized boolean needsRetraining() {
        database.ensureFresh();
        return database.hasStaleEmbeddings();
    }

    public synchronized Result finishSession(Session session) {
        if (session == null || session.totals == null || session.framesWithFace == 0) {
            int tried = (session == null) ? 0 : session.framesTried;
            lastSummary = "No face found in " + tried + " frame(s)";
            Log.i(TAG, lastSummary);
            return null;
        }

        float[] average = new float[session.totals.length];
        for (int i = 0; i < average.length; i++) {
            average[i] = session.totals[i] / session.framesWithFace;
        }
        Log.i(TAG, "Session average over " + session.framesWithFace + " frame(s): "
                + database.describeScoreArray(average));

        if (session.framesWithFace == 1) {
            float bestSingle = -1f;
            for (float score : average) {
                bestSingle = Math.max(bestSingle, score);
            }
            if (bestSingle < SINGLE_FRAME_MIN_SIMILARITY) {
                lastSummary = String.format(java.util.Locale.US,
                        "REJECTED: only one usable lighting frame, best %.3f; need %.2f",
                        bestSingle, SINGLE_FRAME_MIN_SIMILARITY);
                Log.i(TAG, lastSummary);
                return null;
            }
        }

        lastSummary = database.describeScoreArray(average)
                + "\nfrom " + session.framesWithFace + " frame(s)"
                + "\nneed " + FaceDatabase.minSimilarity
                + ", gap " + FaceDatabase.minMargin;

        FaceDatabase.Match match = database.decide(average);
        if (match == null) {
            lastSummary = "REJECTED\n" + lastSummary;
            if (database.hasStaleEmbeddings()) {
                lastSummary = "PHOTOS ARE OUT OF DATE, RETRAIN\n" + lastSummary;
                Log.e(TAG, "Rejected, and the stored photos were made by an older "
                        + "pipeline. Delete each person and add their photos again.");
            }
            return null;
        }
        lastSummary = match.name + "\n" + lastSummary;
        Log.i(TAG, "Match " + match.name + " similarity " + match.similarity
                + " margin " + match.margin);
        return new Result(match.index, match.name, match.similarity);
    }

    private static float[] higherOf(float[] a, float[] b) {
        if (a == null) {
            return b;
        }
        if (b == null || b.length != a.length) {
            return a;
        }
        for (int i = 0; i < a.length; i++) {
            if (b[i] > a[i]) {
                a[i] = b[i];
            }
        }
        return a;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public synchronized Result recognize(Bitmap frame, boolean mirrorToo) {
        if (frame == null || frame.isRecycled()) {
            return null;
        }
        database.ensureFresh();

        FaceEmbedder.Face face = embedder.findBestFace(frame);
        if (face == null) {
            Log.i(TAG, "No face in frame");
            return null;
        }

        FaceDatabase.Match best = null;
        try {
            float[] embedding = embedder.embed(face.frame, face.box);
            if (embedding != null) {
                Log.d(TAG, "scores " + database.describeScores(embedding));
                best = database.match(embedding);
            }

            if (mirrorToo) {
                Bitmap mirrored = FaceEmbedder.mirror(face.frame);
                try {
                    Rect box = FaceEmbedder.mirrorRect(face.box, face.frame.getWidth());
                    float[] mirroredEmbedding = embedder.embed(mirrored, box);
                    if (mirroredEmbedding != null) {
                        FaceDatabase.Match other = database.match(mirroredEmbedding);
                        if (other != null && (best == null || other.similarity > best.similarity)) {
                            best = other;
                        }
                    }
                } finally {
                    mirrored.recycle();
                }
            }
        } finally {
            face.release(frame);
        }

        if (best == null) {
            return null;
        }
        Log.i(TAG, "Match " + best.name + " similarity " + best.similarity + " margin " + best.margin);
        return new Result(best.index, best.name, best.similarity);
    }

    // ---------------- Helpers ----------------

    /** Why the last decode failed. Surfaced in the per photo report. */
    private String lastDecodeProblem = "";

    private Bitmap decodeScaled(ContentResolver resolver, Uri uri, int maxSide) {
        lastDecodeProblem = "";
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;

        Bitmap decoded = null;
        try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "r")) {
            if (pfd == null) {
                lastDecodeProblem = "file descriptor was null";
                return null;
            }
            BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, bounds);
        } catch (SecurityException e) {
            // Happens when the activity that opened the picker was destroyed and the
            // read grant went with it.
            lastDecodeProblem = "no permission to read this photo (SecurityException)";
            Log.e(TAG, "Lost URI permission for " + uri, e);
            return null;
        } catch (OutOfMemoryError oom) {
            lastDecodeProblem = "not enough memory to inspect this photo";
            Log.e(TAG, "Out of memory reading image bounds " + uri, oom);
            return null;
        } catch (Throwable e) {
            lastDecodeProblem = "could not open: " + e.getClass().getSimpleName();
            Log.e(TAG, "Could not read bounds of " + uri, e);
            return null;
        }

        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            lastDecodeProblem = "unsupported, cloud-only, or damaged image";
            return null;
        }
        long sourcePixels = (long) bounds.outWidth * bounds.outHeight;
        if (sourcePixels > MAX_SOURCE_PIXELS) {
            lastDecodeProblem = "image dimensions are too large: "
                    + bounds.outWidth + "x" + bounds.outHeight;
            return null;
        }

        int sample = 1;
        int longest = Math.max(bounds.outWidth, bounds.outHeight);
        /* Power-of-two sampling is supported consistently by older BitmapFactory. */
        while ((long) longest / sample > maxSide && sample <= 1024) {
            sample *= 2;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sample;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inScaled = false;
        options.inDither = false;

        try (ParcelFileDescriptor pfd = resolver.openFileDescriptor(uri, "r")) {
            if (pfd == null) {
                return null;
            }
            decoded = BitmapFactory.decodeFileDescriptor(
                    pfd.getFileDescriptor(), null, options);
            if (decoded == null) {
                lastDecodeProblem = "decoder returned no bitmap";
                return null;
            }
            /* Some vendor decoders ignore inSampleSize for uncommon formats. */
            int decodedLongest = Math.max(decoded.getWidth(), decoded.getHeight());
            if (decodedLongest > maxSide * 2) {
                float scale = (float) maxSide / decodedLongest;
                int width = Math.max(1, Math.round(decoded.getWidth() * scale));
                int height = Math.max(1, Math.round(decoded.getHeight() * scale));
                Bitmap reduced = Bitmap.createScaledBitmap(decoded, width, height, true);
                if (reduced != decoded) {
                    decoded.recycle();
                }
                decoded = reduced;
            }
            return decoded;
        } catch (OutOfMemoryError oom) {
            if (decoded != null && !decoded.isRecycled()) {
                decoded.recycle();
            }
            lastDecodeProblem = "image skipped: not enough memory";
            Log.e(TAG, "Out of memory decoding " + uri, oom);
            return null;
        } catch (Throwable e) {
            if (decoded != null && !decoded.isRecycled()) {
                decoded.recycle();
            }
            lastDecodeProblem = "could not decode: " + e.getClass().getSimpleName();
            Log.e(TAG, "Could not decode " + uri, e);
            return null;
        }
    }

    /**
     * Matches every enrolled person against their own first stored embedding.
     *
     * Each person must come back as themselves with a score near 1.0. This uses only
     * what is on disk, no camera and no photos, so it separates two very different
     * failures:
     *   - a person scores near 1.0 -> storage and matching are fine, and any Unknown
     *     from the camera is a threshold or image quality problem
     *   - a person scores low, or matches the wrong name -> the embeddings written to
     *     "data" are not the embeddings being read back, and the problem is storage
     */
    public synchronized String selfTest() {
        database.ensureFresh();
        List<String> names = database.getNames();

        StringBuilder sb = new StringBuilder();
        if (names.isEmpty()) {
            return "Nothing trained yet.";
        }

        sb.append("SAME PERSON\n");
        float worstNearest = 2f;
        boolean measured = false;
        for (int i = 0; i < names.size(); i++) {
            int n = database.getEmbeddingCount(i);
            float avg = database.internalConsistency(i);
            float[] aff = database.photoAffinities(i);

            if (aff == null || aff.length < 2) {
                sb.append("  ").append(names.get(i))
                        .append(": needs 2+ photos (has ").append(n).append(")\n");
                continue;
            }
            measured = true;

            int strays = 0;
            float nearestAvg = 0f;
            for (float a : aff) {
                nearestAvg += a;
                if (a < 0.35f) {
                    strays++;
                }
            }
            nearestAvg /= aff.length;
            worstNearest = Math.min(worstNearest, nearestAvg);

            sb.append(String.format(java.util.Locale.US,
                    "  %s: %d photos\n    nearest match %.3f  (want 0.60+)\n"
                            + "    all pairs %.3f\n",
                    names.get(i), n, nearestAvg, avg));
            if (strays > 0) {
                sb.append("    ").append(strays)
                        .append(" photo(s) match nothing. Tap Clean photos.\n");
            }
        }

        if (names.size() > 1) {
            sb.append("\nDIFFERENT PEOPLE similarity\n");
            sb.append("(healthy is below 0.40)\n");
            for (int i = 0; i < names.size(); i++) {
                for (int j = i + 1; j < names.size(); j++) {
                    sb.append(String.format(java.util.Locale.US, "  %s vs %s: %.3f\n",
                            names.get(i), names.get(j), database.crossSimilarity(i, j)));
                }
            }
        }

        sb.append("\n");
        if (!measured) {
            sb.append("Add at least 2 photos per person, then run this again.");
        } else if (worstNearest < 0.35f) {
            sb.append("BROKEN. One person's own photos do not even resemble each "
                    + "other. The embeddings carry no identity information, so no "
                    + "threshold will help. The problem is in the face crop or the "
                    + "FaceNet input, not in the matching.");
        } else if (worstNearest < 0.60f) {
            sb.append("WEAK. Matching now uses the single closest photo, so photos "
                    + "from different ages are fine. What is missing is a photo "
                    + "close to how the person looks right now, taken with this "
                    + "phone camera. Add 4 or 5 of those.");
        } else {
            sb.append("GOOD. Stored embeddings are healthy. If the camera still "
                    + "says Unknown, the gap is between your saved photos and the "
                    + "live capture, not in the storage.");
        }
        return sb.toString();
    }

    /** Human readable dump of what is actually on disk. Shown by the training dialog. */
    /** Drops photos that resemble none of the person's other photos. */
    public synchronized String cleanStrayPhotos() {
        database.ensureFresh();
        List<String> names = database.getNames();
        StringBuilder sb = new StringBuilder();
        int total = 0;
        for (int i = 0; i < names.size(); i++) {
            int removed = database.removeStrayPhotos(i, 0.35f);
            total += removed;
            sb.append("  ").append(names.get(i)).append(": removed ")
                    .append(removed).append(", kept ")
                    .append(database.getEmbeddingCount(i)).append("\n");
        }
        if (total == 0) {
            return "Nothing to clean. Every photo resembles at least one other.";
        }
        return "Removed " + total + " photo(s) that matched nothing:\n\n" + sb;
    }

    public synchronized String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("Folder:\n").append(FileUtils.isReady()
                ? FileUtils.file(FileUtils.LABEL_FILE).getParent() : "NOT READY").append("\n\n");

        if (FileUtils.isReady()) {
            sb.append("label : ").append(FileUtils.file(FileUtils.LABEL_FILE).length()).append(" bytes\n");
            sb.append("data  : ").append(FileUtils.file(FileUtils.DATA_FILE).length()).append(" bytes\n");
            sb.append("model : ").append(FileUtils.file(FileUtils.MODEL_FILE).length()).append(" bytes\n\n");
        }

        if (database.hasStaleEmbeddings()) {
            sb.append("WARNING: stored photos were made by an older face pipeline.\n")
                    .append("They cannot match a new capture. Retrain everyone.\n\n");
        }

        List<String> names = database.getNames();
        sb.append("People: ").append(names.size()).append("\n");
        for (int i = 0; i < names.size(); i++) {
            sb.append("  ").append(i).append(". ").append(names.get(i))
                    .append("  ").append(database.getEmbeddingCount(i)).append(" photos\n");
        }
        if (names.isEmpty()) {
            sb.append("  (none)\n");
        }
        return sb.toString();
    }

    public static synchronized void release() {
        if (instance != null && instance.embedder != null) {
            instance.embedder.close();
        }
        instance = null;
    }
}