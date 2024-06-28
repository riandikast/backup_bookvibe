package com.sleepydev.bookvibe.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.databinding.FragmentBuyerAccountBinding
import com.sleepydev.bookvibe.databinding.FragmentHomeBinding
import com.sleepydev.bookvibe.datastore.UserManager
import com.sleepydev.bookvibe.viewmodel.LoginViewModel
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class BuyerAccountFragment : Fragment() {

    private var _binding: FragmentBuyerAccountBinding? = null
    private val binding get() = _binding!!
    lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentBuyerAccountBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        getUserData()

        binding.editProfileCard.setOnClickListener{
            view.findNavController().navigate(
                R.id.action_buyerAccountFragment_to_editProfileFragment
            )
        }

        binding.logoutCard.setOnClickListener {
            userManager = UserManager(requireActivity())
            Navigation.findNavController(view).navigate(R.id.homeFragment)
            GlobalScope.launch {
                userManager.deleteDataUser()
            }
        }
    }

    fun getUserData(){
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel.userName(requireActivity()).observe(viewLifecycleOwner) {
            binding.profileName.setText(it)

        }

        userViewModel.userBalance(requireActivity()).observe(viewLifecycleOwner) {
            val getBalance = "Balance Rp. $it"
            binding.profileBalance.setText(getBalance)

        }
    }


}