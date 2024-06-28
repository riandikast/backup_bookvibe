package com.sleepydev.bookvibe.view.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.databinding.FragmentEditProfileBinding
import com.sleepydev.bookvibe.databinding.FragmentHomeBinding
import com.sleepydev.bookvibe.model.UpdateResponse
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import kotlin.properties.Delegates


class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    lateinit var toast : String
    var currentUserID by Delegates.notNull<Int>()
    lateinit var dataUser : UpdateResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentEditProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserData()
        binding.btnSaveChanges.setOnClickListener {
            val newName = binding.nameText.text.toString()
            val newAddress = binding.addressText.text.toString()
            val newPhone = binding.phoneText.text.toString()
            dataUser = UpdateResponse(currentUserID, newName, newAddress, newPhone )
            updateDataUser(currentUserID, dataUser)
        }
    }

    fun getUserData(){
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel.userName(requireActivity()).observe(viewLifecycleOwner) {
            binding.nameText.setText(it)

        }

        userViewModel.userID(requireActivity()).observe(viewLifecycleOwner) {
            currentUserID = it

        }

    }

    fun updateDataUser(id : Int, dataUser : UpdateResponse){
        val viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        viewModel.getLiveUpdateObserver().observe(requireActivity()) {
            if (it != null) {
                toast = " Berhasil Update Data"
                customSuccessToast(requireContext(), toast)
            } else {
                toast = "Gagal Update Data"
                customFailureToast(requireContext(), toast)
            }
        }
        viewModel.updateDataUser(id, dataUser)
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