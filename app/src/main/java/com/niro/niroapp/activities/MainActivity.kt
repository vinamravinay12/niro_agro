package com.niro.niroapp.activities


import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.android.material.navigation.NavigationView
import com.niro.niroapp.R
import com.niro.niroapp.databinding.ActivityMainBinding
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.models.responsemodels.UserType
import com.niro.niroapp.users.fragments.ContactsFragment
import com.niro.niroapp.utils.NIroAppConstants
import com.niro.niroapp.utils.NiroAppUtils

class MainActivity : AppCompatActivity() {

    private lateinit var bindingActivityMain : ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var mCurrentUser : User
    private lateinit var mNavController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingActivityMain = DataBindingUtil.setContentView(this,R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.nav_drawer)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val bottomNavigationView : BottomNavigationView = findViewById(R.id.bottom_nav_view)
        mNavController = findNavController(R.id.nav_host_fragment)
       
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_support, R.id.nav_about, R.id.nav_logout,R.id.navigation_home,R.id.navigation_orders,R.id.navigation_payments,
        R.id.navigation_loaders,R.id.navigation_loans),drawerLayout)
        setupActionBarWithNavController(mNavController, appBarConfiguration)
        navView.setupWithNavController(mNavController)
        bottomNavigationView.setupWithNavController(mNavController)
        bottomNavigationView.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED

        navView.setNavigationItemSelectedListener { item -> launchSelectedNavigationDrawerFragment(item) }
        bottomNavigationView.setOnNavigationItemSelectedListener { item -> launchSelectedFragment(item) }

        initializeUserProfile()

        
    }

    private fun launchSelectedNavigationDrawerFragment(item: MenuItem): Boolean {

      if(!::mNavController.isInitialized) mNavController = findNavController(R.id.nav_host_fragment)
        when(item.itemId) {
            R.id.nav_support -> mNavController.navigate(R.id.nav_support)
            R.id.nav_about -> mNavController.navigate(R.id.nav_about)
            R.id.nav_logout -> NiroAppUtils.logout(this)
        }
        return true
    }

    private fun launchHomeFragment(user: User) {
        mNavController.navigate(R.id.navigation_home)
    }


    private fun initializeUserProfile() {
        initializeCurrentUser()
        setUserDataInNavHeader(mCurrentUser)
        bindingActivityMain.appBarHome.contentHome.bottomNavView.menu.findItem(R.id.navigation_loaders).title =
            if((mCurrentUser.userType ?: "").equals(UserType.COMMISSION_AGENT.name,true)) getString(R.string.title_users) else getString(R.string.title_buyers)

        launchHomeFragment(mCurrentUser)
    }

    private fun openProfileDetails(user: User) {
        findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_profile_details, bundleOf(NIroAppConstants.ARG_CURRENT_USER to user))
    }


    private fun initializeCurrentUser() {
        if(!(this::mCurrentUser.isInitialized)) mCurrentUser = NiroAppUtils.getCurrentUser(this)
    }

    private fun setUserDataInNavHeader(user: User) {
        val headerLayout = bindingActivityMain.navView.getHeaderView(0)
        headerLayout.findViewById<TextView>(R.id.tvUserName).text = user?.fullName ?: NiroAppUtils.getCurrentUserType(user?.userType)
        headerLayout.findViewById<TextView>(R.id.tvUserNumber).text = user?.phoneNumber ?: ""

        headerLayout.findViewById<LinearLayout>(R.id.layoutMyProfile).setOnClickListener { openProfileDetails(user) }


    }


    override fun onSupportNavigateUp(): Boolean {
        return mNavController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {

    }

    private fun launchSelectedFragment(item: MenuItem): Boolean {

        initializeCurrentUser()
        when(item.itemId) {
            R.id.navigation_home -> launchHomeFragment(mCurrentUser)

            R.id.navigation_orders ->  mNavController.navigate(R.id.navigation_orders,bundleOf(NIroAppConstants.ARG_CURRENT_USER to mCurrentUser))

            R.id.navigation_payments ->  mNavController.navigate(R.id.navigation_payments,bundleOf(NIroAppConstants.ARG_CURRENT_USER to mCurrentUser))

            R.id.navigation_loaders ->  mNavController.navigate(R.id.navigation_loaders, bundleOf(NIroAppConstants.ARG_CURRENT_USER to mCurrentUser))

            R.id.navigation_loans ->  mNavController.navigate(R.id.navigation_loans,bundleOf(NIroAppConstants.ARG_CURRENT_USER to mCurrentUser))

        }

        return true

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val contactsFragment =
            supportFragmentManager.findFragmentByTag(NIroAppConstants.TAG_CONTACTS)


        if (contactsFragment != null && contactsFragment.isVisible) {
            (contactsFragment as ContactsFragment).onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }




}