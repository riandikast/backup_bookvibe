package com.sleepydev.bookvibe.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.adapter.HomeAdapter
import com.sleepydev.bookvibe.adapter.TransactionAdapter
import com.sleepydev.bookvibe.databinding.FragmentProductBinding
import com.sleepydev.bookvibe.databinding.FragmentRegisterBinding
import com.sleepydev.bookvibe.databinding.FragmentTransactionBinding
import com.sleepydev.bookvibe.databinding.TransactionAdapterBinding
import com.sleepydev.bookvibe.utils.CustomToast
import com.sleepydev.bookvibe.viewmodel.NetworkViewModel
import com.sleepydev.bookvibe.viewmodel.ProductViewModel
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionFragment : Fragment() {

    private var _binding:  FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private val networkViewModel: NetworkViewModel by viewModels()
    private val  productViewModel: ProductViewModel by viewModels()
    private val  userViewModel: UserViewModel by viewModels()

    private lateinit var myTransactionAdapter: TransactionAdapter

    private val customToast = CustomToast()
    var toastShown = false
    var  preventFirstLoad = true
    var currentUserID = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentTransactionBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkNetwork()

    }

    fun checkNetwork(){
        networkViewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline){

                preventFirstLoad = false
                getMyTransaction()

            }else{
                toastShown = false
                binding.progressBar.visibility = View.VISIBLE
                binding.contentPage.visibility = View.INVISIBLE

                if (!preventFirstLoad){
                    customToast.customFailureToast(requireContext(),"No Internet Connection")

                }else{
                    preventFirstLoad = false
                }
            }
        }

    }
    @SuppressLint("SuspiciousIndentation")
    fun getMyTransaction(){
        userViewModel.userID(requireContext()).observe(viewLifecycleOwner){
          currentUserID = it
            userViewModel.getCurrentUser(currentUserID)
            userViewModel.currentUserObserver.observe(viewLifecycleOwner){user->
                userViewModel.currentUserResponseCode.observe(viewLifecycleOwner){code->
                    if (code == "200"){
                        binding.list.layoutManager =
                            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


                        val sorted = user.history.sortedByDescending { it.time }


                        myTransactionAdapter = TransactionAdapter( this)
                        myTransactionAdapter.setProductList(sorted)
                        binding.list.adapter = myTransactionAdapter
                        myTransactionAdapter.notifyDataSetChanged()
                        binding.progressBar.visibility = View.GONE
                        binding.contentPage.visibility = View.VISIBLE

                        if (myTransactionAdapter.itemCount.toString() == "0"){

                            binding.checkdata.visibility = View.VISIBLE
                        }else{
                            binding.checkdata.visibility = View.GONE
                        }

                    }else{


                    }
                }
            }
        }




    }
}