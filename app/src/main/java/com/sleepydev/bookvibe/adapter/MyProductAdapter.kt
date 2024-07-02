package com.sleepydev.bookvibe.adapter

import ConfirmDialogUtils
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.databinding.CustomZoomPhotoProfileDialogBinding
import com.sleepydev.bookvibe.databinding.MyProductAdapterBinding
import com.sleepydev.bookvibe.databinding.StockDialogBinding
import com.sleepydev.bookvibe.datastore.UserManager
import com.sleepydev.bookvibe.model.Product
import com.sleepydev.bookvibe.model.UpdateResponse
import com.sleepydev.bookvibe.model.UpdateStockResponse
import com.sleepydev.bookvibe.utils.CustomToast
import com.sleepydev.bookvibe.view.activity.MainActivity
import com.sleepydev.bookvibe.viewmodel.NetworkViewModel
import com.sleepydev.bookvibe.viewmodel.ProductViewModel
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch


class MyProductAdapter ( fragment: Fragment): RecyclerView.Adapter<MyProductAdapter.ViewHolder>() {
    class ViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView)
    private var _binding: MyProductAdapterBinding? = null
    private val binding get() = _binding!!
    var networkVM :NetworkViewModel
    var productVM :ProductViewModel
    var fragment = fragment

    init {

        networkVM = ViewModelProvider(fragment)[NetworkViewModel::class.java]
        productVM = ViewModelProvider(fragment)[ProductViewModel::class.java]

    }

    interface RefreshCallback {
        fun onRefreshRecyclerView()
    }
    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int, ) {

        binding.productName.text = productData!![position].name

        val stock = productData!![position].stock
        binding.productStock.text = "Stock: $stock"
        val getImage = productData!![position].image.toString()
//        binding.productImage.setImageURI(getImage.toUri())
        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false)
        Glide.with(holder.itemView.context).load(getImage.toUri()).apply(requestOptions).into(binding.productImage)

        binding.btnDelete.setOnClickListener {
            var showtoast = false

            ConfirmDialogUtils.showConfirmDialog(
                context = holder.itemView.context,
                title = "Confirm Action",
                message = "Delete Product?",
                onPositiveAction = {
                    networkVM.isOnline.observe(fragment){isOnline->
                        if(isOnline){
                            productVM.deleteProductObserver.observe(fragment){
                                productVM.deleteProductResponseCode.observe(fragment){code->
                                    if(code=="200"){
                                        if (!showtoast){
                                            val ct = CustomToast()
                                            ct.customSuccessToast(holder.itemView.context, "Product Deleted")
                                            showtoast = true
                                        }
                                       val refresh =  fragment
                                        refresh.findNavController().navigate(R.id.productFragment, null,
                                            NavOptions.Builder()
                                                .setPopUpTo(
                                                    R.id.productFragment,
                                                    true
                                                ).build())

                                    }else{

                                        if (!showtoast){
                                            val ct = CustomToast()
                                            ct.customFailureToast(holder.itemView.context, "Delete Failed")
                                            showtoast = true
                                        }
                                    }
                                }
                            }
                            productVM.deleteProduct(productData!![position].id)

                        }else{
                            if (!showtoast){
                                val ct = CustomToast()
                                ct.customFailureToast(holder.itemView.context, "No Internet Connection")
                                showtoast = true
                            }
                        }
                    }
                },
                onNegativeAction = {

                }
            )
        }
        binding.card.setOnClickListener {
            val userManager = UserManager(fragment.requireContext())
            GlobalScope.launch {
                userManager.saveTempProduct(productData!![position].id,
                    productData!![position].image.toString(), productData!![position].sellerID
                )
            }
            fragment.findNavController().navigate(R.id.action_productFragment_to_detailFragment)

        }

        binding.btnUpdate.setOnClickListener {
            var showtoast = false
            val inputBinding = StockDialogBinding.inflate(
                LayoutInflater.from(fragment.requireContext())
            )

            val dialog = AlertDialog.Builder(fragment.requireContext())
                .setView(inputBinding.root)
                .create()

            var currentStock = productData!![position].stock
//            val formattedNumber = String.format("%.0f", currentStock)

            inputBinding.tvStockAmount.setText(currentStock.toString())

            inputBinding.btnDecrease.setOnClickListener {
                if (currentStock > 0) {
                    currentStock--

                    inputBinding.tvStockAmount.setText(currentStock.toString())
                }
            }

            inputBinding.btnIncrease.setOnClickListener {
                currentStock++

                inputBinding.tvStockAmount.setText(currentStock.toString())
            }

            inputBinding.btnConfirm.setOnClickListener {
                networkVM.isOnline.observe(fragment){isOnline->
                    if(isOnline){
                        productVM.stockProductObserver.observe(fragment){
                            productVM.stockProductResponseCode.observe(fragment){code->
                                if(code=="200"){
                                    if (!showtoast){
                                        val ct = CustomToast()
                                        ct.customSuccessToast(holder.itemView.context, "Stock Updated")
                                        showtoast = true
                                    }
                                    val refresh =  fragment
                                    refresh.findNavController().navigate(R.id.productFragment, null,
                                        NavOptions.Builder()
                                            .setPopUpTo(
                                                R.id.productFragment,
                                                true
                                            ).build())

                                }else{

                                    if (!showtoast){
                                        val ct = CustomToast()
                                        ct.customFailureToast(holder.itemView.context, "Stock Not Updated")
                                        showtoast = true
                                    }
                                }
                            }
                        }
                        currentStock = inputBinding.tvStockAmount.text.toString().toInt()

                        val newData = UpdateStockResponse(productData!![position].id, currentStock)
                        productVM.updateStock(productData!![position].id, newData)

                    }else{
                        if (!showtoast){
                            val ct = CustomToast()
                            ct.customFailureToast(holder.itemView.context, "No Internet Connection")
                            showtoast = true
                        }
                    }
                }
                dialog.dismiss()
            }

            dialog.show()
        }



    }
    private var productData : List<Product>? = null
    fun setProductList(productList: List<Product>){
        this.productData = productList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = MyProductAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val view = binding.root

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if(productData == null){
            0
        }else{
            productData!!.size
        }
    }


}