package com.pzpg.ogr

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.material.navigation.NavigationView
import com.pzpg.ogr.authorize_google.EXTRA_ACTION
import com.pzpg.ogr.authorize_google.SIGN_LAYOUT
import com.pzpg.ogr.authorize_google.SignInFragmentActivity
import com.pzpg.ogr.graph.FruchtermanReingoldActivity
import java.io.File
import java.io.IOException



class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "activity has been created")

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_history, R.id.nav_public), drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.nav_menu, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



    fun signIn(view: View){

        Intent(this, SignInFragmentActivity::class.java).also { signInActivity ->
            signInActivity.putExtra(EXTRA_ACTION, SIGN_LAYOUT)
            startActivity(signInActivity)
        }
    }

    fun goGraphActivity(view: View){
        Intent(this, FruchtermanReingoldActivity::class.java).also { graphActivity->
            graphActivity.putExtra("EXTRA_GRAPH_NAME", "test_graph_538587133.graphml")
            graphActivity.putExtra("EXTRA_GRAPH_EXTENSION", "graphml")
            startActivity(graphActivity)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}
