package org.catrobat.catroid.FaceRecognizer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.util.Range
import android.util.Rational
import android.util.Size
import android.view.Surface
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.init
import org.catrobat.catroid.formulaeditor.SensorHandler
import java.util.Arrays
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.Volatile
import kotlin.math.max
import kotlin.math.min


object FaceDetector {
    private const val TAG = "FaceDetector"

    const val UNKNOWN: String = "Unknown"

    /** Frames to score before deciding. More frames, less noise.  */
    private const val BURST_FRAMES = 3

    /** Longest JPEG side. A bigger face crop makes a sharper network input.  */
    private const val TARGET_JPEG_SIDE = 1280

    /* Give Camera2 real preview frames so AE/AF/AWB can settle before JPEG #1. */
    private const val FIRST_SHOT_DELAY_MS: Long = 1200
    private const val BETWEEN_SHOTS_DELAY_MS: Long = 350

    /* Exposure bracket in EV. 0 works indoors; -1 and -2 protect outdoor faces. */
    private val EXPOSURE_BRACKET_EV = floatArrayOf(0f, -1f, -2f)

    /** Nothing may leave the camera open, or a program waiting, longer than this.  */
    private const val WATCHDOG_MS: Long = 20000

    /** Shortest gap between attempts, in case something asks repeatedly.  */
    private const val MIN_INTERVAL_MS: Long = 5000

    /**
     * Stop the burst early when the first frame is already this far above the
     * accept threshold. Turns a typical detection from about two seconds into
     * under one, and still uses all three frames when the match is marginal.
     */
    private const val EARLY_EXIT_MARGIN = 0.15f

    /** True while a capture is in flight.  */
    // ---------------- The gate ----------------
    @Volatile
    var isRunning: Boolean = false
        private set

    @Volatile
    var isDetectionDone: Boolean = false
        private set
    private var lastDetectionStart = 0L

    @JvmStatic
    @Volatile
    var lastName: String = UNKNOWN
        private set

    @JvmStatic
    @Volatile
    var lastConfidence: Float = 0f
        private set

    /**
     * The camera is only allowed to open while a script is actually running.
     *
     * A sensor is read from more places than a script: the brick view evaluates
     * formulas to draw the block label, and the stage evaluates them while it is
     * preparing. Those reads must report the last known name, never open a camera,
     * or the camera fires before the script reaches the block that wants it.
     *
     * StageActivity turns this on when the program starts and off when it stops.
     */
    @Volatile
    private var scriptRunning = false

    @JvmStatic
    fun setScriptRunning(running: Boolean) {
        scriptRunning = running
        Log.i(TAG, "Script running = " + running)
    }

    @JvmStatic
    fun isScriptRunning(): Boolean {
        return scriptRunning
    }

    fun hasPermission(context: Context?): Boolean {
        return context != null && ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * How long a blocking read will wait. Kept well under any watchdog so the
     * caller always gets an answer rather than being stuck.
     */
    private const val BLOCKING_WAIT_MS: Long = 12000

    /**
     * Detects and waits for the answer, then returns the name.
     *
     * This is what makes
     *
     * Set variable rr to (face name detection)
     *
     * work on its own, with no separate detect block. The read pauses until the
     * camera is finished, so the very next block sees the real name.
     *
     * Safe to call from a Catroid script, because scripts run on the libGDX
     * thread, not the Android main thread, so pausing it cannot cause an ANR. It
     * pauses the stage for about a second while the camera works.
     *
     * Called from the main thread it never blocks. It starts the capture and
     * returns the previous name, because freezing the UI thread would be an ANR.
     *
     * Only the first read in a program run opens the camera. Later reads return
     * the stored name immediately, so a script can use the sensor as often as it
     * likes.
     */
    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun detectBlocking(context: Context?): String {
        if (isDetectionDone) {
            return lastName
        }

        // Rule one: only a running script may open the camera. Everything else
        // gets the last known name. This is what stops the camera firing before
        // the script reaches the block that asked for it.
        if (!scriptRunning) {
            Log.i(
                TAG, ("Read outside a running script, reporting '" + lastName
                    + "' without opening the camera")
            )
            return lastName
        }

        // Rule two: only the stage thread may open the camera. The UI thread
        // reads sensors to draw brick labels.
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.i(
                TAG, ("Read on the main thread, reporting '" + lastName
                    + "' without opening the camera")
            )
            return lastName
        }

        Log.i(
            TAG, ("Blocking read from thread '"
                + Thread.currentThread().getName() + "', opening the camera")
        )
        if (context == null) {
            return lastName
        }

        val latch = CountDownLatch(1)
        val started = requestDetection(context, object : Callback {
            override fun onFinished(name: String?, confidence: Float) {
                latch.countDown()
            }
        })

        if (!started && !isRunning) {
            return lastName
        }

        try {
            if (!latch.await(BLOCKING_WAIT_MS, TimeUnit.MILLISECONDS)) {
                Log.w(TAG, "Blocking read timed out")
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        Log.i(TAG, "Blocking read returning '" + lastName + "'")
        return lastName
    }

    /**
     * Forces a fresh detection, ignoring the one per run latch and the interval.
     *
     * This is what a brick uses. A brick is an explicit instruction from the
     * child, so unlike a sensor read it should always mean "look now".
     */
    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun detectNow(context: Context?, callback: Callback?): Boolean {
        synchronized(FaceDetector::class.java) {
            if (isRunning) {
                Log.i(TAG, "A capture is already in flight, joining it")
                return false
            }
            isDetectionDone = false
            lastDetectionStart = 0L
        }
        return requestDetection(context, callback)
    }

    /**
     * Opens detection again. Call once per program start, including the restart
     * button, or only the first run will ever detect.
     */
    @JvmStatic
    @Synchronized
    fun resetForNewRun() {
        scriptRunning = false
        isDetectionDone = false
        isRunning = false
        lastDetectionStart = 0L
        lastName = UNKNOWN
        lastConfidence = 0f
        SensorHandler.setFaceNameRecognitionResult(UNKNOWN)
        Log.i(TAG, "Detection reopened for a new run")
    }

    /**
     * Runs one detection in the background. Returns straight away.
     *
     * The callback is guaranteed to fire exactly once, on success, on failure and
     * on timeout, so a caller can safely use it to release a waiting program.
     * Returns false when nothing was started, in which case the callback has
     * already been called.
     */
    @JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun requestDetection(context: Context?, callback: Callback?): Boolean {
        val safeCallback = callback ?: object : Callback {
            override fun onFinished(name: String?, confidence: Float) {
                // Intentionally empty: no result handling is required when no callback is supplied.
            }
        }

        if (context == null) {
            Log.e(TAG, "No context")
            finish(UNKNOWN, 0f, safeCallback)
            return false
        }

        synchronized(FaceDetector::class.java) {
            if (isDetectionDone) {
                Log.i(TAG, "Already detected this run")
                safeCallback.onFinished(lastName, lastConfidence)
                return false
            }
            Log.i(
                TAG, ("Capture starting, requested by thread '"
                    + Thread.currentThread().getName() + "'")
            )
            if (isRunning) {
                Log.i(TAG, "A detection is already running")
                return false
            }
            val now = System.currentTimeMillis()
            if (now - lastDetectionStart < MIN_INTERVAL_MS) {
                return false
            }
            isRunning = true
            lastDetectionStart = now
        }

        if (!hasPermission(context)) {
            Log.e(
                TAG, ("CAMERA permission is not granted. Map FACE_NAME_DETECTION to "
                    + "CAMERA in BrickResourcesToRuntimePermissions so Catroid asks "
                    + "for it before the stage starts.")
            )
            finish(UNKNOWN, 0f, safeCallback)
            return false
        }

        Session(context.getApplicationContext(), safeCallback).start()
        return true
    }

    @Synchronized
    private fun finish(name: String?, confidence: Float, callback: Callback) {
        lastName = if (name == null || name.trim { it <= ' ' }
                .isEmpty()) UNKNOWN else name.trim { it <= ' ' }
        lastConfidence = confidence
        isRunning = false
        isDetectionDone = true

        try {
            SensorHandler.setFaceNameRecognitionResult(lastName)
        } catch (t: Throwable) {
            Log.e(TAG, "Could not write the sensor", t)
        }
        Log.i(TAG, "Detection finished: '" + lastName + "' confidence " + confidence)

        try {
            callback.onFinished(lastName, confidence)
        } catch (t: Throwable) {
            Log.e(TAG, "Callback failed", t)
        }
    }

    /** Called once when a detection ends, whatever the outcome.  */
    interface Callback {
        fun onFinished(name: String?, confidence: Float)
    }

    // ---------------- One capture ----------------
    private class Session(private val context: Context, private val callback: Callback) {
        private var recognizer: Recognizer? = null
        private var scores: Recognizer.Session? = null

        private var thread: HandlerThread? = null
        private var handler: Handler? = null
        private var cameraDevice: CameraDevice? = null
        private var captureSession: CameraCaptureSession? = null
        private var imageReader: ImageReader? = null
        private var stillBuilder: CaptureRequest.Builder? = null
        private var previewTexture: SurfaceTexture? = null
        private var previewSurface: Surface? = null
        private var exposureBracket = intArrayOf(0, 0, 0)
        private var cameraId: String? = null

        private var shotsRequested = 0

        @Volatile
        private var ended = false

        fun start() {
            try {
                init(context)

                val activeRecognizer =
                    Recognizer.getInstance(context)

                recognizer = activeRecognizer

                if (activeRecognizer.classNames.isEmpty()) {
                    Log.w(TAG, "Nobody has been trained yet")
                    end(UNKNOWN, 0f)
                    return
                }

                scores = activeRecognizer.newSession()
            } catch (e: Exception) {
                Log.e(TAG, "Recognizer not available", e)
                end(UNKNOWN, 0f)
                return
            }

            val detectorThread =
                HandlerThread("face_detector")

            thread = detectorThread
            detectorThread.start()

            val detectorHandler =
                Handler(detectorThread.looper)

            handler = detectorHandler

            detectorHandler.postDelayed(
                {
                    if (!ended) {
                        Log.w(TAG, "Watchdog fired")
                        decide()
                    }
                },
                WATCHDOG_MS
            )

            detectorHandler.post {
                openCamera()
            }
        }
        fun openCamera() {
            try {
                val manager =
                    context.getSystemService(Context.CAMERA_SERVICE) as CameraManager?
                if (manager == null) {
                    end(UNKNOWN, 0f)
                    return
                }

                cameraId = chooseCameraId(manager)
                if (cameraId == null) {
                    Log.e(TAG, "No usable camera")
                    end(UNKNOWN, 0f)
                    return
                }

                val jpeg = pickJpegSize(manager, cameraId!!)
                exposureBracket = buildExposureBracket(manager, cameraId!!)
                Log.i(
                    TAG, ("Capturing " + jpeg.getWidth() + "x" + jpeg.getHeight()
                        + " on camera " + cameraId)
                )

                imageReader = ImageReader.newInstance(
                    jpeg.getWidth(), jpeg.getHeight(),
                    ImageFormat.JPEG, BURST_FRAMES + 1
                )
                imageReader!!.setOnImageAvailableListener(onImageAvailable, handler)

                manager.openCamera(cameraId!!, object : CameraDevice.StateCallback() {
                    override fun onOpened(device: CameraDevice) {
                        cameraDevice = device
                        createSession()
                    }

                    override fun onDisconnected(device: CameraDevice) {
                        device.close()
                        decide()
                    }

                    override fun onError(device: CameraDevice, error: Int) {
                        Log.e(TAG, "Camera error " + error)
                        device.close()
                        decide()
                    }
                }, handler)
            } catch (e: SecurityException) {
                Log.e(TAG, "Camera permission was revoked", e)
                end(UNKNOWN, 0f)
            } catch (e: Exception) {
                Log.e(TAG, "Could not open the camera", e)
                end(UNKNOWN, 0f)
            }
        }

        fun createSession() {
            try {
                val target = imageReader!!.getSurface()
                /*
				 * A delay after opening a camera does not converge 3A unless requests are
				 * actually flowing.  Use a headless SurfaceTexture as a preview target;
				 * this class still needs no Activity or visible preview.
				 */
                /* Detached SurfaceTexture: no OpenGL context or Activity is required. */
                previewTexture = SurfaceTexture(false)
                previewTexture!!.setDefaultBufferSize(640, 480)
                previewSurface = Surface(previewTexture)
                cameraDevice!!.createCaptureSession(
                    Arrays.asList<Surface?>(previewSurface, target),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(s: CameraCaptureSession) {
                            captureSession = s
                            try {
                                val previewBuilder =
                                    cameraDevice!!.createCaptureRequest(
                                        CameraDevice.TEMPLATE_PREVIEW
                                    )
                                previewBuilder.addTarget(previewSurface!!)
                                configure3A(previewBuilder)
                                captureSession!!.setRepeatingRequest(
                                    previewBuilder.build(), null, handler
                                )

                                stillBuilder = cameraDevice!!.createCaptureRequest(
                                    CameraDevice.TEMPLATE_STILL_CAPTURE
                                )
                                stillBuilder!!.addTarget(target)
                                configure3A(stillBuilder!!)
                                stillBuilder!!.set<Byte?>(CaptureRequest.JPEG_QUALITY, 95.toByte())

                                handler!!.postDelayed(
                                    Runnable { this@Session.takeShot() },
                                    FIRST_SHOT_DELAY_MS
                                )
                            } catch (e: Exception) {
                                Log.e(TAG, "Could not build the request", e)
                                decide()
                            }
                        }

                        override fun onConfigureFailed(s: CameraCaptureSession) {
                            Log.e(TAG, "Session configuration failed")
                            decide()
                        }
                    }, handler
                )
            } catch (e: Exception) {
                Log.e(TAG, "Could not create the session", e)
                decide()
            }
        }

        fun takeShot() {
            if (ended || captureSession == null || stillBuilder == null) {
                return
            }
            if (shotsRequested >= BURST_FRAMES) {
                decide()
                return
            }
            val bracketIndex = shotsRequested
            shotsRequested++
            try {
                stillBuilder!!.set<Int?>(
                    CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION,
                    exposureBracket[min(bracketIndex, exposureBracket.size - 1)]
                )
                captureSession!!.capture(
                    stillBuilder!!.build(),
                    object : CameraCaptureSession.CaptureCallback() {}, handler
                )
            } catch (e: Exception) {
                Log.e(TAG, "Capture " + shotsRequested + " failed", e)
                decide()
            }
        }

        fun configure3A(builder: CaptureRequest.Builder) {
            builder.set<Int?>(CaptureRequest.CONTROL_MODE, CaptureRequest.CONTROL_MODE_AUTO)
            builder.set<Int?>(
                CaptureRequest.CONTROL_AF_MODE,
                CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
            )
            builder.set<Int?>(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON)
            builder.set<Int?>(CaptureRequest.CONTROL_AWB_MODE, CaptureRequest.CONTROL_AWB_MODE_AUTO)
        }

        private val onImageAvailable =
            ImageReader.OnImageAvailableListener { reader: ImageReader? ->
                var image: Image? = null
                var decoded: Bitmap? = null
                var upright: Bitmap? = null
                try {
                    image = reader!!.acquireLatestImage()
                    if (ended || image == null) {
                        return@OnImageAvailableListener
                    }

                    val buffer = image.getPlanes()[0].getBuffer()
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)

                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888
                    decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                    if (decoded == null) {
                        next()
                        return@OnImageAvailableListener
                    }

                    upright = rotateToSensorUpright(decoded)
                    if (isSeverelyOverexposed(upright)) {
                        Log.i(
                            TAG, ("Frame " + shotsRequested
                                + " rejected: highlights are clipped")
                        )
                    } else {
                        recognizer!!.addFrame(scores, upright, this.isFrontCamera)
                    }

                    val early = recognizer!!.peekSession(scores)
                    if (early != null
                        && early.confidence > FaceDatabase.Companion.minSimilarity + EARLY_EXIT_MARGIN
                    ) {
                        Log.i(TAG, "Clear match on frame " + shotsRequested + ", stopping early")
                        end(early.name, early.confidence)
                        return@OnImageAvailableListener
                    }

                    if (shotsRequested >= BURST_FRAMES) {
                        decide()
                    } else {
                        next()
                    }
                } catch (t: Throwable) {
                    Log.e(TAG, "Frame failed", t)
                    next()
                } finally {
                    if (upright != null && upright != decoded && !upright.isRecycled()) {
                        upright.recycle()
                    }
                    if (decoded != null && !decoded.isRecycled()) {
                        decoded.recycle()
                    }
                    if (image != null) {
                        image.close()
                    }
                }
            }

        fun next() {
            if (ended) {
                return
            }
            if (shotsRequested >= BURST_FRAMES) {
                decide()
            } else {
                handler!!.postDelayed(Runnable { this.takeShot() }, BETWEEN_SHOTS_DELAY_MS)
            }
        }

        /**
         * Clipped white pixels contain no recoverable facial texture.  Do not let an
         * overexposed 0-EV frame lower the session average; the following -1/-2 EV
         * bracket frames will retain the eyes, nose and skin texture.
         */
        fun isSeverelyOverexposed(bitmap: Bitmap?): Boolean {
            if (bitmap == null || bitmap.isRecycled()) {
                return true
            }
            val left = bitmap.getWidth() / 4
            val top = bitmap.getHeight() / 4
            val right = bitmap.getWidth() * 3 / 4
            val bottom = bitmap.getHeight() * 3 / 4
            var sampled = 0
            var clipped = 0
            var luminanceSum = 0L
            val step = max(1, min(bitmap.getWidth(), bitmap.getHeight()) / 120)
            var y = top
            while (y < bottom) {
                var x = left
                while (x < right) {
                    val p = bitmap.getPixel(x, y)
                    val r = (p shr 16) and 0xff
                    val g = (p shr 8) and 0xff
                    val b = p and 0xff
                    val luma = (77 * r + 150 * g + 29 * b) shr 8
                    luminanceSum += luma.toLong()
                    if (r >= 250 && g >= 250 && b >= 250) {
                        clipped++
                    }
                    sampled++
                    x += step
                }
                y += step
            }
            if (sampled == 0) {
                return false
            }
            val clippedRatio = clipped.toFloat() / sampled
            val meanLuma = luminanceSum.toFloat() / sampled
            return clippedRatio > 0.45f || meanLuma > 238f
        }

        fun decide() {
            if (ended) {
                return
            }
            var result: Recognizer.Result? = null
            try {
                result = recognizer!!.finishSession(scores)
            } catch (t: Throwable) {
                Log.e(TAG, "Scoring failed", t)
            }
            if (result == null) {
                end(UNKNOWN, 0f)
            } else {
                end(result.name, result.confidence)
            }
        }

        @Synchronized
        fun end(name: String?, confidence: Float) {
            if (ended) {
                return
            }
            ended = true
            closeCamera()
            finish(name, confidence, callback)
            stopThread()
        }

        fun closeCamera() {
            try {
                if (captureSession != null) {
                    captureSession!!.close()
                    captureSession = null
                }
                if (cameraDevice != null) {
                    cameraDevice!!.close()
                    cameraDevice = null
                }
                if (imageReader != null) {
                    imageReader!!.close()
                    imageReader = null
                }
                if (previewSurface != null) {
                    previewSurface!!.release()
                    previewSurface = null
                }
                if (previewTexture != null) {
                    previewTexture!!.release()
                    previewTexture = null
                }
                stillBuilder = null
            } catch (ignored: Exception) {
                // nothing useful to do
            }
        }

        fun stopThread() {
            val toStop = thread
            thread = null
            if (toStop == null) {
                return
            }
            // Quit from outside its own looper, or the callback never returns.
            Thread(Runnable {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    toStop.quitSafely()
                } else {
                    toStop.quit()
                }
            }).start()
        }

        /**
         * Rotates by sensor orientation only. There is no activity, so screen
         * rotation is unknown. FaceEmbedder tries all four rotations anyway, so an
         * approximate upright is enough.
         */
        fun rotateToSensorUpright(decoded: Bitmap): Bitmap? {
            try {
                val manager =
                    context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val sensor = manager.getCameraCharacteristics(cameraId!!)
                    .get<Int?>(CameraCharacteristics.SENSOR_ORIENTATION)
                if (sensor == null || sensor % 360 == 0) {
                    return decoded
                }
                val matrix = Matrix()
                matrix.postRotate(sensor.toFloat())
                return Bitmap.createBitmap(
                    decoded, 0, 0,
                    decoded.getWidth(), decoded.getHeight(), matrix, true
                )
            } catch (e: Exception) {
                return decoded
            }
        }

        val isFrontCamera: Boolean
            get() {
                try {
                    val manager =
                        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                    val facing = manager.getCameraCharacteristics(cameraId!!)
                        .get<Int?>(CameraCharacteristics.LENS_FACING)
                    return facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT
                } catch (e: Exception) {
                    return true
                }
            }

        @Throws(CameraAccessException::class)
        fun chooseCameraId(manager: CameraManager): String? {
            var back: String? = null
            var front: String? = null
            for (id in manager.getCameraIdList()) {
                val facing = manager.getCameraCharacteristics(id)
                    .get<Int?>(CameraCharacteristics.LENS_FACING)
                if (facing == null) {
                    continue
                }
                if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    front = id
                } else if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                    back = id
                }
            }
            return if (front != null) front else back
        }

        @Throws(CameraAccessException::class)
        fun pickJpegSize(manager: CameraManager, id: String): Size {
            val map = manager.getCameraCharacteristics(id)
                .get<StreamConfigurationMap?>(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val sizes = if (map != null) map.getOutputSizes(ImageFormat.JPEG) else null
            if (sizes == null || sizes.size == 0) {
                return Size(1280, 960)
            }

            var bestUnder: Size? = null
            var smallest = sizes[0]
            for (s in sizes) {
                if (s.getWidth() * s.getHeight() < smallest.getWidth() * smallest.getHeight()) {
                    smallest = s
                }
                if (max(s.getWidth(), s.getHeight()) <= TARGET_JPEG_SIDE
                    && (bestUnder == null || (s.getWidth() * s.getHeight()
                        > bestUnder.getWidth() * bestUnder.getHeight()))
                ) {
                    bestUnder = s
                }
            }
            return if (bestUnder != null) bestUnder else smallest
        }

        /** Converts desired EV values to this phone's Camera2 compensation indices.  */
        @Throws(CameraAccessException::class)
        fun buildExposureBracket(manager: CameraManager, id: String): IntArray {
            val c = manager.getCameraCharacteristics(id)
            val range = c.get<Range<Int?>?>(
                CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE
            )
            val step = c.get<Rational?>(
                CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP
            )
            val values = IntArray(EXPOSURE_BRACKET_EV.size)
            if (range == null || step == null || step.toFloat() <= 0f) {
                return values // Device does not expose exposure compensation.
            }
            val evPerIndex = step.toFloat()
            for (i in values.indices) {
                val index = Math.round(EXPOSURE_BRACKET_EV[i] / evPerIndex)
                values[i] = max(range.getLower()!!, min(range.getUpper()!!, index))
            }
            Log.i(
                TAG,
                ("AE bracket indices " + values.contentToString() + ", step " + evPerIndex + " EV")
            )
            return values
        }
    }
}