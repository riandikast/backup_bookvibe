package com.sleepydev.bookvibe.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sleepydev.bookvibe.databinding.TransactionAdapterBinding
import com.sleepydev.bookvibe.model.TransactionItemResponse
import com.sleepydev.bookvibe.viewmodel.LoginViewModel
import com.sleepydev.bookvibe.viewmodel.NetworkViewModel
import com.sleepydev.bookvibe.viewmodel.ProductViewModel

class TransactionAdapter(fragment: Fragment) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var _binding: TransactionAdapterBinding? = null
    private val binding get() = _binding!!
    var networkVM: NetworkViewModel
    var productVM: ProductViewModel
    var loginVM: LoginViewModel
    var fragment = fragment
    var token = ""

    init {
        loginVM = ViewModelProvider(fragment)[LoginViewModel::class.java]
        networkVM = ViewModelProvider(fragment)[NetworkViewModel::class.java]
        productVM = ViewModelProvider(fragment)[ProductViewModel::class.java]

    }

    private var productData: List<TransactionItemResponse>? = null
    fun setProductList(productList: List<TransactionItemResponse>) {
        this.productData = productList
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        _binding =
            TransactionAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val view = binding.root

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false)


        binding.productName.text = productData!![position].productName
        Glide.with(fragment).load(productData!![position].adjustedImg).apply(requestOptions)
            .into(binding.productImage)
        binding.quantity.text = "Quantity: ${productData!![position].quantity}"

        if (productData!![position].status == "Purchased") {
            binding.balanceStatus.setTextColor(Color.RED)

            binding.balanceStatus.text = "- Rp. ${productData!![position].adjustedBalance}"
        } else {
            binding.balanceStatus.setTextColor(Color.GREEN)
            binding.balanceStatus.text = "+ Rp. ${productData!![position].adjustedBalance}"
        }


    }


    override fun getItemCount(): Int {
        return if (productData == null) {
            0
        } else {
            productData!!.size
        }
    }


}