/* Copyright 2016 The TensorFlow Authors. All Rights Reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package org.catrobat.catroid.FaceRecognizer.ml;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Trace;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.channels.FileChannel;

import org.catrobat.catroid.FaceRecognizer.env.ImageUtils;

public class FaceNet {
    private static final String MODEL_FILE = "facenet.tflite";

    public static final int EMBEDDING_SIZE = 512;

    private static final int INPUT_SIZE_HEIGHT = 160;
    private static final int INPUT_SIZE_WIDTH = 160;

    private static final int BYTE_SIZE_OF_FLOAT = 4;

    // Pre-allocated buffers.
    private int[] intValues;
    private float[] rgbValues;

    private FloatBuffer inputBuffer;
    private FloatBuffer outputBuffer;

    private Bitmap bitmap;

    private Interpreter interpreter;

    /** Memory-map the model file in Assets. */
    private static ByteBuffer loadModelFile(AssetManager assets)
            throws IOException {
        AssetFileDescriptor fileDescriptor = assets.openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    public static FaceNet create(final AssetManager assetManager) {
        final FaceNet f = new FaceNet();

        try {
            f.interpreter = new Interpreter(loadModelFile(assetManager));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Pre-allocate buffers.
        f.intValues = new int[INPUT_SIZE_HEIGHT * INPUT_SIZE_WIDTH];
        f.rgbValues = new float[INPUT_SIZE_HEIGHT * INPUT_SIZE_WIDTH * 3];
        f.inputBuffer = ByteBuffer.allocateDirect(INPUT_SIZE_HEIGHT * INPUT_SIZE_WIDTH * 3 * BYTE_SIZE_OF_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        f.outputBuffer = ByteBuffer.allocateDirect(EMBEDDING_SIZE * BYTE_SIZE_OF_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        f.bitmap = Bitmap.createBitmap(INPUT_SIZE_WIDTH, INPUT_SIZE_HEIGHT, Bitmap.Config.ARGB_8888);
        return f;
    }

    private FaceNet() {}

    public FloatBuffer getEmbeddings(Bitmap originalBitmap, Rect rect) {
        // Log this method so that it can be analyzed with systrace.
        Trace.beginSection("getEmbeddings");

        Trace.beginSection("preprocessBitmap");
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(originalBitmap, rect,
                new Rect(0, 0, INPUT_SIZE_WIDTH, INPUT_SIZE_HEIGHT), null);

        bitmap.getPixels(intValues, 0, INPUT_SIZE_WIDTH, 0, 0,
                INPUT_SIZE_WIDTH, INPUT_SIZE_HEIGHT);
        ImageUtils.saveBitmap(bitmap);

        for (int i = 0; i < intValues.length; ++i) {
            int p = intValues[i];

            rgbValues[i * 3 + 2] = (float) (p & 0xFF);
            rgbValues[i * 3 + 1] = (float) ((p >> 8) & 0xFF);
            rgbValues[i * 3 + 0] = (float) ((p >> 16) & 0xFF);
        }

        ImageUtils.prewhiten(rgbValues, inputBuffer);

        Trace.endSection(); // preprocessBitmap

        // Run the inference call.
        Trace.beginSection("run");
        outputBuffer.rewind();
        interpreter.run(inputBuffer, outputBuffer);
        outputBuffer.flip();
        Trace.endSection();

        Trace.endSection(); // "getEmbeddings"
        return outputBuffer;
    }

    public void close() {
        interpreter.close();
    }
}

