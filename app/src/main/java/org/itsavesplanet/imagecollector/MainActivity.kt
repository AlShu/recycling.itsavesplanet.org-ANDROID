package org.itsavesplanet.imagecollector

import android.app.FragmentManager
import android.os.Bundle
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
import android.view.View
import android.widget.Toast


class MainActivity : AppCompatActivity() {

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
