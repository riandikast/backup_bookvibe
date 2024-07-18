package com.sleepydev.bookvibe.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.adapter.HomeAdapter
import com.sleepydev.bookvibe.adapter.MyProductAdapter
import com.sleepydev.bookvibe.databinding.FragmentHomeBinding
import com.sleepydev.bookvibe.databinding.FragmentLoginBinding
import com.sleepydev.bookvibe.utils.CustomToast
import com.sleepydev.bookvibe.viewmodel.LoginViewModel
import com.sleepydev.bookvibe.viewmodel.NetworkViewModel
import com.sleepydev.bookvibe.viewmodel.ProductViewModel
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val networkViewModel: NetworkViewModel by viewModels()
    private val  productViewModel: ProductViewModel by viewModels()
    private val  usertViewModel: UserViewModel by viewModels()

    private val customToast = CustomToast()
    var toastShown = false
    var  preventFirstLoad = true

    private lateinit var myProductAdapter: HomeAdapter
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
        _binding =  FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNetwork()
        preventFirstLoad = false
        binding.appName.setOnClickListener {
            view.findNavController().navigate(R.id.loginFragment)
        }

    }

    fun checkNetwork(){

        networkViewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline){
                handler?.removeCallbacks(toastRunnable!!)
                preventFirstLoad = false
                getMyProduct()

            }else{
                toastShown = false
                binding.progressBar.visibility = View.VISIBLE
                binding.contentPage.visibility = View.INVISIBLE
                binding.checkdata.visibility = View.INVISIBLE

                if (!preventFirstLoad){

                    preventFirstLoad = true

                    handler = Handler(Looper.getMainLooper())
                    toastRunnable = Runnable {
                        customToast.customFailureToast(requireContext(), "No Internet Connection")
                    }

                    handler?.postDelayed(toastRunnable!!, 4000)
                }else{
                    preventFirstLoad = false
                }
            }
        }

    }
    @SuppressLint("NotifyDataSetChanged")
    fun getMyProduct(){
        usertViewModel.userID(requireContext()).observe(viewLifecycleOwner){

        }

        productViewModel.getAllProductObserver.observe(viewLifecycleOwner){
            productViewModel.getAllProductResponseCode.observe(viewLifecycleOwner){code->
                if (code == "200"){
                    binding.list.layoutManager =  GridLayoutManager(requireContext(), 2)

                    val sorted = it.sortedByDescending { it.createdAt }
                    myProductAdapter = HomeAdapter( this)
                    myProductAdapter.setProductList(sorted)
                    binding.list.adapter = myProductAdapter

                    myProductAdapter.notifyDataSetChanged()
                    binding.progressBar.visibility = View.GONE
                    binding.contentPage.visibility = View.VISIBLE

                    if (myProductAdapter.itemCount.toString() == "0"){

                        binding.checkdata.visibility = View.VISIBLE
                    }else{
                        binding.checkdata.visibility = View.GONE
                    }

                }else{

                }
            }
        }
        productViewModel.getAllProduct()


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        binding.contentPage.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE
        preventFirstLoad = true
        checkNetwork()



    }





}