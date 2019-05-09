/* many thanks
https://inducesmile.com/android/android-camera2-api-example-tutorial/
*/
package org.itsavesplanet.imagecollector

//import android.app.Fragment
import android.app.Activity
import android.Manifest
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.support.v4.app.Fragment
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.image_capture.view.*
import org.itsavesplanet.imagecollector.Constants.Companion.CAMERA_CAPTURE_IMAGE_REQUEST_CODE
import org.itsavesplanet.imagecollector.Constants.Companion.MEDIA_TYPE_IMAGE
import org.itsavesplanet.imagecollector.R.id.btnCapturePicture
import android.os.HandlerThread
//import android.hardware.camera2.CaptureRequest
//import android.hardware.camera2.CameraCaptureSession
//import android.hardware.camera2.CameraDevice
import android.media.ImageReader
import android.os.Handler
import android.util.Log
import android.util.Size
import android.view.Surface.ROTATION_270
//import sun.text.normalizer.UTF16.append
import android.view.Surface.ROTATION_180
import android.view.Surface.ROTATION_90
import android.view.Surface.ROTATION_0
import android.util.SparseIntArray
import android.view.TextureView
import android.view.Surface
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.SensorManager
import android.media.Image
import android.os.Environment
import android.support.v4.app.ActivityCompat
import org.itsavesplanet.imagecollector.sensors.Accelerometer
import org.itsavesplanet.imagecollector.sensors.Gyroscope
import org.itsavesplanet.imagecollector.sensors.LinearAccelerometer
import java.io.*
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.abs


class ImageCaptureFragment : Fragment() {
    var imageStoragePath: String? = null
    var activity: Activity? = null
    var sessionUid: String? = null
    var imagesCnt: Int = 0
    private val TAG = "AndroidCameraApi"
    private var textureView: TextureView? = null
    private var textureListener: TextureView.SurfaceTextureListener? = null


    val ORIENTATIONS = object : SparseIntArray(4) {
        init {
            this.append(Surface.ROTATION_0, 90)
            this.append(Surface.ROTATION_90, 0)
            this.append(Surface.ROTATION_180, 270)
            this.append(Surface.ROTATION_270, 180)
        }
    }


    private val cameraId: String? = null
    private var cameraDevice: CameraDevice? = null
    private var cameraCaptureSessions: CameraCaptureSession? = null
    private var captureRequest: CaptureRequest? = null
    private var captureRequestBuilder: CaptureRequest.Builder? = null
    private val imageDimension: Size? = null
    private var imageReader: ImageReader? = null
    private val file: File? = null
    //    private val REQUEST_CAMERA_PERMISSION = 200
//    private val mFlashSupported: Boolean = false
    private var mBackgroundHandler: Handler? = null
    private var mBackgroundThread: HandlerThread? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.image_capture, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val btnCapturePicture = findViewById(R.id.btnCapturePicture)
//        btnCapturePicture.setOnClickListener { Log.d(TAG, "onViewCreated(): hello world"); }

        /**
         * Capture image on button click
         */
//        view.findViewById(R.id.btnCapturePicture).setOnClickListener(
//        setContentView(R.layout.activity_main)

        activity = getActivity()
        textureView = view.textureView
        textureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                // Transform you image captured size according to the surface width and height
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
            }
        }


        genSessionUid()
        val context = activity!!.getApplicationContext()
        if (!CameraUtils.checkPermissions(activity?.getApplicationContext())) {
            requestCameraPermission(MEDIA_TYPE_IMAGE, activity as Activity)
        }
        view.textureView.setSurfaceTextureListener(
            textureListener
        )
        val mSensorManager = activity?.getSystemService(SENSOR_SERVICE) as SensorManager
        val accelerometer =
            Accelerometer(
                mSensorManager,
                context,
                CameraUtils.getOutputMediaFile("accelerometer", sessionUid, Constants.DATA_EXTENSION)
            )
        val gyroscope = Gyroscope(
            mSensorManager,
            context,
            CameraUtils.getOutputMediaFile("gyroscope", sessionUid, Constants.DATA_EXTENSION)
        )
        val linearAccelerometer = LinearAccelerometer(
            mSensorManager,
            context,
            CameraUtils.getOutputMediaFile("linear_accelerometer", sessionUid, Constants.DATA_EXTENSION)
        )
        view.btnCapturePicture.setOnClickListener(
            View.OnClickListener {
                if (CameraUtils.checkPermissions(context)) {
                    if (imagesCnt == 0) {
                        accelerometer.startSimulation()
                        linearAccelerometer.startSimulation()
                        gyroscope.startSimulation()
                    }
                    imagesCnt++
                    view.btnCapturePicture.text = imagesCnt.toString()
                    takePicture()
                } else {
                    requestCameraPermission(MEDIA_TYPE_IMAGE, activity as Activity)
                }
            }
        )

        view.btnStopCapturePicture.setOnClickListener(
            View.OnClickListener {
                view.btnCapturePicture.text = "Processing"
                accelerometer.stopSimulation()
                linearAccelerometer.stopSimulation()
                gyroscope.stopSimulation()
            }
        )
    }

    fun genSessionUid() {
        sessionUid = UUID.randomUUID().toString()
    }


    /**
     * Requesting permissions using Dexter library
     */
    private fun requestCameraPermission(type: Int, activity: Activity) {
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {

                        if (type == MEDIA_TYPE_IMAGE) {
//                            takePicture()
                        }

                    } else if (report.isAnyPermissionPermanentlyDenied()) {
                        showPermissionsAlert()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }


    /**
     * Alert dialog to navigate to app settings
     * to enable necessary permissions
     */
    private fun showPermissionsAlert() {
        val builder = AlertDialog.Builder(activity as Context)
        builder.setTitle("Permissions required!")
            .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
            .setPositiveButton(
                "GOTO SETTINGS"
            ) { dialog, which -> CameraUtils.openSettings(context) }
            .setNegativeButton("CANCEL") { dialog, which -> }.show()
    }

    /**
     * Activity result method will be called after closing the camera
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(activity as Context, imageStoragePath)

                // successfully captured the image
                // display it in image view
                previewCapturedImage()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(
                    activity as Context,
                    "User cancelled image capture", Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                // failed to capture image
                Toast.makeText(
                    activity as Context,
                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }


    /**
     * Display image from gallery
     */
    private fun previewCapturedImage() {
        try {
//            // hide video preview
//            txtDescription.setVisibility(View.GONE)
//            videoPreview.setVisibility(View.GONE)
//
//            imgPreview.setVisibility(View.VISIBLE)
//
//            val bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath)
//
//            imgPreview.setImageBitmap(bitmap)

        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

    }

    protected fun startBackgroundThread() {
        val mbt = HandlerThread("Camera Background")
        mbt.start()
        mBackgroundThread = mbt
        mBackgroundHandler = Handler(mbt.looper)
    }

    protected fun stopBackgroundThread() {
        try {
            mBackgroundThread?.quitSafely()
            mBackgroundThread?.join()
            mBackgroundThread = null
            mBackgroundHandler = null
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    data class Resolution(val width: Int, val height: Int)

    private fun getImageSize(): Resolution {
        val camDev = cameraDevice
        val manager = activity?.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val characteristics = manager.getCameraCharacteristics(camDev?.getId())
        var jpegSizes: Array<Size>? = null
        if (characteristics != null) {
            jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                .getOutputSizes(ImageFormat.JPEG)
        }
        var width = 640
        var height = 480
        var size: Size? = null
        var minError: Int? = null
        if (jpegSizes != null && 0 < jpegSizes?.size) {
            for (candidate in jpegSizes) {
                val wCandidate = candidate.getWidth()
                val hCandidate = candidate.getHeight()
                val error = abs(wCandidate * hCandidate - width * height)
                if (minError == null || minError > error) {
                    minError = error
                    size = candidate
                }
            }
        }
        width = size?.width ?: width
        height = size?.height ?: height

        return Resolution(width, height)
    }

    private fun takePicture() {
        val camDev = cameraDevice
        if (null == camDev) {
            Log.e(TAG, "cameraDevice is null");
            return
        }
        try {
            var (width, height) = getImageSize()
            val reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            val outputSurfaces = ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(Surface(textureView?.getSurfaceTexture()));
            val captureBuilder = camDev.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Orientation
            val rotation = activity?.getWindowManager()?.getDefaultDisplay()?.getRotation() ?: 0;
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation))

            val file = CameraUtils.getOutputMediaFile("img.$imagesCnt", sessionUid, Constants.IMAGE_EXTENSION)
            if (file != null) {
                imageStoragePath = file.absolutePath
            }
            val readerListener = object : ImageReader.OnImageAvailableListener {
                override fun onImageAvailable(reader: ImageReader) {
                    var image: Image? = null
                    try {
                        image = reader.acquireLatestImage();
                        val buffer: ByteBuffer = image.getPlanes()[0].getBuffer()
                        save(buffer)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        if (image != null) {
                            image.close()
                        }
                    }
                }

                fun save(bytes: ByteBuffer) {
                    var output: OutputStream? = null
                    try {
                        val output = FileOutputStream(file)
                        output.getChannel().write(bytes)
                    } finally {
                        if (null != output) {
                            output.close()
                        }
                    }
                }
            }

            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);
            val captureListener = object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(activity, "Saved:" + file, Toast.LENGTH_SHORT).show()
                    createCameraPreview()
                }
            }
            val callBack = object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler)
                    } catch (e: CameraAccessException) {
                        e.printStackTrace()
                    }
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    1 + 1
                }
            }
            camDev.createCaptureSession(outputSurfaces, callBack, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }


    fun openCamera() {
        val act = activity
        if (act != null) {
            val manager = act.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            Log.e(TAG, "is camera open");
            try {
                val cameraId = manager.getCameraIdList()[0];
                val characteristics: CameraCharacteristics = manager.getCameraCharacteristics(cameraId)
                val map: StreamConfigurationMap =
                    characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                assert(map != null)
                val context = act.getApplicationContext()
//            val imageDimension = map.getOutputSizes(SurfaceTexture.class)[0]
                // Add permission for camera and let user grant the permission
                if (
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
//                ActivityCompat.requestPermissions(AndroidCameraApi.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                    return
                }

                manager.openCamera(cameraId, stateCallback, null)

            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
            Log.e(TAG, "openCamera X");
        }
    }

    private val stateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened")
            cameraDevice = camera
            createCameraPreview()
        }

        override fun onDisconnected(camera: CameraDevice) {
            cameraDevice?.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            cameraDevice?.close()
            cameraDevice = null
        }
    }

    protected fun createCameraPreview() {

        try {
            val texture: SurfaceTexture? = textureView?.getSurfaceTexture();
            val width = imageDimension?.getWidth() ?: 100
            val height = imageDimension?.getHeight() ?: 100
            texture?.setDefaultBufferSize(width, height)
            val surface: Surface = Surface(texture)

            val cbr = cameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            cbr?.addTarget(surface);
            cameraDevice?.createCaptureSession(
                Arrays.asList(surface), object : CameraCaptureSession.StateCallback() {
                    override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                        //The camera is already closed
                        if (null == cameraDevice) {
                            return
                        }
                        captureRequestBuilder = cbr
                        // When the session is ready, we start displaying the preview.
                        cameraCaptureSessions = cameraCaptureSession
//                        val rotation = activity?.getWindowManager()?.getDefaultDisplay()?.getRotation() ?: 0
//                        val rotMatrix = getRotationMatrix(rotation, width.toFloat(), height.toFloat())
//                        textureView?.setTransform(rotMatrix)
                        updatePreview()
                    }

                    override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                        Toast.makeText(activity, "Configuration change", Toast.LENGTH_SHORT).show();
                    }
                },
                null
            )
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun closeCamera() {
        val camDev = cameraDevice
        if (null != camDev) {
            camDev.close()
            cameraDevice = null
        }
        val ir = imageReader
        if (null != ir) {
            ir.close()
            imageReader = null
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume")
        startBackgroundThread()
        if (textureView?.isAvailable() ?: false) {
            openCamera()
        } else {
            textureView?.setSurfaceTextureListener(textureListener)
        }
    }

    override fun onPause() {
        Log.e(TAG, "onPause")
//        closeCamera();
        stopBackgroundThread()
        super.onPause()
    }

    protected fun updatePreview() {
        if (null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return")
        }
        captureRequestBuilder?.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO)
        try {
            cameraCaptureSessions?.setRepeatingRequest(captureRequestBuilder?.build(), null, mBackgroundHandler)
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }

    }

    fun getRotationMatrix(mDisplayOrientation: Int, width: Float, height: Float): Matrix {
        val matrix = Matrix();
        val matrix0 = floatArrayOf(
            0f, 0f, // top left
            width, 0f, // top right
            0f, height, // bottom left
            width, height
        )
        val matrix1 = floatArrayOf(
            0f, height, // top left
            0f, 0f, // top right
            width, height, // bottom left
            width, 0f
        )

        val matrix2 = floatArrayOf(
            width, 0f, // top left
            width, height, // top right
            0f, 0f, // bottom left
            0f, height
        )

        if (mDisplayOrientation % 180 == 90) {
            matrix.setPolyToPoly(
                matrix0,
                0,
                (if (mDisplayOrientation == 90) matrix1 else matrix2),
                0,
                4
            )
        } else if (mDisplayOrientation == 180) {
            matrix.postRotate(180f, width, height)
        }
        return matrix
    }

}
