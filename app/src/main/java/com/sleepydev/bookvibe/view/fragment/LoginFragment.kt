package com.sleepydev.bookvibe.view.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.databinding.FragmentLoginBinding
import com.sleepydev.bookvibe.datastore.UserManager
import com.sleepydev.bookvibe.model.User
import com.sleepydev.bookvibe.utils.CustomToast
import com.sleepydev.bookvibe.viewmodel.NetworkViewModel
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    lateinit var dataUser : List<User>
    lateinit var viewModel : UserViewModel
    lateinit var email: String
    lateinit var password: String

    var authValid by Delegates.notNull<Boolean>()
    lateinit var userManager : UserManager
    private val networkViewModel: NetworkViewModel by viewModels()
    var isDataObtained = false
    var isConnect = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userManager = UserManager(requireContext())

        checkNetwork()



        binding.btnBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.btnlogin.setOnClickListener {
            if (isConnect){

                if (binding.loginemail.text.isNotEmpty() && binding.loginpassword.text.isNotEmpty()){
                    email = binding.loginemail.text.toString()
                    password = binding.loginpassword.text.toString()
                    authLogin()

                }
                else{
                    val customToast = CustomToast()
                    customToast.customFailureToast(requireContext(),"Please Fill All the Form")

                }

            }else{
                val customToast = CustomToast()
                customToast.customFailureToast(requireContext(),"No Internet Connection")

            }
        }


        binding.btnGoToRegist.setOnClickListener {
            view.findNavController().navigate(
                R.id.action_loginFragment_to_registerFragment
             )
        }
    }

    fun checkNetwork(){

        networkViewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline){
                 isConnect = true

                getDataUser()

            }else{
                isConnect = false

            }
        }

    }

    fun getDataUser(){
        viewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        viewModel.getAllUserObserver.observe(viewLifecycleOwner) {
            viewModel.getAllUserResponseCode.observe(viewLifecycleOwner){  code->
                if (code=="200"){
                    dataUser = it!!
                    isDataObtained = true
                }
            }
        }
        viewModel.getAllUser()

    }

    fun authLogin() {
            userManager = UserManager(requireActivity())
            networkViewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->
                    if (isDataObtained){
                        if(dataUser.isEmpty()){
                            val customToast = CustomToast()
                            customToast.customFailureToast(requireContext(),"Wrong Email or Password")

                        }else{
                            for (i in dataUser.indices) {
                                if (email == dataUser[i].email && password == dataUser[i].password) {
                                    GlobalScope.launch {

                                        userManager.saveDataLogin(dataUser[i].userToken!!)
                                        userManager.saveDataUser(dataUser[i].id!!.toInt(), dataUser[i].name, dataUser[i].email, dataUser[i].password, dataUser[i].accountType, dataUser[i].history.toString(), dataUser[i].cart.toString(), dataUser[i].createdAt!!, dataUser[i].userToken!!, dataUser[i].balance, dataUser[i].address, dataUser[i].phoneNumber)
                                    }

                                    val customToast = CustomToast()
                                    customToast.customSuccessToast(requireActivity(),"Login Successful")

                                    view?.findNavController()
                                        ?.navigate(R.id.action_loginFragment_to_homeFragment, null,
                                            NavOptions.Builder()
                                                .setPopUpTo(
                                                    R.id.loginFragment,
                                                    true
                                                ).build())
                                    authValid  = true
                                    activity?.recreate()
                                    break

                                }else{
                                    authValid = false
                                }

                            }

                            if (!authValid){
                                val customToast = CustomToast()
                                customToast.customFailureToast(requireContext(),"Wrong Email or Password")

                            }
                        }

                }

        }

    }



}