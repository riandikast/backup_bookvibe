package com.sleepydev.bookvibe.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sleepydev.bookvibe.datastore.UserManager
import com.sleepydev.bookvibe.model.Product
import com.sleepydev.bookvibe.model.SoldResponse
import com.sleepydev.bookvibe.model.UpdateResponse
import com.sleepydev.bookvibe.model.UpdateStockResponse
import com.sleepydev.bookvibe.model.User
import com.sleepydev.bookvibe.repository.ProductRepository
import com.sleepydev.bookvibe.repository.UserRepository
import com.sleepydev.bookvibe.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.android.parcel.RawValue
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor (val productRepository: ProductRepository): ViewModel() {
    lateinit var userManager: UserManager
    var addProductObserver: SingleLiveEvent<Product> = SingleLiveEvent()
    var addProductResponseCode: SingleLiveEvent<String> = SingleLiveEvent()

    var getAllProductResponseCode: MutableLiveData<String> = MutableLiveData()
    var getAllProductObserver: MutableLiveData<List<Product>> = MutableLiveData()

    var currentSellerObserver:SingleLiveEvent<Product>  = SingleLiveEvent()
    var currentSellerResponseCode: MutableLiveData<String> = MutableLiveData()

    var deleteProductObserver: SingleLiveEvent<Product> = SingleLiveEvent()
    var deleteProductResponseCode: SingleLiveEvent<String> = SingleLiveEvent()

    var stockProductObserver: SingleLiveEvent<Product> = SingleLiveEvent()
    var stockProductResponseCode: SingleLiveEvent<String> = SingleLiveEvent()

    var soldProductObserver: SingleLiveEvent<Product> = SingleLiveEvent()
    var soldProductResponseCode: SingleLiveEvent<String> = SingleLiveEvent()

    fun addProduct(name: String, desc:String, price:Int, stock:Int, sellerName:String, image: @RawValue Any? = null, sellerID: Int ){
        viewModelScope.launch {
            productRepository.addProduct(name, desc, price, stock, sellerName, image, sellerID, 0, 0, addProductObserver, addProductResponseCode)
        }

    }

    fun getAllProduct(){
        viewModelScope.launch {
            productRepository.getAllProduct(getAllProductObserver, getAllProductResponseCode)
        }
    }

    fun deleteProduct(id:Int) {
        viewModelScope.launch {
            productRepository.deleteProduct(id, deleteProductObserver, deleteProductResponseCode)
        }
    }

    fun getProductTemp(id:Int) {
        viewModelScope.launch {
            productRepository.getSellerProduct(id, currentSellerObserver, currentSellerResponseCode)
        }
    }

    fun updateStock(id : Int, dataProduct : UpdateStockResponse,){
        viewModelScope.launch {
            productRepository.updateStock(id, dataProduct, stockProductObserver, stockProductResponseCode)
        }
    }

    fun productSold(id : Int, dataProduct : SoldResponse){
        viewModelScope.launch {
            productRepository.soldProduct(id, dataProduct, soldProductObserver, soldProductResponseCode)
        }
    }

    fun productID(context: Context): LiveData<Int> {
        userManager = UserManager(context)
        return userManager.productID.asLiveData()
    }

    fun productIMG(context: Context): LiveData<String> {
        userManager = UserManager(context)
        return userManager.productIMG.asLiveData()
    }


    fun sellerID(context: Context): LiveData<Int> {
        userManager = UserManager(context)
        return userManager.sellerID.asLiveData()
    }



}