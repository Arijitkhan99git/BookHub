package activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.internshala.bookhub.*
import fragment.AboutFragment
import fragment.DashboardFragment
import fragment.FavoritesFragment
import fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView

    var previousmenuitem:MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout=findViewById(R.id.drawerLayout)
        coordinatorLayout=findViewById(R.id.coordinateLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frame)
        navigationView=findViewById(R.id.navigationView)

        //caling for toolbar
        setuptoolbar()

        //to Dashboard in default at 1st
        openDashboard()

        //for Hamburgger icon state
        val actionBarDrawerToggle=ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        //for Hamburgger icon actionListner action happened
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousmenuitem!=null) {
                previousmenuitem?.isChecked=false
            }

            //selected item set the value true
            it.isChecked=true
            it.isCheckable=true
            previousmenuitem=it

            when(it.itemId)
            {
                R.id.dashboard ->
                {
                    openDashboard()
                    //caling the dashbord fun

                    drawerLayout.closeDrawers()
                    //R.id.frame ->replace with DashboardFragment()
                        //close the drawer
                }
                R.id.favorites ->
                {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FavoritesFragment()
                        )
                        .commit()
                    //set the title of app
                    supportActionBar?.title="Favourties"

                    drawerLayout.closeDrawers()
                }

                R.id.about ->
                {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            AboutFragment()
                        )
                        .commit()

                    supportActionBar?.title="About App"

                    drawerLayout.closeDrawers()
                }
            }

            return@setNavigationItemSelectedListener true
        }
    }

    fun setuptoolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title="Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /*to select home_button(Hamburger) make action we written this code
    onOptionsItemSelected(item: MenuItem?) function take the home_button(Hamburger) as menu_item
    if id_of hamburguer match with id_of menu_item ,then Drawer open
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id= item?.itemId

        if (id==android.R.id.home)
        {
            //openDrawer method open the Drawer
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openDashboard()
    {
        val fragment= DashboardFragment()
        val transaction=
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame,fragment)

        transaction.commit()
        supportActionBar?.title="Dashboard"
    }

    override fun onBackPressed()
    {
        val frag=supportFragmentManager.findFragmentById(R.id.frame)

        when(frag)
        {
            !is DashboardFragment ->openDashboard()

            else ->super.onBackPressed()
        }
    }
}

