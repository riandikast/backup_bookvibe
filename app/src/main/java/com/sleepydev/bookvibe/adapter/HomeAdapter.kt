package com.sleepydev.bookvibe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.adapter.MyProductAdapter.ViewHolder
import com.sleepydev.bookvibe.databinding.HomeAdapterBinding
import com.sleepydev.bookvibe.datastore.UserManager
import com.sleepydev.bookvibe.model.Product
import com.sleepydev.bookvibe.viewmodel.LoginViewModel
import com.sleepydev.bookvibe.viewmodel.NetworkViewModel
import com.sleepydev.bookvibe.viewmodel.ProductViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HomeAdapter(fragment: Fragment) : RecyclerView.Adapter<ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private var _binding: HomeAdapterBinding? = null
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

    private var productData: List<Product>? = null
    fun setProductList(productList: List<Product>) {
        this.productData = productList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProductAdapter.ViewHolder {
        _binding = HomeAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val view = binding.root

        return MyProductAdapter.ViewHolder(view)
    }


    override fun onBindViewHolder(holder: MyProductAdapter.ViewHolder, position: Int) {

        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false)
        binding.tvName.text = productData!![position].name
        val price = productData!![position].price.toString()
        binding.tvPrice.text = "Rp. $price"
        val soldCount = productData!![position].soldCount.toString()
        binding.tvSold.text = "$soldCount Sold"
        Glide.with(fragment).load(productData!![position].image).apply(requestOptions)
            .into(binding.productImage)

        binding.card.setOnClickListener {

            loginVM.userToken(fragment.requireContext()).observe(fragment.viewLifecycleOwner) {
                token = it
                if (token !== "") {
                    val userManager = UserManager(fragment.requireContext())
                    GlobalScope.launch {
                        userManager.saveTempProduct(
                            productData!![position].id,
                            productData!![position].image.toString(),
                            productData!![position].sellerID
                        )
                    }
                    fragment.findNavController()
                        .navigate(R.id.action_homeFragment_to_detailFragment)

                } else {
                    fragment.findNavController().navigate(R.id.action_homeFragment_to_loginFragment)

                }
            }

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