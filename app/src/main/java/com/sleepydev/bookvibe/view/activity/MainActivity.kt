package com.sleepydev.bookvibe.view.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.viewmodel.LoginViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
//            window.attributes.layoutInDisplayCutoutMode = LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
//        }
        navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnNavigationItemSelectedListener { navigation->
            val loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
            loginViewModel.userToken(this).observe(this) { token ->
                if (token == "") {

                    if (navigation.itemId == R.id.wishlistFragment){
                        //navigate to Fragment1 or Fragment2
                        navController.navigate(R.id.loginFragment)

                    }
                    else if (navigation.itemId == R.id.transactionFragment){
                        navController.navigate(R.id.loginFragment)

                    }
                    else if (navigation.itemId == R.id.accountFragment){
                        navController.navigate(R.id.loginFragment)

                    }

                } else {

                    loginViewModel.userType(this).observe(this) { type ->
                        Log.d(navigation.toString(), "woi")
                        if (type == "Seller" && navigation.itemId == R.id.accountFragment){
                                navController.navigate(R.id.sellerAccountFragment)

                        }
                        else if (type == "Buyer" && navigation.itemId == R.id.accountFragment){
                                navController.navigate(R.id.buyerAccountFragment)
                        }
                        else{
                            NavigationUI.onNavDestinationSelected(navigation, navController)
                        }
                    }


                }
            }

            true
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            //check fragment name is "home" or not

            if (destination.id == R.id.homeFragment) {
                bottomNavigationView.visibility = View.VISIBLE
            }

            else if (destination.id == R.id.transactionFragment) {
                bottomNavigationView.visibility = View.VISIBLE
            }

            else if (destination.id == R.id.buyerAccountFragment) {
                bottomNavigationView.visibility = View.VISIBLE
            }

            else if (destination.id == R.id.sellerAccountFragment) {
                bottomNavigationView.visibility = View.VISIBLE
            }

            else if (destination.id == R.id.wishlistFragment) {
                bottomNavigationView.visibility = View.VISIBLE
            }

            else {
                bottomNavigationView.visibility = View.GONE
            }
        }
    }
}