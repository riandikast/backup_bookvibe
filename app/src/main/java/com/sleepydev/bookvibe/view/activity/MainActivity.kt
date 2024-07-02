package com.sleepydev.bookvibe.view.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
import android.view.WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.datastore.UserManager
import com.sleepydev.bookvibe.model.User
import com.sleepydev.bookvibe.utils.CustomToast
import com.sleepydev.bookvibe.viewmodel.LoginViewModel
import com.sleepydev.bookvibe.viewmodel.NetworkViewModel
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    lateinit var dataUser : List<User>
    private val networkViewModel: NetworkViewModel by viewModels()
    lateinit var token : String
    lateinit var type : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment

        val loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        loginViewModel.userToken(this).observe(this){
            token = it

        }
        loginViewModel.userType(this).observe(this){
            type = it
        }

        navController = navHostFragment.navController
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnNavigationItemSelectedListener { navigation->

                if (token == "") {
//                    if (navigation.itemId == R.id.wishlistFragment){
//                        navController.navigate(R.id.loginFragment)
//
//                    }
                    if (navigation.itemId == R.id.homeFragment){
                        navController.navigate(R.id.homeFragment)
                    }

                    else if (navigation.itemId == R.id.transactionFragment){
                        navController.navigate(R.id.loginFragment)

                    }
                    else if (navigation.itemId == R.id.accountFragment){
                        navController.navigate(R.id.loginFragment)

                    }

                } else {

                        if (type == "Seller" && navigation.itemId == R.id.accountFragment){
                                navController.navigate(R.id.sellerAccountFragment)

                        }
                        else if (type == "Buyer" && navigation.itemId == R.id.accountFragment){
                                navController.navigate(R.id.buyerAccountFragment)

                        } else if(navigation.itemId == R.id.transactionFragment){
                            navController.navigate(R.id.transactionFragment)
                        }
                        else{
                            navController.navigate(R.id.homeFragment)
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

    fun checkNetwork(){

        networkViewModel.isOnline.observe(this) { isOnline ->
            if (isOnline){

                getDataUser()

            }else{


            }
        }

    }

    fun getDataUser(){
        val loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
//        loginViewModel.userTokenObserver.observe(this) { token ->
//            if (token == "") {
//
//            }
//            else{
//                val viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
//                viewModel.getAllUserObserver.observe(this) {
//                    dataUser = it!!
//                    Log.d(it.toString(), "woi")
//                    val userManager = UserManager(this)
//                    GlobalScope.launch {
//                        for (i in dataUser.indices) {
//                            userManager.saveDataUser(
//                                dataUser[i].id,
//                                dataUser[i].name,
//                                dataUser[i].email,
//                                dataUser[i].password,
//                                dataUser[i].accountType,
//                                dataUser[i].history.toString(),
//                                dataUser[i].cart.toString(),
//                                dataUser[i].createdAt,
//                                dataUser[i].userToken,
//                                dataUser[i].balance,
//                                dataUser[i].address,
//                                dataUser[i].phoneNumber
//                            )
//                        }
//                    }
//
//                }
//
//                viewModel.getAllUser()
//            }
//        }

    }
    private fun clearGlideCache() {
        // Clear Disk Cache
        Thread {
            Glide.get(this).clearDiskCache()
        }.start()

        // Clear Memory Cache
        Glide.get(this).clearMemory()
    }


}