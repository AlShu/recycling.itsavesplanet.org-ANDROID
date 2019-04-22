package org.itsavesplanet.imagecollector

import android.Manifest
import android.app.FragmentManager
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
//import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.Toast

import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.itsavesplanet.imagecollector.Constants.Companion
import org.itsavesplanet.imagecollector.Constants.Companion.CAMERA_CAPTURE_IMAGE_REQUEST_CODE
import org.itsavesplanet.imagecollector.Constants.Companion.MEDIA_TYPE_IMAGE


class MainActivity : AppCompatActivity() {
//    var imageStoragePath: String? = null

//    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)

//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }

        // Example of a call to a native method
        //        sample_text.text = stringFromJNI()

//        drawerLayout = findViewById(R.id.drawer_layout)
//
//        val navigationView: NavigationView = findViewById(R.id.nav_view)
        nav_view.setNavigationItemSelectedListener { menuItem ->
            // close drawer when item is tapped
            drawer_layout.closeDrawers()
            // set item as selected to persist highlight
            menuItem.isChecked = true

            var fragment: Fragment? = null
            val id = menuItem.itemId
            if (id == R.id.nav_camera) {
                Toast.makeText(getApplicationContext(), "Camera is clicked", Toast.LENGTH_SHORT).show()
                // Handle the camera action
                fragment = ImageCaptureFragment() as Fragment
            } else if (id == R.id.nav_gallery) {
                Toast.makeText(getApplicationContext(), "Gallery is clicked", Toast.LENGTH_SHORT).show()
                fragment = GalleryFragment() as Fragment
            }
            val fragmentManager = getSupportFragmentManager()
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            true
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
//
//    /**
//     * Requesting permissions using Dexter library
//     */
//    private fun requestCameraPermission(type: Int) {
//        Dexter.withActivity(this)
//            .withPermissions(
//                Manifest.permission.CAMERA,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.RECORD_AUDIO
//            )
//            .withListener(object : MultiplePermissionsListener {
//                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
//                    if (report.areAllPermissionsGranted()) {
//
//                        if (type == MEDIA_TYPE_IMAGE) {
//                            // capture picture
//                            captureImage()
//                        }
//
//                    } else if (report.isAnyPermissionPermanentlyDenied()) {
//                        showPermissionsAlert()
//                    }
//                }
//
//                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
//                    token.continuePermissionRequest()
//                }
//            }).check()
//    }
//
//
//    /**
//     * Capturing Camera Image will launch camera app requested image capture
//     */
//    private fun captureImage() {
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//
//        val file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE)
//        if (file != null) {
//            imageStoragePath = file.absolutePath
//        }
//
//        val fileUri = CameraUtils.getOutputMediaFileUri(applicationContext, file)
//
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
//
//        // start the image capture Intent
//        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
//    }
//
//    /**
//     * Alert dialog to navigate to app settings
//     * to enable necessary permissions
//     */
//    private fun showPermissionsAlert() {
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Permissions required!")
//            .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
//            .setPositiveButton(
//                "GOTO SETTINGS"
//            ) { dialog, which -> CameraUtils.openSettings(this@MainActivity) }
//            .setNegativeButton("CANCEL") { dialog, which -> }.show()
//    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
