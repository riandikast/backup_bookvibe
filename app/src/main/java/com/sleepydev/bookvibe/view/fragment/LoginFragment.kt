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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.databinding.FragmentLoginBinding
import com.sleepydev.bookvibe.datastore.UserManager
import com.sleepydev.bookvibe.model.User
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.properties.Delegates


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    lateinit var dataUser : List<User>
    lateinit var viewModel : UserViewModel
    lateinit var email: String
    lateinit var password: String
    lateinit var toast : String
    var authValid by Delegates.notNull<Boolean>()
    lateinit var userManager : UserManager

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
        getDataUser()


        binding.btnBack.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.btnlogin.setOnClickListener {
            if (binding.loginemail.text.isNotEmpty() && binding.loginpassword.text.isNotEmpty()){
                email = binding.loginemail.text.toString()
                password = binding.loginpassword.text.toString()

                authLogin()

            }
            else{
                toast = "Harap Isi Semua Data"
                customFailureToast(requireContext(), toast)
            }
        }


        binding.btnGoToRegist.setOnClickListener {
            view.findNavController().navigate(
                R.id.action_loginFragment_to_registerFragment
             )
        }
    }

    fun getDataUser(){
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        viewModel.getLiveUserObserver().observe(viewLifecycleOwner) {
            dataUser = it!!
            Log.d(it.toString(), "woi")

        }

        viewModel.userApi()

    }

    fun authLogin() {
        userManager = UserManager(requireContext())

        for (i in dataUser.indices) {

            if (email == dataUser[i].email && password == dataUser[i].password) {
                GlobalScope.launch {

                    userManager.saveDataLogin(dataUser[i].userToken)
                    userManager.saveDataUser(dataUser[i].id, dataUser[i].name, dataUser[i].email, dataUser[i].password, dataUser[i].accountType, dataUser[i].history.toString(), dataUser[i].cart.toString(), dataUser[i].createdAt, dataUser[i].userToken, dataUser[i].balance)
                }

                toast = "Login Berhasil"
                customSuccessToast(requireContext(), toast)

                view?.findNavController()
                    ?.navigate(R.id.action_loginFragment_to_homeFragment, null,
                        NavOptions.Builder()
                            .setPopUpTo(
                                R.id.loginFragment,
                                true
                            ).build())
                authValid  = true
                break

            }else{
                authValid = false


            }

        }
        if (!authValid){
            toast = "Email atau Password Salah"
            customFailureToast(requireContext(), toast)
        }
    }

    fun customFailureToast(context: Context?, msg: String?) {
        val inflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(R.layout.error_toast, null)
        val text = layout.findViewById<View>(R.id.errortext) as? TextView
        text?.text = msg
        text?.setPadding(20, 0, 20, 0)
        text?.textSize = 22f
        text?.setTextColor(Color.WHITE)
        val toast = Toast(context)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        layout.setBackgroundColor(Color.DKGRAY)
        toast.setView(layout)
        toast.show()
    }

    fun customSuccessToast(context: Context?, msg: String?) {
        val inflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(R.layout.success_toast, null)
        val text = layout.findViewById<View>(R.id.successtext) as? TextView
        text?.text = msg
        text?.setPadding(20, 0, 20, 0)
        text?.textSize = 22f
        text?.setTextColor(Color.WHITE)
        val toast = Toast(context)
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        toast.duration = Toast.LENGTH_SHORT
        layout.setBackgroundColor(Color.DKGRAY)
        toast.setView(layout)
        toast.show()
    }

}