package com.sleepydev.bookvibe.view.fragment

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.databinding.FragmentDetailBinding
import com.sleepydev.bookvibe.databinding.PurchaseDialogBinding
import com.sleepydev.bookvibe.model.SoldResponse
import com.sleepydev.bookvibe.model.TopUpResponse
import com.sleepydev.bookvibe.model.TransactionItemResponse
import com.sleepydev.bookvibe.model.TransactionResponse
import com.sleepydev.bookvibe.utils.CustomToast
import com.sleepydev.bookvibe.utils.IDGenerator
import com.sleepydev.bookvibe.viewmodel.NetworkViewModel
import com.sleepydev.bookvibe.viewmodel.ProductViewModel
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

@AndroidEntryPoint
class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val networkViewModel: NetworkViewModel by viewModels()
    private val productViewModel: ProductViewModel by viewModels()
    private val usertViewModel: UserViewModel by viewModels()

    private val customToast = CustomToast()
    var toastShown = false
    var preventFirstLoad = true
    var currentProductID by Delegates.notNull<Int>()
    var currentSellerID: Int? = null
    var currentUserID by Delegates.notNull<Int>()
    lateinit var dialog: AlertDialog
    lateinit var getOldHistory: List<TransactionItemResponse>
    lateinit var getSellerOldHistory: List<TransactionItemResponse>
    var getPrice = 0
    var getStock = 0
    var userBalance = 0
    var stockAfterSold = 0
    var totalPrice = 0
    var currentQty = 1
    var lastRevenue = 0
    var lastSoldCount = 0
    var productName = ""
    var productImage = ""
    lateinit var lastHistory: Any
    var currentUserAdress = ""
    var currentUserPhone = ""
    var isConnect = false

    private var handler: Handler? = null
    private var toastRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        getOldHistory = mutableListOf()
        checkNetwork()
        binding.btnBackDetailProduct.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        binding.buyBtn.setOnClickListener {
            val inputBinding =
                PurchaseDialogBinding.inflate(
                    LayoutInflater.from(requireContext()),
                )

            dialog =
                AlertDialog
                    .Builder(requireContext())
                    .setView(inputBinding.root)
                    .create()

            if (getPrice > userBalance) {
                inputBinding.tvYourBalance.setTextColor(Color.RED)
                inputBinding.btnConfirm.isEnabled = false
                inputBinding.btnConfirm.alpha = 0.6F
            } else {
                inputBinding.tvYourBalance.setTextColor(Color.BLACK)
                inputBinding.btnConfirm.isEnabled = true
                inputBinding.btnConfirm.alpha = 1F
            }

            currentQty = 1
            totalPrice = currentQty * getPrice
            inputBinding.tvTotalPrice.text = "Total Price: Rp. $totalPrice"
            inputBinding.tvStockAmount.setText(currentQty.toString())
            inputBinding.tvYourBalance.text = "Your Balance Rp. $userBalance"
            inputBinding.tvStock.text = "Stock: $getStock"
            inputBinding.tvStockAmount.addTextChangedListener(
                object : TextWatcher {
                    private var isEditing: Boolean = false

                    override fun afterTextChanged(s: Editable?) {
                        if (isEditing) return
                        isEditing = true

                        if (s.isNullOrEmpty()) {
                            currentQty = 0
                            inputBinding.btnConfirm.isEnabled = false
                            inputBinding.btnConfirm.alpha = 0.6F
                            inputBinding.tvTotalPrice.text = "Total Price: Rp. 0"
                        } else {
                            val value = s.toString().toIntOrNull() ?: 0
                            currentQty = value
                            if (value < 1) {
                                inputBinding.tvStockAmount.setText("1")
                                inputBinding.tvStockAmount.setSelection(inputBinding.tvStockAmount.text.length)
                                totalPrice = 1 * getPrice
                                inputBinding.tvTotalPrice.text = "Total Price: Rp. $totalPrice"
                            } else {
                                val adjustedValue =
                                    if (value > getStock) {
                                        getStock
                                    } else {
                                        value
                                    }
                                if (adjustedValue != value) {
                                    currentQty = adjustedValue
                                    inputBinding.tvStockAmount.setText(adjustedValue.toString())
                                    inputBinding.tvStockAmount.setSelection(inputBinding.tvStockAmount.text.length)
                                    totalPrice = adjustedValue * getPrice
                                    inputBinding.tvTotalPrice.text = "Total Price: Rp. $totalPrice"
                                } else {
                                    val limit = 2147483647
                                    if (adjustedValue.toLong() * getPrice > limit) {
                                        inputBinding.tvTotalPrice.text = "Total Price has exceeded the limit."
                                        inputBinding.tvTotalPrice.setTextColor(Color.RED)
                                        inputBinding.btnConfirm.isEnabled = false
                                        inputBinding.btnConfirm.alpha = 0.6F
                                    } else {
                                        inputBinding.tvTotalPrice.setTextColor(Color.BLACK)
                                        inputBinding.btnConfirm.isEnabled = true
                                        inputBinding.btnConfirm.alpha = 1F
                                        totalPrice = adjustedValue * getPrice
                                        inputBinding.tvTotalPrice.text = "Total Price: Rp. $totalPrice"
                                    }
                                }

                                if (totalPrice > userBalance) {
                                    inputBinding.tvYourBalance.setTextColor(Color.RED)
                                    inputBinding.btnConfirm.isEnabled = false
                                    inputBinding.btnConfirm.alpha = 0.6F
                                } else {
                                    inputBinding.tvYourBalance.setTextColor(Color.BLACK)
                                    inputBinding.btnConfirm.isEnabled = true
                                    inputBinding.btnConfirm.alpha = 1F
                                }
                            }
                        }

                        isEditing = false
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int,
                    ) {}

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int,
                    ) {}
                },
            )

            inputBinding.btnDecrease.setOnClickListener {
                if (currentQty > 1) {
                    currentQty--

                    inputBinding.tvStockAmount.setText(currentQty.toString())
                    totalPrice = currentQty * getPrice
                    inputBinding.tvTotalPrice.text = "Total Price: Rp. $totalPrice"
                }
            }

            inputBinding.btnIncrease.setOnClickListener {
                currentQty++
                if (currentQty <= getStock) {
                    inputBinding.tvStockAmount.setText(currentQty.toString())
                    totalPrice = currentQty * getPrice
                    inputBinding.tvTotalPrice.text = "Total Price: Rp. $totalPrice"
                }
            }

            inputBinding.btnConfirm.setOnClickListener {
                inputBinding.tvStockAmount.clearFocus()
                it.hideKeyboard()

                ConfirmDialogUtils.showConfirmDialog(
                    context = requireContext(),
                    title = "Confirm Action",
                    message = "Purchase Product?",
                    onPositiveAction = {
                        stockAfterSold = getStock - currentQty
                        val newRevenue = lastRevenue + totalPrice
                        val newSoldCount = lastSoldCount + currentQty
                        val dataProduct = SoldResponse(currentProductID, stockAfterSold, newRevenue, newSoldCount)
                        val newBalance = userBalance - totalPrice
                        val dataUser = TopUpResponse(currentUserID, newBalance)

                        checkInternetBeforePurchase(currentProductID, dataProduct, currentUserID, dataUser)
                    },
                )
                dialog.dismiss()
            }

            dialog.show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun checkNetwork() {
        networkViewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline) {
                isConnect = true
                handler?.removeCallbacks(toastRunnable!!)
                preventFirstLoad = false
                getMyProduct()
            } else {
                isConnect = false
                toastShown = false
                binding.progressBar.visibility = View.VISIBLE
                binding.contentPage.visibility = View.INVISIBLE
                binding.buyBtn.visibility = View.INVISIBLE

                if (!preventFirstLoad) {
                    handler = Handler(Looper.getMainLooper())
                    toastRunnable =
                        Runnable {
                            customToast.customFailureToast(requireContext(), "No Internet Connection")
                        }

                    handler?.postDelayed(toastRunnable!!, 4000)
                } else {
                    preventFirstLoad = false
                }
                if (dialog.isShowing) {
                    dialog.dismiss()
                }
            }
        }
    }

    fun getMyProduct() {
        productViewModel.productID(requireContext()).observe(viewLifecycleOwner) {
            currentProductID = it
            productViewModel.getProductTemp(currentProductID)
        }

        usertViewModel.userID(requireContext()).observe(viewLifecycleOwner) {
            currentUserID = it
            usertViewModel.currentUserObserver.observe(viewLifecycleOwner) { user ->
                usertViewModel.currentUserResponseCode.observe(viewLifecycleOwner) { codeUser ->
                    if (codeUser == "200") {
                        userBalance = user.balance
                        lastHistory = user.balance
                        getOldHistory = user.history
                        currentUserPhone = user.phoneNumber
                        currentUserAdress = user.address
                    }
                }
            }
            usertViewModel.getCurrentUser(currentUserID)
        }

        Handler(Looper.getMainLooper()).postDelayed({
            productViewModel.currentSellerObserver.observe(viewLifecycleOwner) {
                productViewModel.currentSellerResponseCode.observe(viewLifecycleOwner) { code ->
                    if (code == "200") {
                        getPrice = it.price
                        lastRevenue = it.revenue
                        currentSellerID = it.sellerID
                        productName = it.name
                        productImage = it.image.toString()

                        if (it.stock == 0 && it.sellerID != currentUserID) {
                            binding.buyBtn.setText("Product is Out of Stock")
                            binding.buyBtn.isEnabled = false

                            binding.buyBtn.alpha = 0.6F
                        } else {
                            if (it.sellerID == currentUserID) {
                                binding.buyBtn.setText("Cannot Buy Own Product")
                                binding.buyBtn.isEnabled = false
                                binding.buyBtn.alpha = 0.6F
                            } else {
                                if (currentUserPhone != "" && currentUserAdress != "") {
                                    binding.buyBtn.setText("Purchase This Product")
                                    binding.buyBtn.isEnabled = true
                                    binding.buyBtn.alpha = 1F
                                } else {
                                    binding.buyBtn.setText("Please Fill Your Address and Phone Number ")
                                    binding.buyBtn.isEnabled = false
                                    binding.buyBtn.alpha = 0.6F
                                }
                            }
                        }

                        lastSoldCount = it.soldCount

                        usertViewModel.getCurrentUserForSellerImage(currentSellerID!!)
                        getStock = it.stock

                        usertViewModel.getCurrentUserForSellerImage(currentSellerID!!)

                        binding.productName.text = it.name
                        binding.productPrice.text = "Rp. ${it.price}"
                        binding.soldCount.text = "Sold ${it.soldCount}"
                        binding.sellerName.text = it.sellerName
                        binding.productDescription.text = it.desc
                        val getImg = it.image
                        val requestOptions =
                            RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .skipMemoryCache(false)
                        Glide
                            .with(requireContext())
                            .load(getImg)
                            .apply(requestOptions)
                            .into(binding.productImage)

                        usertViewModel.currentUserForSellerImageObserver.observe(viewLifecycleOwner) { user ->
                            usertViewModel.currentUserForSellerImageResponseCode.observe(viewLifecycleOwner) { codeUser ->
                                if (codeUser == "200") {
                                    getSellerOldHistory = user!!.history
                                    if (user!!.image != "") {
                                        val rawValueToString = user.image.toString()
                                        val regex = """name="(content://[^\"]+)"""".toRegex()
                                        // Ekstraksi content URI menggunakan regex
                                        val matchResult = regex.find(rawValueToString)

                                        val contentUri = matchResult?.groups?.get(1)?.value
                                        val uri = Uri.parse(user.image.toString())

                                        Glide
                                            .with(requireContext())
                                            .load(uri)
                                            .apply(requestOptions)
                                            .into(binding.sellerImage)
                                    }
                                    binding.contentPage.visibility = View.VISIBLE
                                    binding.progressBar.visibility = View.GONE
                                    binding.buyBtn.visibility = View.VISIBLE
                                } else {
                                    if (!toastShown) {
                                        customToast.customFailureToast(requireContext(), "Product Not Found")
                                        toastShown = true
                                    }
                                }
                            }
                        }
                    } else {
                    }
                }
            }
        }, 1000)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun checkInternetBeforePurchase(
        productID: Int,
        dataProduct: SoldResponse,
        userID: Int,
        adjustedBalance: TopUpResponse,
    ) {
        if (isConnect) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val date = Date()
            val time = dateFormat.format(date)
            val responseItem =
                TransactionItemResponse(
                    IDGenerator.generateUniqueID(),
                    "Purchased",
                    currentProductID,
                    productName,
                    productImage,
                    currentQty,
                    totalPrice.toString(),
                    time,
                )

            val sellerResponseItem =
                TransactionItemResponse(
                    IDGenerator.generateUniqueID(),
                    "Sold",
                    currentProductID,
                    productName,
                    productImage,
                    currentQty,
                    totalPrice.toString(),
                    time,
                )

            val dataUser = TransactionResponse(currentUserID, getOldHistory)
            val dataSeller = TransactionResponse(currentSellerID!!, getSellerOldHistory)
            handleTransactionHistory(dataUser, dataSeller, responseItem, sellerResponseItem)
            handleProductSold(productID, dataProduct, userID, adjustedBalance)
        } else {
            toastShown = false
            binding.progressBar.visibility = View.VISIBLE
            binding.contentPage.visibility = View.INVISIBLE

            if (!preventFirstLoad) {
                customToast.customFailureToast(requireContext(), "No Internet Connection")
            } else {
                preventFirstLoad = false
            }
        }
    }

    fun handleProductSold(
        productID: Int,
        dataProduct: SoldResponse,
        userID: Int,
        adjustedBalance: TopUpResponse,
    ) {
        productViewModel.soldProductObserver.observe(viewLifecycleOwner) {
            productViewModel.soldProductResponseCode.observe(viewLifecycleOwner) { soldCode ->
                if (soldCode == "200") {
                    handleUserBalance(userID, adjustedBalance)
                } else {
                    if (!toastShown) {
                        customToast.customFailureToast(requireContext(), "Purchase Failed")
                        toastShown = true
                    }
                }
            }
        }
        productViewModel.productSold(productID, dataProduct)
    }

    fun handleUserBalance(
        userID: Int,
        adjustedBalance: TopUpResponse,
    ) {
        usertViewModel.balanceObserver.observe(viewLifecycleOwner) {
            usertViewModel.topUpResponseCode.observe(viewLifecycleOwner) { code ->
                toastShown = false
                if (code == "200") {
                    if (!toastShown) {
                        customToast.customSuccessToast(requireContext(), "Purchase Successful")
                        toastShown = true
                        view?.findNavController()?.navigate(
                            R.id.detailFragment,
                            null,
                            NavOptions
                                .Builder()
                                .setPopUpTo(
                                    R.id.detailFragment,
                                    true,
                                ).build(),
                        )
                    }
                } else {
                    if (!toastShown) {
                        customToast.customFailureToast(requireContext(), "Purchase Failed")
                        toastShown = true
                    }
                }
            }
        }
        usertViewModel.topUpBalance(userID, adjustedBalance)
    }

    fun handleTransactionHistory(
        transactionResponse: TransactionResponse,
        sellerTransactionResponse: TransactionResponse,
        list: TransactionItemResponse,
        listSeller: TransactionItemResponse,
    ) {
        usertViewModel.userID(requireContext()).observe(viewLifecycleOwner) {
            usertViewModel.updateTransaction(it, transactionResponse, list)
        }
        usertViewModel.updateTransactionForSeller(currentSellerID!!, sellerTransactionResponse, listSeller)
    }

    fun View.hideKeyboard() {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }
}
