/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.camera;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Build;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class CameraManager implements Camera.PreviewCallback, OnFrameAvailableListener {

	public static final int TEXTURE_NAME = 1;
	private static CameraManager instance;
	private Camera camera;
	private SurfaceTexture texture;
	private float[] transformMatrix = new float[16];
	private List<JpgPreviewCallback> callbacks = new ArrayList<JpgPreviewCallback>();
	private int previewFormat;
	private int previewWidth;
	private int previewHeight;
	private boolean frameAvailable = false;

	public static CameraManager getInstance() {
		if (instance == null) {
			instance = new CameraManager();
		}
		return instance;
	}

	private CameraManager() {
		createTexture();
	}

	public Camera getCamera() {
		if (camera == null) {
			createCamera();
		}
		return camera;
	}

	public boolean createCamera() {
		if (camera != null) {
			return false;
		}
		camera = Camera.open();
		camera.setPreviewCallback(this);
		if (texture != null) {
			try {
				camera.setPreviewTexture(texture);
			} catch (IOException e) {
				e.printStackTrace(); // TODO
			}
		}
		return camera != null;
	}

	public void startCamera() {
		if (camera == null) {
			createCamera();
		}
		Parameters parameters = camera.getParameters();
		previewFormat = parameters.getPreviewFormat();
		previewWidth = parameters.getPreviewSize().width;
		previewHeight = parameters.getPreviewSize().height;
		camera.startPreview();
	}

	public void releaseCamera() {
		if (camera == null) {
			return;
		}
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	public void addOnJpgPreviewFrameCallback(JpgPreviewCallback callback) {
		if (callbacks.contains(callback)) {
			Log.e("Blah", "already added");
			return;
		}
		callbacks.add(callback);
	}

	public void removeOnJpgPreviewFrameCallback(JpgPreviewCallback callback) {
		callbacks.remove(callback);
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		for (JpgPreviewCallback callback : callbacks) {
			byte[] jpgData = getDecodeableBytesFromCameraFrame(data);
			callback.onJpgPreviewFrame(jpgData);
		}
	}

	private byte[] getDecodeableBytesFromCameraFrame(byte[] cameraData) {
		byte[] decodableBytes;
		YuvImage image = new YuvImage(cameraData, previewFormat, previewWidth, previewHeight, null);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		image.compressToJpeg(new Rect(0, 0, previewWidth, previewHeight), 50, out);
		decodableBytes = out.toByteArray();
		return decodableBytes;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void createTexture() {
		//		IntBuffer textures = IntBuffer.allocate(1);
		//		Gdx.gl.glGenTextures(1, textures);
		//		int textureID = textures.get(0);
		//		texture = new SurfaceTexture(textureID);

		int[] textures = new int[1];
		// generate one texture pointer and bind it as an external texture.
		GLES20.glGenTextures(1, textures, 0);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
		// No mip-mapping with camera source.
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		// Clamp to edge is only option.
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

		int textureID = textures[0];
		texture = new SurfaceTexture(textureID);
		//texture = new SurfaceTexture(TEXTURE_NAME);
		texture.setOnFrameAvailableListener(this);
		Matrix.setIdentityM(transformMatrix, 0);
		renderer = new VideoRender(textureID);

	}

	VideoRender renderer;

	public void draw() {
		if (frameAvailable) {
			texture.updateTexImage();
			texture.getTransformMatrix(transformMatrix);
			frameAvailable = false;
			renderer.setTransformMatrix(transformMatrix);
		}
		renderer.onDrawFrame(Gdx.gl10);
	}

	@Override
	public void onFrameAvailable(SurfaceTexture surfaceTexture) {
		frameAvailable = true;
	}

	private static class VideoRender {
		private static String TAG = "VideoRender";

		/*
		 * @TargetApi(Build.VERSION_CODES.HONEYCOMB)
		 * private void createTexture() {
		 * // IntBuffer textures = IntBuffer.allocate(1);
		 * // Gdx.gl.glGenTextures(1, textures);
		 * // int textureID = textures.get(0);
		 * // texture = new SurfaceTexture(textureID);
		 * 
		 * int[] textures = new int[1];
		 * // generate one texture pointer and bind it as an external texture.
		 * GLES20.glGenTextures(1, textures, 0);
		 * GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
		 * // No mip-mapping with camera source.
		 * GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		 * GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		 * // Clamp to edge is only option.
		 * GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		 * GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		 * 
		 * int texture_id = textures[0];
		 * texture = new SurfaceTexture(texture_id);
		 * //texture = new SurfaceTexture(TEXTURE_NAME);
		 * texture.setOnFrameAvailableListener(this);
		 * 
		 * }
		 */
		private static final int FLOAT_SIZE_BYTES = 4;
		private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
		private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
		private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
		private final float[] mTriangleVerticesData = {
				// X, Y, Z, U, V
				-1.0f, -1.0f, 0, 0.f, 0.f, 1.0f, -1.0f, 0, 1.f, 0.f, -1.0f, 1.0f, 0, 0.f, 1.f, 1.0f, 1.0f, 0, 1.f, 1.f, };

		private FloatBuffer mTriangleVertices;

		private final String mVertexShader = "uniform mat4 uMVPMatrix;\n" + "uniform mat4 uSTMatrix;\n"
				+ "attribute vec4 aPosition;\n" + "attribute vec4 aTextureCoord;\n" + "varying vec2 vTextureCoord;\n"
				+ "void main() {\n" + "  gl_Position = uMVPMatrix * aPosition;\n"
				+ "  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n" + "}\n";

		private final String mFragmentShader = "#extension GL_OES_EGL_image_external : require\n"
				+ "precision mediump float;\n" + "varying vec2 vTextureCoord;\n"
				+ "uniform samplerExternalOES sTexture;\n" + "void main() {\n"
				+ "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" + "}\n";

		private float[] mMVPMatrix = new float[16];
		private float[] mSTMatrix;

		public void setTransformMatrix(float[] matrix) {
			mSTMatrix = matrix;
		}

		private int mProgram;
		private int mTextureID;
		private int muMVPMatrixHandle;
		private int muSTMatrixHandle;
		private int maPositionHandle;
		private int maTextureHandle;

		private static int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

		public VideoRender(int textureID) {
			mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * FLOAT_SIZE_BYTES)
					.order(ByteOrder.nativeOrder()).asFloatBuffer();
			mTriangleVertices.put(mTriangleVerticesData).position(0);

			create(Gdx.gl10);

		}

		public void onDrawFrame(GL10 glUnused) {

			GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

			GLES20.glUseProgram(mProgram);
			checkGlError("glUseProgram");

			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);

			mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
			GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
					TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
			checkGlError("glVertexAttribPointer maPosition");
			GLES20.glEnableVertexAttribArray(maPositionHandle);
			checkGlError("glEnableVertexAttribArray maPositionHandle");

			mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
			GLES20.glVertexAttribPointer(maTextureHandle, 3, GLES20.GL_FLOAT, false,
					TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mTriangleVertices);
			checkGlError("glVertexAttribPointer maTextureHandle");
			GLES20.glEnableVertexAttribArray(maTextureHandle);
			checkGlError("glEnableVertexAttribArray maTextureHandle");

			Matrix.setIdentityM(mMVPMatrix, 0);
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			GLES20.glUniformMatrix4fv(muSTMatrixHandle, 1, false, mSTMatrix, 0);

			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
			checkGlError("glDrawArrays");
			GLES20.glFinish();

		}

		public void create(GL10 glUnused) {
			mProgram = createProgram(mVertexShader, mFragmentShader);
			if (mProgram == 0) {
				return;
			}
			maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
			checkGlError("glGetAttribLocation aPosition");
			if (maPositionHandle == -1) {
				throw new RuntimeException("Could not get attrib location for aPosition");
			}
			maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
			checkGlError("glGetAttribLocation aTextureCoord");
			if (maTextureHandle == -1) {
				throw new RuntimeException("Could not get attrib location for aTextureCoord");
			}

			muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
			checkGlError("glGetUniformLocation uMVPMatrix");
			if (muMVPMatrixHandle == -1) {
				throw new RuntimeException("Could not get attrib location for uMVPMatrix");
			}

			muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
			checkGlError("glGetUniformLocation uSTMatrix");
			if (muSTMatrixHandle == -1) {
				throw new RuntimeException("Could not get attrib location for uSTMatrix");
			}
			/*
			 * int[] textures = new int[1];
			 * GLES20.glGenTextures(1, textures, 0);
			 * 
			 * mTextureID = textures[0];
			 * GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
			 * checkGlError("glBindTexture mTextureID");
			 * 
			 * GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			 * GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			 * 
			 * mSurface = new SurfaceTexture(mTextureID);
			 * 
			 * camera = Camera.open();
			 * try {
			 * camera.setPreviewTexture(mSurface);
			 * } catch (IOException e) {
			 * e.printStackTrace(); // TODO
			 * }
			 * 
			 * synchronized (this) {
			 * updateSurface = false;
			 * }
			 * camera.startPreview();
			 * Log.d("Blah", " me started");
			 * ?
			 */
		}

		private int loadShader(int shaderType, String source) {
			int shader = GLES20.glCreateShader(shaderType);
			if (shader != 0) {
				GLES20.glShaderSource(shader, source);
				GLES20.glCompileShader(shader);
				int[] compiled = new int[1];
				GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
				if (compiled[0] == 0) {
					Log.e(TAG, "Could not compile shader " + shaderType + ":");
					Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
					GLES20.glDeleteShader(shader);
					shader = 0;
				}
			}
			return shader;
		}

		private int createProgram(String vertexSource, String fragmentSource) {
			int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
			if (vertexShader == 0) {
				return 0;
			}
			int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
			if (pixelShader == 0) {
				return 0;
			}

			int program = GLES20.glCreateProgram();
			if (program != 0) {
				GLES20.glAttachShader(program, vertexShader);
				checkGlError("glAttachShader");
				GLES20.glAttachShader(program, pixelShader);
				checkGlError("glAttachShader");
				GLES20.glLinkProgram(program);
				int[] linkStatus = new int[1];
				GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
				if (linkStatus[0] != GLES20.GL_TRUE) {
					Log.e(TAG, "Could not link program: ");
					Log.e(TAG, GLES20.glGetProgramInfoLog(program));
					GLES20.glDeleteProgram(program);
					program = 0;
				}
			}
			return program;
		}

		private void checkGlError(String op) {
			int error;
			while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
				Log.e(TAG, op + ": glError " + error);
				throw new RuntimeException(op + ": glError " + error);
			}
		}

	} // End of class VideoRender.

}
