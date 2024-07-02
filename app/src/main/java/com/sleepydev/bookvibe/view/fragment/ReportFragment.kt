package com.sleepydev.bookvibe.view.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.adapter.HomeAdapter
import com.sleepydev.bookvibe.adapter.MyProductAdapter
import com.sleepydev.bookvibe.databinding.FragmentHomeBinding
import com.sleepydev.bookvibe.databinding.FragmentReportBinding
import com.sleepydev.bookvibe.utils.CustomToast
import com.sleepydev.bookvibe.viewmodel.NetworkViewModel
import com.sleepydev.bookvibe.viewmodel.ProductViewModel
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates


@AndroidEntryPoint
class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private val networkViewModel: NetworkViewModel by viewModels()
    private val  productViewModel: ProductViewModel by viewModels()
    private val  usertViewModel: UserViewModel by viewModels()

    private val customToast = CustomToast()
    var toastShown = false
    var  preventFirstLoad = true
    var currentSeller by Delegates.notNull<Int>()
    var inventoryValuation by Delegates.notNull<Int>()
    var aov by Delegates.notNull<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentReportBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkNetwork()

        binding.btnBackProfile.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

    }

    fun checkNetwork(){
        networkViewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline){

                preventFirstLoad = false
                getMyProduct()

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

    fun getMyProduct(){
        usertViewModel.userID(requireContext()).observe(viewLifecycleOwner){
            currentSeller = it
        }
        inventoryValuation = 0
        productViewModel.getAllProductObserver.observe(viewLifecycleOwner){product->
            productViewModel.getAllProductResponseCode.observe(viewLifecycleOwner){code->
                if (code == "200"){
                    val getOwnProduct = product.filter { it.sellerID == currentSeller }

                    var totalRevenue = 0
                    var totalSold = 0
                    var inventoryLeft = 0

                    for (product in getOwnProduct) {
                        totalRevenue += product.revenue
                        totalSold += product.soldCount
                        inventoryLeft += product.stock

                    }

                    if (totalSold!=0){
                        aov = totalRevenue / totalSold
                    }else{
                        aov = 0
                    }

                    getOwnProduct.forEach {
                        val getFutureRevenue = listOf(it.stock * it.price)
                        for (valuation in getFutureRevenue){
                            inventoryValuation += valuation
                        }
                    }

                    binding.rvAov.text = "Average Order Value: Rp. $aov"
                    binding.tvRevenue.text = "Total Revenue: Rp. $totalRevenue"
                    binding.totalSold.text = "Total Product Sold: $totalSold"
                    binding.invValuation.text = "Inventory Valuation: Rp. $inventoryValuation"
                    binding.progressBar.visibility = View.GONE
                    binding.contentPage.visibility = View.VISIBLE

                }else{

                }
            }
        }
        productViewModel.getAllProduct()


    }


}