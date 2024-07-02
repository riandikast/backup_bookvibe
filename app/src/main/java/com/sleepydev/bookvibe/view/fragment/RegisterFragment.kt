package com.sleepydev.bookvibe.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isNotEmpty
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.databinding.FragmentRegisterBinding
import com.sleepydev.bookvibe.datastore.UserManager
import com.sleepydev.bookvibe.model.User
import com.sleepydev.bookvibe.utils.CustomToast
import com.sleepydev.bookvibe.viewmodel.NetworkViewModel
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    lateinit var registEmail: String
    lateinit var selectedAccountType: String

    lateinit var dataUser: List<User>
    lateinit var viewModel: UserViewModel
    lateinit var password: String
    private val networkViewModel: NetworkViewModel by viewModels()
    var authValid by Delegates.notNull<Boolean>()
    private lateinit var arrayAdapter: ArrayAdapter<String>
    var isDataObtained = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedAccountType = ""
        checkNetwork()
        setUpDropDownAccount()
        binding.btnBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.btnBackToLogin.setOnClickListener {
            view.findNavController().navigate(
                R.id.action_registerFragment_to_loginFragment
            )
        }

        binding.btnRegist.setOnClickListener {
            val name = binding.registName.text.toString()
            registEmail = binding.regisEmail.text.toString()
            password = binding.regisPassword.text.toString()
            val confirmpass = binding.confirmPassword.text.toString()


            networkViewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->
                if (isOnline){
                    if (isDataObtained){
                        if (binding.registName.text.isNotEmpty() && binding.regisEmail.text.isNotEmpty()
                            && binding.regisPassword.text.isNotEmpty()
                            && binding.confirmPassword.text.isNotEmpty()
                            && selectedAccountType != ""
                        ) {
                            if (password == confirmpass) {

                                if(dataUser.isEmpty()){
                                    authValid = true

                                }else{
                                    for (i in dataUser.indices) {
                                        if (registEmail == dataUser[i].email) {
                                            authValid = false
                                            val customToast = CustomToast()
                                            customToast.customFailureToast(requireContext(),"Email Already Registered")

                                            break
                                        } else {
                                            authValid = true


                                        }
                                    }
                                }
                                if(isValidEmail(registEmail)){
                                    if(authValid){
                                        regisUser(name, registEmail, password, selectedAccountType)
                                    }
                                }else{
                                    val customToast = CustomToast()
                                    customToast.customFailureToast(requireContext(),"Email Not Valid")


                                }


                            } else {
                                val customToast = CustomToast()
                                customToast.customFailureToast(requireContext(),"Confirm password didn't match")

                            }
                        } else {
                            val customToast = CustomToast()
                            customToast.customFailureToast(requireContext(),"Please Fill All the Form")

                        }
                    }else{
                        val customToast = CustomToast()
                        customToast.customFailureToast(requireContext(),"Register Failed")

                    }


                }else{

                    val customToast = CustomToast()
                    customToast.customFailureToast(requireContext(),"No Internet Connection")


                }
            }

        }

        binding.dropdownAccountType.setOnClickListener {
            binding.registName.clearFocus()
            binding.confirmPassword.clearFocus()
            binding.regisPassword.clearFocus()
            binding.regisEmail.clearFocus()
            it.hideKeyboard()

        }

    }

    fun checkNetwork(){

        networkViewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline){

                getDataUser()

            }else{


            }
        }

    }
    fun getDataUser(){
        viewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        viewModel.getAllUserObserver.observe(viewLifecycleOwner) {
            viewModel.getAllUserResponseCode.observe(viewLifecycleOwner){code->


                if(code=="200"){

                    dataUser = it!!
                    isDataObtained = true
                }


            }


        }

        viewModel.getAllUser()

    }

    @SuppressLint("SuspiciousIndentation")
    fun regisUser(name: String, email: String, password: String, accountType:String) {
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

                viewModel.registerObserver.observe(viewLifecycleOwner) {
                    viewModel.registerResponseCode.observe(viewLifecycleOwner){code->

                        if (code ==  "201") {
                            val customToast = CustomToast()
                            customToast.customSuccessToast(requireContext(),"Register Succesful")

                            view?.findNavController()
                                ?.navigate(R.id.action_registerFragment_to_loginFragment)

                        } else {


                        }
                    }

                }

                val balance = 0
                viewModel.register(name, email, password, accountType, balance )




    }

    fun setUpDropDownAccount(){
        val listAccountType = arrayOf("Buyer", "Seller")
        binding.dropdownAccountType.hint = "Select Account Type"
        arrayAdapter = ArrayAdapter(requireContext(), R.layout.account_type_category, listAccountType)

        binding.dropdownAccountType.setAdapter(arrayAdapter)
        binding.dropdownAccountType.setOnItemClickListener { parent, view, position, id ->
            val selectedItem: String? = arrayAdapter.getItem(position)
            selectedAccountType = selectedItem!!




        }

    }


    fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}