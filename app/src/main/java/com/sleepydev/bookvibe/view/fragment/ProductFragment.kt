package com.sleepydev.bookvibe.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.adapter.MyProductAdapter
import com.sleepydev.bookvibe.databinding.FragmentLoginBinding
import com.sleepydev.bookvibe.databinding.FragmentProductBinding
import com.sleepydev.bookvibe.model.Product
import com.sleepydev.bookvibe.utils.CustomToast
import com.sleepydev.bookvibe.viewmodel.LoginViewModel
import com.sleepydev.bookvibe.viewmodel.NetworkViewModel
import com.sleepydev.bookvibe.viewmodel.ProductViewModel
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates


@AndroidEntryPoint
class ProductFragment : Fragment(), MyProductAdapter.RefreshCallback {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var myProductAdapter: MyProductAdapter
    private val networkViewModel: NetworkViewModel by viewModels()
    private val  productViewModel: ProductViewModel by viewModels()
    private val  usertViewModel: UserViewModel by viewModels()

    private val customToast = CustomToast()
    var toastShown = false
    var  preventFirstLoad = true

    var currentSeller by Delegates.notNull<Int>()
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
        _binding =  FragmentProductBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNetwork()
        binding.btnAddProduct.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_productFragment_to_addProductFragment)
        }

        binding.btnBackProfile.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
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
                binding.emptyList.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.VISIBLE
                binding.contentPage.visibility = View.INVISIBLE

                if (!preventFirstLoad){
                    handler = Handler(Looper.getMainLooper())
                    toastRunnable = Runnable {
                        customToast.customFailureToast(requireContext(), "No Internet Connection")
                    }

                    handler?.postDelayed(toastRunnable!!, 4000)
                }else{
                    preventFirstLoad = false
                }
                if (myProductAdapter.getDialog().isShowing){
                    myProductAdapter.getDialog().dismiss()
                }
            }
        }

    }
    fun getMyProduct(){
        usertViewModel.userID(requireContext()).observe(viewLifecycleOwner){
            currentSeller = it
        }


        productViewModel.getAllProductObserver.observe(viewLifecycleOwner){
            productViewModel.getAllProductResponseCode.observe(viewLifecycleOwner){code->

                if (code == "200"){
                    binding.list.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    val sortedID = it.filter { it.sellerID == currentSeller }
                    val sorted = sortedID.sortedByDescending { it.createdAt }
                    myProductAdapter = MyProductAdapter( this)
                    myProductAdapter.setProductList(sorted)
                    binding.list.adapter = myProductAdapter
                    myProductAdapter.notifyDataSetChanged()
                    binding.progressBar.visibility = View.GONE
                    binding.contentPage.visibility = View.VISIBLE

                    if (myProductAdapter.itemCount.toString() == "0"){

                        binding.emptyList.visibility = View.VISIBLE
                    }else{
                        binding.emptyList.visibility = View.GONE
                    }
                }else{

                }
            }
        }
        productViewModel.getAllProduct()


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRefreshRecyclerView() {
        checkNetwork()
    }



}