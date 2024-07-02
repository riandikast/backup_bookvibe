package com.sleepydev.bookvibe.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isNotEmpty
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.databinding.FragmentAddProductBinding
import com.sleepydev.bookvibe.databinding.FragmentProductBinding
import com.sleepydev.bookvibe.utils.CustomToast
import com.sleepydev.bookvibe.viewmodel.NetworkViewModel
import com.sleepydev.bookvibe.viewmodel.ProductViewModel
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.RawValue
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlin.properties.Delegates


@AndroidEntryPoint
class AddProductFragment : Fragment() {
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private val  productViewModel: ProductViewModel by viewModels()
    private val networkViewModel: NetworkViewModel by viewModels()

    private lateinit var sizeCheck: String
    private var typeCheck : String? = null
    private lateinit var imageCheck : String
    var image : MultipartBody.Part? = null
    lateinit var imageUri : Uri
    lateinit var sendNoImage :String
    var toastShown = false
    var  preventFirstLoad = true
    private val customToast = CustomToast()

    var currentUserID by Delegates.notNull<Int>()
    lateinit var currentUserNamee : String

    private lateinit var pickFileLauncher: ActivityResultLauncher<Intent>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentAddProductBinding.inflate(inflater, container, false)

        imageCheck=""
        sendNoImage = "true"

        checkNetwork()

        pickFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                // Tangani hasil pemilihan file

                handleFilePickResult(data)

            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnBacktohome.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.addBtn.setOnClickListener {
            val productName = binding.addProductName.text.toString()
            val productDesc = binding.addProductDesc.text.toString()
            val productPrice = binding.addProductPrice.text.toString()
            val productStock = binding.addProductStock.text.toString()

            networkViewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->

                if (isOnline){
                    if (binding.addProductName.text.isNotEmpty() && binding.addProductDesc.text.isNotEmpty()
                        && binding.addProductPrice.text.isNotEmpty()
                        && binding.addProductStock.text.isNotEmpty()
                        && image!=null
                    ) {
                        val productPriceInt = productPrice.toInt()
                        val productStockInt = productStock.toInt()
                        if (sendNoImage == "true"){
                            val attachmentEmpty = "".toRequestBody("text/plain".toMediaTypeOrNull())
                            image  =  MultipartBody.Part.createFormData(imageUri.toString(), "", attachmentEmpty)
                            Log.d("tesblank", image.toString())
                        }
                        addProduct(productName, productDesc, productPriceInt!!, productStockInt!!, currentUserNamee, imageUri, currentUserID)
                }else{

                        val customToast = CustomToast()
                        customToast.customFailureToast(requireContext(),"Please Fill All the Form")


                    }
            }else{
                    val customToast = CustomToast()
                    customToast.customFailureToast(requireContext(),"No Internet Connection")


                }
            }
        }

        binding.addProductImage.setOnClickListener {
            if (askForPermissions()) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickFileLauncher.launch(intent)
            }
        }

    }

    private fun getContent(it : Uri) {

        val contentResolver = requireContext().contentResolver

        if (it != null) {
            val type = contentResolver.getType(it)
            imageUri = it
            binding.addProductImage.setImageURI(it)
            val getType = type.toString()
            typeCheck = if (getType == "image/png") {
                ".png"
            } else if (getType == "image/jpg") {
                ".jpg"
            } else if (getType == "image/jpeg") {
                ".jpeg"
            } else {
                null
            }

            val outputDir = requireContext().cacheDir // context being the Activity pointer
            val tempFile = File.createTempFile("temp-", typeCheck, outputDir)
            val customName = tempFile.name.toString()
            val regex = Regex("[0-9]")
            val removeTempName = customName.replace("temp-", "Profile Image")
            val postCustomName = regex.replace(removeTempName, "")
            Log.d("logdir", postCustomName)
            val inputStream = contentResolver.openInputStream(it)
            tempFile.outputStream().use {
                inputStream?.copyTo(it)

            }

            val requestBody: RequestBody = tempFile.asRequestBody(type?.toMediaType())
            val getImageSize = requestBody.contentLength().toDouble()
            val convertToMB = getImageSize / 1000000
            sizeCheck = if (convertToMB > 1) {
                ">1mb"
            } else {
                "<1mb"
            }
            Log.d("imagesize", convertToMB.toString())

            image =
                MultipartBody.Part.createFormData(imageUri.toString(), postCustomName, requestBody)
            imageCheck = "true"
            Log.d("cekk", imageCheck)
        }
    }
    private fun handleFilePickResult(it: Intent?) {

        it?.data.let {
            getContent(it!!)

        }

        if (typeCheck !=null && sizeCheck=="<1mb" && it?.data!==null){
            binding.addProductImage.setImageURI(it.data)
            sendNoImage = "false"
        }

    }

    fun checkNetwork(){
        networkViewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline){

                preventFirstLoad = false
                getUserData()

            }else{
                toastShown = false
                currentUserID = 0


                if (!preventFirstLoad){
                    customToast.customFailureToast(requireContext(),"No Internet Connection")
                }else{
                    preventFirstLoad = false
                }
            }
        }

    }



    @SuppressLint("SuspiciousIndentation")
    fun getUserData() {
        val userViewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]

        userViewModel.userID(requireContext()).observe(viewLifecycleOwner) {
            currentUserID = it
            userViewModel.getCurrentUser(it)
        }

        userViewModel.currentUserObserver.observe(viewLifecycleOwner) {
            currentUserNamee = it.name
        }

    }

    fun addProduct(name: String, desc:String, price:Int, stock:Int, sellerName:String, image: @RawValue Any? = null, sellerID: Int) {

        productViewModel.addProductObserver.observe(viewLifecycleOwner) {
            productViewModel.addProductResponseCode.observe(viewLifecycleOwner){code->
                if (code ==  "201") {
                    val customToast = CustomToast()
                    customToast.customSuccessToast(requireContext(),"Product Posted")

                    view?.findNavController()
                        ?.navigate(R.id.action_addProductFragment_to_productFragment, null,
                            NavOptions.Builder()
                                .setPopUpTo(
                                    R.id.productFragment ,
                                    true
                                ).build())

                } else {
                    val customToast = CustomToast()
                    customToast.customFailureToast(requireContext(),"Cannot Post Product")

                }
            }
        }

        productViewModel.addProduct(name, desc, price, stock, sellerName, image.toString(), sellerID )




    }


    private fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(requireActivity(),arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),2000)
            }
            return false
        }
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            2000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission is granted, you can perform your operation here
                } else {
                    // permission is denied, you can ask for permission again, if you want
                    //  askForPermissions()
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton("App Settings"
            ) { dialogInterface, i ->
                // send to app settings if permission is denied permanently
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", "and5.finalproject.secondhand5", null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel",null)
            .show()
    }

}