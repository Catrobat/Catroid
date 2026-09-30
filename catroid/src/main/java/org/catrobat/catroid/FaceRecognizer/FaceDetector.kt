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
import android.util.Log
import android.util.Range
import android.util.Rational
import android.util.Size
import android.view.Surface
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.init
import org.catrobat.catroid.formulaeditor.SensorHandler
import java.util.Arrays
import kotlin.concurrent.Volatile
import kotlin.math.max
import kotlin.math.min


object FaceDetector {
    private const val TAG = "FaceDetector"

    const val UNKNOWN: String = "Unknown"

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

    fun hasPermission(context: Context?): Boolean {
        return context != null && ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Forces a fresh detection, ignoring an earlier result and the interval.
     *
     * This is the only way a capture starts: the Detect face name brick calls
     * it. Reading the "detected face name" sensor only returns the last result.
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
        val safeCallback: Callback = callback ?: Callback { _, _ -> }

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

        captureStarter.start(context.getApplicationContext()) { name, confidence ->
            finish(name, confidence, safeCallback)
        }
        return true
    }

    /**
     * Runs one capture and reports the recognised name, or [UNKNOWN], exactly once.
     *
     * In the app this is always the camera [Session]. Tests replace it so the
     * brick -> detector -> sensor chain can run without a camera; everything
     * around it (permission check, run gate, [finish] writing the sensor) stays
     * the production code.
     */
    fun interface CaptureStarter {
        fun start(context: Context, onResult: Callback)
    }

    private val cameraCapture = CaptureStarter { context, onResult ->
        Session(context, onResult).start()
    }

    @VisibleForTesting
    @Volatile
    internal var captureStarter: CaptureStarter = cameraCapture

    @VisibleForTesting
    internal fun useCameraCapture() {
        captureStarter = cameraCapture
        frameSource = null
    }

    /**
     * Supplies the frames of a capture in place of the camera. The camera
     * [Session] still runs everything else: the recognition burst, shot
     * scheduling, the watchdog, the decision and the result. Only Camera2, the
     * JPEG decode and the rotation to upright are replaced.
     */
    interface FrameSource {
        /** Front-camera frames are also tried mirrored, as the camera path does. */
        val isFrontCamera: Boolean get() = true

        /**
         * Returns upright frame [shot] (0-based), or null if it is unusable.
         * Called on the detector thread; the session recycles the bitmap.
         */
        fun frame(shot: Int): Bitmap?
    }

    /** Null in the app: frames come from the camera. */
    @VisibleForTesting
    @Volatile
    internal var frameSource: FrameSource? = null

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
    fun interface Callback {
        fun onFinished(
            name: String?,
            confidence: Float
        )
    }

    // ---------------- One capture ----------------
    private class Session(private val context: Context, private val callback: Callback) {
        /** The recognition loop; shared with the tests, see [FrameBurst]. */
        private var burst: FrameBurst? = null

        /** Null in the app. Tests supply frames here instead of the camera. */
        private val frames: FrameSource? = frameSource

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

                val newBurst = Recognizer.getInstance(context).newBurst()

                if (newBurst == null) {
                    Log.w(TAG, "Nobody has been trained yet")
                    end(UNKNOWN, 0f)
                    return
                }

                burst = newBurst
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

            if (frames == null) {
                detectorHandler.post { openCamera() }
            } else {
                detectorHandler.post { takeShot() }
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

                val id = chooseCameraId(manager)
                if (id == null) {
                    Log.e(TAG, "No usable camera")
                    end(UNKNOWN, 0f)
                    return
                }
                cameraId = id

                val jpeg = pickJpegSize(manager, id)
                exposureBracket = buildExposureBracket(manager, id)
                Log.i(
                    TAG, ("Capturing " + jpeg.getWidth() + "x" + jpeg.getHeight()
                        + " on camera " + id)
                )

                val reader = ImageReader.newInstance(
                    jpeg.getWidth(), jpeg.getHeight(),
                    ImageFormat.JPEG, FrameBurst.FRAMES + 1
                )
                imageReader = reader
                reader.setOnImageAvailableListener(onImageAvailable, handler)

                manager.openCamera(id, object : CameraDevice.StateCallback() {
                    override fun onOpened(device: CameraDevice) {
                        cameraDevice = device
                        createSession(device, reader)
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

        fun createSession(device: CameraDevice, reader: ImageReader) {
            try {
                val target = reader.getSurface()
                /*
				 * A delay after opening a camera does not converge 3A unless requests are
				 * actually flowing.  Use a headless SurfaceTexture as a preview target;
				 * this class still needs no Activity or visible preview.
				 */
                /* Detached SurfaceTexture: no OpenGL context or Activity is required. */
                val texture = SurfaceTexture(false)
                texture.setDefaultBufferSize(640, 480)
                previewTexture = texture
                val preview = Surface(texture)
                previewSurface = preview
                device.createCaptureSession(
                    Arrays.asList<Surface?>(preview, target),
                    object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(s: CameraCaptureSession) {
                            captureSession = s
                            try {
                                val previewBuilder =
                                    device.createCaptureRequest(
                                        CameraDevice.TEMPLATE_PREVIEW
                                    )
                                previewBuilder.addTarget(preview)
                                configure3A(previewBuilder)
                                s.setRepeatingRequest(
                                    previewBuilder.build(), null, handler
                                )

                                val still = device.createCaptureRequest(
                                    CameraDevice.TEMPLATE_STILL_CAPTURE
                                )
                                still.addTarget(target)
                                configure3A(still)
                                still.set<Byte?>(CaptureRequest.JPEG_QUALITY, 95.toByte())
                                stillBuilder = still

                                handler?.postDelayed(
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
            val source = frames
            val session = captureSession
            val still = stillBuilder
            if (ended || (source == null && (session == null || still == null))) {
                return
            }
            if (shotsRequested >= FrameBurst.FRAMES) {
                decide()
                return
            }
            val bracketIndex = shotsRequested
            shotsRequested++
            if (source != null) {
                deliverFromSource(source, bracketIndex)
                return
            }
            if (session == null || still == null) {
                return
            }
            try {
                still.set<Int?>(
                    CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION,
                    exposureBracket[min(bracketIndex, exposureBracket.size - 1)]
                )
                session.capture(
                    still.build(),
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
                    image = reader?.acquireLatestImage()
                    if (ended || image == null) {
                        return@OnImageAvailableListener
                    }

                    val buffer = image.getPlanes()[0].getBuffer()
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)

                    val options = BitmapFactory.Options()
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888
                    decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
                    upright = decoded?.let { rotateToSensorUpright(it) }

                    handleFrame(upright, this.isFrontCamera)
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

        /** Every frame, from the camera or a [FrameSource], goes through here. */
        fun handleFrame(frame: Bitmap?, mirrorToo: Boolean) {
            val activeBurst = burst
            if (activeBurst == null) {
                decide()
                return
            }
            if (activeBurst.addFrame(frame, mirrorToo)) {
                endWith(activeBurst.result)
            } else {
                next()
            }
        }

        fun deliverFromSource(source: FrameSource, shot: Int) {
            var frame: Bitmap? = null
            try {
                frame = source.frame(shot)
                handleFrame(frame, source.isFrontCamera)
            } catch (t: Throwable) {
                Log.e(TAG, "Frame failed", t)
                next()
            } finally {
                if (frame != null && !frame.isRecycled()) {
                    frame.recycle()
                }
            }
        }

        fun next() {
            if (ended) {
                return
            }
            val detectorHandler = handler
            if (shotsRequested >= FrameBurst.FRAMES || detectorHandler == null) {
                decide()
            } else {
                detectorHandler.postDelayed(Runnable { this.takeShot() }, BETWEEN_SHOTS_DELAY_MS)
            }
        }

        /** Decides with the frames that arrived, e.g. when the watchdog fires. */
        fun decide() {
            if (ended) {
                return
            }
            var result: Recognizer.Result? = null
            try {
                result = burst?.finish()
            } catch (t: Throwable) {
                Log.e(TAG, "Scoring failed", t)
            }
            endWith(result)
        }

        fun endWith(result: Recognizer.Result?) {
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
            callback.onFinished(name, confidence)
            stopThread()
        }

        fun closeCamera() {
            try {
                captureSession?.close()
                captureSession = null
                cameraDevice?.close()
                cameraDevice = null
                imageReader?.close()
                imageReader = null
                previewSurface?.release()
                previewSurface = null
                previewTexture?.release()
                previewTexture = null
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
            val id = cameraId ?: return decoded
            try {
                val manager =
                    context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                val sensor = manager.getCameraCharacteristics(id)
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
                val id = cameraId ?: return true
                try {
                    val manager =
                        context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
                    val facing = manager.getCameraCharacteristics(id)
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
            val lower = range?.getLower()
            val upper = range?.getUpper()
            if (lower == null || upper == null || step == null || step.toFloat() <= 0f) {
                return values // Device does not expose exposure compensation.
            }
            val evPerIndex = step.toFloat()
            for (i in values.indices) {
                val index = Math.round(EXPOSURE_BRACKET_EV[i] / evPerIndex)
                values[i] = max(lower, min(upper, index))
            }
            Log.i(
                TAG,
                ("AE bracket indices " + values.contentToString() + ", step " + evPerIndex + " EV")
            )
            return values
        }
    }
}