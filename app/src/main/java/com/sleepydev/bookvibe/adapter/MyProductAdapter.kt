package com.sleepydev.bookvibe.adapter

import ConfirmDialogUtils
import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.databinding.MyProductAdapterBinding
import com.sleepydev.bookvibe.databinding.StockDialogBinding
import com.sleepydev.bookvibe.datastore.UserManager
import com.sleepydev.bookvibe.model.Product
import com.sleepydev.bookvibe.model.UpdateStockResponse
import com.sleepydev.bookvibe.utils.CustomToast
import com.sleepydev.bookvibe.viewmodel.NetworkViewModel
import com.sleepydev.bookvibe.viewmodel.ProductViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MyProductAdapter(fragment: Fragment) : RecyclerView.Adapter<MyProductAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var _binding: MyProductAdapterBinding? = null
    private val binding get() = _binding!!
    var networkVM: NetworkViewModel
    var productVM: ProductViewModel
    var fragment = fragment

    private var dialogBinding: StockDialogBinding? = null
    lateinit var dialogUpdate: AlertDialog

    fun getDialog(): AlertDialog {
        return dialogUpdate
    }

    var validStock = true
    var validPrice = true


    init {

        networkVM = ViewModelProvider(fragment)[NetworkViewModel::class.java]
        productVM = ViewModelProvider(fragment)[ProductViewModel::class.java]

    }

    interface RefreshCallback {
        fun onRefreshRecyclerView()
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dialogBinding = StockDialogBinding.inflate(
            LayoutInflater.from(fragment.requireContext())
        )

        dialogUpdate = AlertDialog.Builder(fragment.requireContext())
            .setView(dialogBinding!!.root)
            .create()
        binding.productName.text = productData!![position].name

        val stock = productData!![position].stock
        if (productData!![position].stock == 0) {
            binding.productStock.setTextColor(Color.RED)
        } else {
            binding.productStock.setTextColor(Color.BLACK)
        }
        binding.productStock.text = "Stock: $stock"
        val getImage = productData!![position].image.toString()


        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false)
        Glide.with(holder.itemView.context).load(getImage.toUri()).apply(requestOptions)
            .into(binding.productImage)

        binding.btnDelete.setOnClickListener {
            var showtoast = false

            ConfirmDialogUtils.showConfirmDialog(
                context = holder.itemView.context,
                title = "Confirm Action",
                message = "Delete Product?",
                onPositiveAction = {
                    networkVM.isOnline.observe(fragment) { isOnline ->
                        if (isOnline) {
                            productVM.deleteProductObserver.observe(fragment) {
                                productVM.deleteProductResponseCode.observe(fragment) { code ->
                                    if (code == "200") {
                                        if (!showtoast) {
                                            val ct = CustomToast()
                                            ct.customSuccessToast(
                                                holder.itemView.context,
                                                "Product Deleted"
                                            )
                                            showtoast = true
                                        }
                                        val refresh = fragment
                                        refresh.findNavController().navigate(
                                            R.id.productFragment, null,
                                            NavOptions.Builder()
                                                .setPopUpTo(
                                                    R.id.productFragment,
                                                    true
                                                ).build()
                                        )

                                    } else {

                                        if (!showtoast) {
                                            val ct = CustomToast()
                                            ct.customFailureToast(
                                                holder.itemView.context,
                                                "Delete Failed"
                                            )
                                            showtoast = true
                                        }
                                    }
                                }
                            }
                            productVM.deleteProduct(productData!![position].id)

                        } else {
                            if (!showtoast) {
                                val ct = CustomToast()
                                ct.customFailureToast(
                                    holder.itemView.context,
                                    "No Internet Connection"
                                )
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
                userManager.saveTempProduct(
                    productData!![position].id,
                    productData!![position].image.toString(), productData!![position].sellerID
                )
            }
            fragment.findNavController().navigate(R.id.action_productFragment_to_detailFragment)

        }

        binding.btnUpdate.setOnClickListener {
            var showtoast = false


            var currentStock = productData!![position].stock
            var currentPrice = productData!![position].price

            dialogBinding!!.etPrice.setText(currentPrice.toString())
            dialogBinding!!.tvStockAmount.setText(currentStock.toString())

            dialogBinding!!.tvStockAmount.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    val value = s.toString().toIntOrNull() ?: 0
                    validStock = !s.isNullOrEmpty()
                    currentStock = value
                    if (validPrice && validStock) {
                        dialogBinding!!.btnConfirm.isEnabled = true
                        dialogBinding!!.btnConfirm.alpha = 1F
                    } else {
                        dialogBinding!!.btnConfirm.isEnabled = false
                        dialogBinding!!.btnConfirm.alpha = 0.6F
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            dialogBinding!!.etPrice.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val value = s.toString().toIntOrNull() ?: 0
                    if (s.isNullOrEmpty()) {
                        validPrice = false

                    } else {
                        if (value < 1) {
                            dialogBinding!!.etPrice.setText("1")
                            dialogBinding!!.etPrice.setSelection(dialogBinding!!.etPrice.text.length)
                        }
                        validPrice = true

                    }
                    currentStock = value
                    if (validPrice && validStock) {
                        dialogBinding!!.btnConfirm.isEnabled = true
                        dialogBinding!!.btnConfirm.alpha = 1F
                    } else {
                        dialogBinding!!.btnConfirm.isEnabled = false
                        dialogBinding!!.btnConfirm.alpha = 0.6F
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })



            dialogBinding!!.btnDecrease.setOnClickListener {
                if (currentStock > 0) {
                    currentStock--

                    dialogBinding!!.tvStockAmount.setText(currentStock.toString())
                }
            }

            dialogBinding!!.btnIncrease.setOnClickListener {
                currentStock++

                dialogBinding!!.tvStockAmount.setText(currentStock.toString())
            }

            dialogBinding!!.btnConfirm.setOnClickListener {
                networkVM.isOnline.observe(fragment) { isOnline ->
                    if (isOnline) {
                        productVM.stockProductObserver.observe(fragment) {
                            productVM.stockProductResponseCode.observe(fragment) { code ->
                                if (code == "200") {
                                    if (!showtoast) {
                                        val ct = CustomToast()
                                        ct.customSuccessToast(
                                            holder.itemView.context,
                                            "Stock Updated"
                                        )
                                        showtoast = true
                                    }
                                    val refresh = fragment
                                    refresh.findNavController().navigate(
                                        R.id.productFragment, null,
                                        NavOptions.Builder()
                                            .setPopUpTo(
                                                R.id.productFragment,
                                                true
                                            ).build()
                                    )

                                } else {

                                    if (!showtoast) {
                                        val ct = CustomToast()
                                        ct.customFailureToast(
                                            holder.itemView.context,
                                            "Stock Not Updated"
                                        )
                                        showtoast = true
                                    }
                                }
                            }
                        }
                        currentStock = dialogBinding!!.tvStockAmount.text.toString().toInt()
                        currentPrice = dialogBinding!!.etPrice.text.toString().toInt()
                        val newData = UpdateStockResponse(
                            productData!![position].id,
                            currentStock,
                            currentPrice
                        )
                        productVM.updateStock(productData!![position].id, newData)

                    } else {
                        if (!showtoast) {
                            val ct = CustomToast()
                            ct.customFailureToast(holder.itemView.context, "No Internet Connection")
                            showtoast = true
                        }
                    }
                }
                dialogUpdate.dismiss()
            }

            dialogUpdate.show()
        }


    }

    private var productData: List<Product>? = null
    fun setProductList(productList: List<Product>) {
        this.productData = productList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding =
            MyProductAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val view = binding.root

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (productData == null) {
            0
        } else {
            productData!!.size
        }
    }


}