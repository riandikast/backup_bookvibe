package com.sleepydev.bookvibe.view.fragment

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.databinding.FragmentBuyerAccountBinding
import com.sleepydev.bookvibe.databinding.FragmentHomeBinding
import com.sleepydev.bookvibe.databinding.FragmentSellerAccountBinding
import com.sleepydev.bookvibe.databinding.TopupDialogBinding
import com.sleepydev.bookvibe.datastore.UserManager
import com.sleepydev.bookvibe.model.TopUpResponse
import com.sleepydev.bookvibe.model.UpdateResponse
import com.sleepydev.bookvibe.utils.CustomToast
import com.sleepydev.bookvibe.utils.NetworkMonitor
import com.sleepydev.bookvibe.viewmodel.LoginViewModel
import com.sleepydev.bookvibe.viewmodel.NetworkViewModel
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

@AndroidEntryPoint
class SellerAccountFragment : Fragment() {

    private var _binding: FragmentSellerAccountBinding? = null
    private val binding get() = _binding!!
    lateinit var userManager: UserManager
    var currentUserID by Delegates.notNull<Int>()
    lateinit var currentUserBalance : String
    var oldBalance by Delegates.notNull<Int>()
    var updatedBalance by Delegates.notNull<Int>()
    lateinit var topUpDialog : AlertDialog

    private val networkViewModel: NetworkViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    private val customToast = CustomToast()
    var toastShown = false
    var  preventFirstLoad = true
    lateinit var inputBinding : TopupDialogBinding

    private var handler: Handler? = null
    private var toastRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentSellerAccountBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userManager = UserManager(requireContext())

        checkNetwork()
        initTopupDialog()

        binding.editProfileCard.setOnClickListener{
            view.findNavController().navigate(
                R.id.action_sellerAccountFragment_to_editProfileFragment
            )
        }

        binding.reportProfileCard.setOnClickListener {
            view.findNavController().navigate(
                R.id.action_sellerAccountFragment_to_reportFragment
            )
        }

        binding.productProfileCard.setOnClickListener {
            view.findNavController().navigate(R.id.action_sellerAccountFragment_to_productFragment)
        }

        binding.logoutCard.setOnClickListener {
            ConfirmDialogUtils.showConfirmDialog(
                context = requireContext(),
                title = "Confirm Action",
                message = "Are you sure you want to Logout?",
                onPositiveAction = {
                    userManager = UserManager(requireContext())
                    Navigation.findNavController(view).navigate(R.id.homeFragment)
                    GlobalScope.launch {
                        userManager.deleteDataUser()
                    }
                    activity?.recreate()
                },
                onNegativeAction = {

                }
            )
        }

        binding.btnTopup.setOnClickListener {
            toastShown = false
            inputBinding.currentBalance.text = currentUserBalance
            inputBinding.textInputBalance.prefixTextView.gravity = Gravity.TOP
            inputBinding.textInputBalance.prefixTextView.textSize = 20F

            topUpDialog.show()
        }
    }

    fun checkNetwork(){
        networkViewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline){
                handler?.removeCallbacks(toastRunnable!!)
                preventFirstLoad = false
                getUserData()

            }else{
                toastShown = false
                inputBinding.fieldBalance.setText("")
                inputBinding.fieldBalance.clearFocus()
                currentUserID = 0
                oldBalance = 0
                binding.progressBar.visibility = View.VISIBLE
                binding.contentPage.visibility = View.INVISIBLE

                topUpDialog.dismiss()
                if (!preventFirstLoad){

                    handler = Handler(Looper.getMainLooper())
                    toastRunnable = Runnable {
                        customToast.customFailureToast(requireContext(), "No Internet Connection")
                    }

                    handler?.postDelayed(toastRunnable!!, 4000)
                } else{
                    preventFirstLoad = false
                }
            }
        }

    }

    fun initTopupDialog(){
        inputBinding = TopupDialogBinding.inflate(
            LayoutInflater.from(requireContext())
        )

        val inputView = inputBinding.root
        topUpDialog = AlertDialog.Builder(requireContext())
            .setView(inputView)
            .create()
        topUpDialog.setOnCancelListener {
            topUpDialog.dismiss()
            inputBinding.fieldBalance.setText("")
            inputBinding.fieldBalance.clearFocus()
        }

        inputBinding.fieldBalance.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Enable or disable the button based on whether EditText is empty
                inputBinding.btnTopup.isEnabled = !s.isNullOrEmpty()
                if (!s.isNullOrEmpty()){
                    inputBinding.btnTopup.alpha = 1F
                }else{
                    inputBinding.btnTopup.alpha = 0.6F
                }

            }
        })

        inputBinding.textInputBalance.prefixTextView.updateLayoutParams {
            height = ViewGroup.LayoutParams.MATCH_PARENT

        }
        inputBinding.btnTopup.setOnClickListener {
            val topUpAmountText = inputBinding.fieldBalance.text.toString()
            val topUpAmount = topUpAmountText.toInt()
            updatedBalance = oldBalance + topUpAmount
            val result = TopUpResponse(currentUserID, updatedBalance)
            topUpBalance(currentUserID, result)
            getUserData()
        }
    }

    fun getUserData(){
        val userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel.userID(requireContext()).observe(viewLifecycleOwner) {
            currentUserID = it
            userViewModel.getCurrentUser(it)
        }
        userViewModel.currentUserObserver.observe(viewLifecycleOwner) {
            userViewModel.currentUserResponseCode.observe(viewLifecycleOwner) { code ->
                if (code == "200") {
                    binding.progressBar.visibility = View.GONE
                    binding.contentPage.visibility = View.VISIBLE
                    binding.profileName.setText(it.name)
                    val valid = it.balance.toString()
                    currentUserBalance = "Balance Rp. ${valid}"
                    oldBalance = it.balance
                    binding.profileBalance.text = currentUserBalance

                    if (it.image != null) {
                        val rawValueToString = it.image.toString()
                        val regex = """name="(content://[^\"]+)"""".toRegex()

                        val matchResult = regex.find(rawValueToString)

                        val contentUri = matchResult?.groups?.get(1)?.value
                        val uri = Uri.parse(it.image.toString())
                        val requestOptions = RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false)
                        Glide.with(requireContext())
                            .load(uri)
                            .apply(requestOptions)
                            .into(binding.profileCircle)
                    }
                }
            }
        }

    }

    fun topUpBalance(id : Int, balance : TopUpResponse){
        val viewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        toastShown = false
        networkViewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline) {
                viewModel.balanceObserver.observe(viewLifecycleOwner) {
                    viewModel.topUpResponseCode.observe(viewLifecycleOwner) { code ->
                        if (code == "200") {
                            if (!toastShown){
                                customToast.customSuccessToast(requireContext(),"Topup Successful")
                                toastShown = true
                            }

                            val userManager = UserManager(requireContext())
                            GlobalScope.launch {
                                userManager.updateBalance(updatedBalance)
                            }
                            topUpDialog.dismiss()
                            inputBinding.fieldBalance.setText("")
                            inputBinding.fieldBalance.clearFocus()

                            getUserData()
                        } else {
                            if (!toastShown){
                                customToast.customFailureToast(requireContext(),"Topup Failed")
                                toastShown = true
                            }
                        }
                    }
                }
                viewModel.topUpBalance(id, balance)

            } else {
                customToast.customFailureToast(requireContext(),"No Internet Connection")
            }
        }

    }





}