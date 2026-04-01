package org.catrobat.catroid.AgeGender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class AgeGenderEngine {

    private AgeGenderEngine() {}

    // ---- Public callbacks ----
    public interface AgeCallback {
        void onResult(@Nullable Integer ageYears, @Nullable String error);
    }
    public interface GenderCallback {
        void onResult(@Nullable String gender, @Nullable String error);
    }
    public interface ExpressionCallback {
        void onResult(@Nullable String expression, @Nullable String error);
    }
    public interface AllCallback {
        void onResult(@Nullable Integer ageYears,
                      @Nullable String gender,
                      @Nullable String expression,
                      @Nullable String error);
    }

    // ---- Public API (each starts the headless activity with a ResultReceiver) ----
    public static void detectAge(@NonNull Activity activity, @NonNull AgeCallback cb) {
        start(activity, VisitorAnalysisActivity.REQUEST_AGE,
                new EngineReceiver(new Handler(Looper.getMainLooper()), cb, null, null, null));
    }

    public static void detectGender(@NonNull Activity activity, @NonNull GenderCallback cb) {
        start(activity, VisitorAnalysisActivity.REQUEST_GENDER,
                new EngineReceiver(new Handler(Looper.getMainLooper()), null, cb, null, null));
    }

    public static void detectExpression(@NonNull Activity activity, @NonNull ExpressionCallback cb) {
        start(activity, VisitorAnalysisActivity.REQUEST_EXPRESSION,
                new EngineReceiver(new Handler(Looper.getMainLooper()), null, null, cb, null));
    }

    public static void detectAll(@NonNull Activity activity, @NonNull AllCallback cb) {
        start(activity, VisitorAnalysisActivity.REQUEST_ALL,
                new EngineReceiver(new Handler(Looper.getMainLooper()), null, null, null, cb));
    }

    // ---- Internals ----
    private static void start(@NonNull Activity activity,
                              @NonNull String request,
                              @NonNull ResultReceiver receiver) {
        Intent i = new Intent(activity, VisitorAnalysisActivity.class);
        i.putExtra(VisitorAnalysisActivity.EXTRA_REQUEST, request);
        i.putExtra(VisitorAnalysisActivity.EXTRA_RECEIVER, receiver);
        // No setContentView in the activity; it’s headless & returns via ResultReceiver.
        activity.startActivity(i);
    }

    private static class EngineReceiver extends ResultReceiver {
        @Nullable private final AgeCallback ageCb;
        @Nullable private final GenderCallback genderCb;
        @Nullable private final ExpressionCallback exprCb;
        @Nullable private final AllCallback allCb;

        EngineReceiver(Handler handler,
                       @Nullable AgeCallback ageCb,
                       @Nullable GenderCallback genderCb,
                       @Nullable ExpressionCallback exprCb,
                       @Nullable AllCallback allCb) {
            super(handler);
            this.ageCb = ageCb;
            this.genderCb = genderCb;
            this.exprCb = exprCb;
            this.allCb = allCb;
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String err = resultData.getString(VisitorAnalysisActivity.EXTRA_ERROR);

            if (allCb != null) {
                Integer age = resultData.containsKey(VisitorAnalysisActivity.EXTRA_AGE)
                        ? resultData.getInt(VisitorAnalysisActivity.EXTRA_AGE) : null;
                String gender = resultData.getString(VisitorAnalysisActivity.EXTRA_GENDER);
                String expr = resultData.getString(VisitorAnalysisActivity.EXTRA_EXPRESSION);
                allCb.onResult(age, gender, expr, (resultCode == VisitorAnalysisActivity.RESULT_OK ? null : err));
                return;
            }

            if (ageCb != null) {
                Integer age = resultData.containsKey(VisitorAnalysisActivity.EXTRA_AGE)
                        ? resultData.getInt(VisitorAnalysisActivity.EXTRA_AGE) : null;
                ageCb.onResult(age, (resultCode == VisitorAnalysisActivity.RESULT_OK ? null : err));
                return;
            }

            if (genderCb != null) {
                String gender = resultData.getString(VisitorAnalysisActivity.EXTRA_GENDER);
                genderCb.onResult(gender, (resultCode == VisitorAnalysisActivity.RESULT_OK ? null : err));
                return;
            }

            if (exprCb != null) {
                String expr = resultData.getString(VisitorAnalysisActivity.EXTRA_EXPRESSION);
                exprCb.onResult(expr, (resultCode == VisitorAnalysisActivity.RESULT_OK ? null : err));
            }
        }
    }
}
