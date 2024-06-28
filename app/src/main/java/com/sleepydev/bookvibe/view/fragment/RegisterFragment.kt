package com.sleepydev.bookvibe.view.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isNotEmpty
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.databinding.FragmentRegisterBinding
import com.sleepydev.bookvibe.datastore.UserManager
import com.sleepydev.bookvibe.model.User
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import kotlin.properties.Delegates


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    lateinit var registEmail: String
    lateinit var selectedAccountType: String

    lateinit var dataUser: List<User>
    lateinit var viewModel: UserViewModel
    lateinit var password: String
    lateinit var toast: String
    private var accountType = mutableListOf<String>()
    var authValid by Delegates.notNull<Boolean>()
    private lateinit var arrayAdapter: ArrayAdapter<String>
    lateinit var userManager : UserManager

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

        getDataUser()
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





            if (binding.registName.text.isNotEmpty() && binding.regisEmail.text.isNotEmpty()
                && binding.regisPassword.text.isNotEmpty()
                && binding.confirmPassword.text.isNotEmpty()
                && binding.accountType.isNotEmpty()
            ) {
                if (password == confirmpass) {
                    for (i in dataUser.indices) {
                        if (registEmail == dataUser[i].email) {
                            authValid = false
                            break
                        } else {
                            authValid = true
                        }
                    }

                    if (authValid) {
                        regisUser(name, registEmail, password, selectedAccountType)



                    } else {
                        toast = "Email Sudah Terdaftar"
                        customFailureToast(requireContext(), toast)
                    }

                } else {
                    toast = "Konfirmasi Password Tidak Sesuai"
                    customFailureToast(requireContext(), toast)
                }
            } else {
                toast = "Harap isi semua data"
                customFailureToast(requireContext(), toast)
            }
        }

    }

    fun getDataUser(){
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        viewModel.getLiveUserObserver().observe(viewLifecycleOwner) {
            dataUser = it!!


        }

        viewModel.userApi()

    }

    fun regisUser(name: String, email: String, password: String, accountType:String) {
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        viewModel.getLiveRegisObserver().observe(requireActivity()) {
            if (it != null) {
                toast = "Registrasi Berhasil"
                customSuccessToast(requireContext(), toast)
                view?.findNavController()
                    ?.navigate(R.id.action_registerFragment_to_loginFragment)
            } else {
                toast = "Registrasi Gagal"
                customFailureToast(requireContext(), toast)
            }
        }

        val balance = 100000
        viewModel.regisUser(name, email, password, accountType, balance )
    }

    fun setUpDropDownAccount(){
        val listAccountType = arrayOf("Buyer", "Seller")
        binding.dropdownAccountType.hint = "Select Account Type"
        arrayAdapter = ArrayAdapter(requireActivity(), R.layout.account_type_category, listAccountType)

        binding.dropdownAccountType.setAdapter(arrayAdapter)
        binding.dropdownAccountType.setOnItemClickListener { parent, view, position, id ->
            val selectedItem: String? = arrayAdapter.getItem(position)
            selectedAccountType = selectedItem!!




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