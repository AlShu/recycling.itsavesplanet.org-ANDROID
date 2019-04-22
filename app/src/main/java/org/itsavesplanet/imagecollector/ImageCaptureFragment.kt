package org.itsavesplanet.imagecollector

//import android.app.Fragment
import android.app.Activity
import android.Manifest
import android.content.Context
import android.content.Intent
import android.support.v4.app.Fragment
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.Button
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.image_capture.view.*
import org.itsavesplanet.imagecollector.Constants.Companion.MEDIA_TYPE_IMAGE
import org.itsavesplanet.imagecollector.R.id.btnCapturePicture


class ImageCaptureFragment : Fragment() {
    var imageStoragePath: String? = null
    var activity: Activity? = null

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
        view.btnCapturePicture.setOnClickListener(
            View.OnClickListener {
                if (CameraUtils.checkPermissions(activity?.getApplicationContext())) {
                    captureImage()
                } else {
                    requestCameraPermission(MEDIA_TYPE_IMAGE, activity as Activity)
                }
            }
        )
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
                            // capture picture
                            captureImage()
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
     * Capturing Camera Image will launch camera app requested image capture
     */
    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE)
        if (file != null) {
            imageStoragePath = file.absolutePath
        }

//        val fileUri = CameraUtils.getOutputMediaFileUri(activity?.getApplicationContext(), file)
        val fileUri = CameraUtils.getOutputMediaFileUri(activity as Context, file)

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)

        // start the image capture Intent
        startActivityForResult(intent, Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
    }

    /**
     * Alert dialog to navigate to app settings
     * to enable necessary permissions
     */
    private fun showPermissionsAlert() {
//        val context = activity?.getApplicationContext() as Context
        val builder = AlertDialog.Builder(activity as Context)
        builder.setTitle("Permissions required!")
            .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
            .setPositiveButton(
                "GOTO SETTINGS"
            ) { dialog, which -> CameraUtils.openSettings(context) }
            .setNegativeButton("CANCEL") { dialog, which -> }.show()
    }
}
