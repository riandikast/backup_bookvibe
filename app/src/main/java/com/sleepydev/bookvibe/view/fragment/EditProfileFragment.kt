package com.sleepydev.bookvibe.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.databinding.CustomZoomPhotoProfileDialogBinding
import com.sleepydev.bookvibe.databinding.FragmentEditProfileBinding
import com.sleepydev.bookvibe.databinding.FragmentHomeBinding
import com.sleepydev.bookvibe.datastore.UserManager
import com.sleepydev.bookvibe.model.TopUpResponse
import com.sleepydev.bookvibe.model.UpdateResponse
import com.sleepydev.bookvibe.utils.CustomToast
import com.sleepydev.bookvibe.viewmodel.NetworkViewModel
import com.sleepydev.bookvibe.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Address
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlin.properties.Delegates


@AndroidEntryPoint
class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    var currentUserID by Delegates.notNull<Int>()
    lateinit var dataUser : UpdateResponse
    lateinit var newName : String
    lateinit var newAddress : String
    lateinit var newPhone : String


    //update image
    private lateinit var sizeCheck: String
    private var typeCheck : String? = null
    private lateinit var imageCheck : String
    var image : MultipartBody.Part? = null
    lateinit var imageUri : Uri
    private lateinit var zoomType : String
    private lateinit var localZoom : Uri
    private lateinit var sendNoImage : String
    lateinit var imageDialog : AlertDialog

    private lateinit var getProfileImage : Uri
    private val networkViewModel: NetworkViewModel by viewModels()


    private val customToast = CustomToast()
    var toastShown = false
    var  preventFirstLoad = true
    lateinit var previousName : String
    lateinit var previousAddress : String
    lateinit var previousPhone: String

    private lateinit var pickFileLauncher: ActivityResultLauncher<Intent>

    private var handler: Handler? = null
    private var toastRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getProfileImage = "".toUri()
        pickFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                // Tangani hasil pemilihan file

                handleFilePickResult(data)

            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentEditProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toastShown = false
        checkNetwork()
        zoomType = "online"
        imageCheck=""

        sendNoImage = "true"

        binding.btnBackProfile.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.btnAddProfile.setOnClickListener {
            if (askForPermissions()) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickFileLauncher.launch(intent)
            }
        }

        binding.profileImage.setOnClickListener{

             val zoomBinding = CustomZoomPhotoProfileDialogBinding.inflate(
                LayoutInflater.from(requireContext())
            )

            val inputView = zoomBinding.root
            imageDialog = AlertDialog.Builder(requireContext())
                .setView(inputView)
                .create()

            imageDialog.show()
            zoomBinding.deleteProfilePic.setOnClickListener {
                binding.profileImage.setImageResource(R.drawable.profile_circle)
                zoomBinding.zoomProfile.setImageResource(R.drawable.profile_circle)
                imageUri = "".toUri()
                getProfileImage = "".toUri()
                localZoom = "".toUri()
                zoomType = "blank"
                sendNoImage = "true"
                binding.btnSaveChanges.isEnabled = true
                binding.btnSaveChanges.alpha = 1F

            }

            if (zoomType == "local"){
                Glide.with(requireActivity()).load(localZoom).into(zoomBinding.zoomProfile)
            }else if (zoomType == "online"){
                if (zoomType == "blank"){
                    zoomBinding.zoomProfile.setImageResource(R.drawable.profile_circle)
                }else{
                    if (getProfileImage.toString() != ""){
                        Glide.with(requireActivity()).load(getProfileImage).into(zoomBinding.zoomProfile)
                    }else{
                        zoomBinding.zoomProfile.setImageResource(R.drawable.profile_circle)
                    }
                }

            }else{
                zoomBinding.zoomProfile.setImageResource(R.drawable.profile_circle)
            }

        }

        binding.btnSaveChanges.setOnClickListener {

            newName = binding.nameText.text.toString()
            newAddress = binding.addressText.text.toString()
            newPhone = binding.phoneText.text.toString()


            if (sendNoImage == "true"){
                imageUri = "".toUri()
                val attachmentEmpty = "".toRequestBody("text/plain".toMediaTypeOrNull())
                image  =  MultipartBody.Part.createFormData(imageUri.toString(), "", attachmentEmpty)
                Log.d("tesblank", image.toString())
            }
            dataUser = UpdateResponse(currentUserID, newName, newAddress, newPhone, imageUri.toString())
            updateDataUser(currentUserID, dataUser)
        }
    }
    fun editProfileTextWatcher(){

        binding.nameText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentText = s.toString()
                if(currentText!= previousName ){
                    binding.btnSaveChanges.isEnabled = true
                    binding.btnSaveChanges.alpha = 1F
                }else{
                    binding.btnSaveChanges.isEnabled = false
                    binding.btnSaveChanges.alpha = 0.6F
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Enable or disable the button based on whether EditText is empty

            }
        })
        binding.addressText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val currentText = s.toString()
                if(currentText!= previousAddress ){
                    binding.btnSaveChanges.isEnabled = true
                    binding.btnSaveChanges.alpha = 1F
                }else{
                    binding.btnSaveChanges.isEnabled = false
                    binding.btnSaveChanges.alpha = 0.6F
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Enable or disable the button based on whether EditText is empty

            }
        })
        binding.phoneText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val currentText = s.toString()
                if(currentText!= previousPhone){
                    binding.btnSaveChanges.isEnabled = true
                    binding.btnSaveChanges.alpha = 1F
                }else{
                    binding.btnSaveChanges.isEnabled = false
                    binding.btnSaveChanges.alpha = 0.6F
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Enable or disable the button based on whether EditText is empty

            }
        })


    }
    fun checkNetwork(){

        networkViewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline){
                handler?.removeCallbacks(toastRunnable!!)
                preventFirstLoad = false
                getUserData()

            }else{
                toastShown = false
                currentUserID = 0
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

            binding.nameText.setText(it.name)
            binding.addressText.setText(it.address)
            binding.phoneText.setText(it.phoneNumber)
            binding.progressBar.visibility = View.GONE
            binding.contentPage.visibility = View.VISIBLE

            previousName = it.name
            previousAddress = it.address
            previousPhone = it.phoneNumber
            editProfileTextWatcher()

            if (it.image != null && it.image != "") {
                val rawValueToString = it.image.toString()
                val regex = """name="(content://[^\"]+)"""".toRegex()

                // Ekstraksi content URI menggunakan regex
                val matchResult = regex.find(rawValueToString)

                    val contentUri = matchResult?.groups?.get(1)?.value
                    val uri = Uri.parse(it.image.toString())
                    imageUri = uri
                    localZoom = uri
                    getProfileImage = uri
                    val requestOptions = RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                    Glide.with(requireContext())
                        .load(uri)
                        .apply(requestOptions)
                        .into(binding.profileImage)


            }

        }







    }

    private fun handleFilePickResult(it: Intent?) {


        Handler(Looper.getMainLooper()).postDelayed({
            binding.changeImageProgressBar.visibility = View.VISIBLE
            binding.changeImageProgressBar.visibility = View.GONE
            it?.data.let {
                getContent(it!!)
                binding.btnSaveChanges.isEnabled = true
                binding.btnSaveChanges.alpha = 1F
            }

            if (typeCheck !=null && sizeCheck=="<1mb" && it?.data!==null){
                binding.profileImage.setImageURI(it.data)

                sendNoImage = "false"
                zoomType = "local"
                localZoom = it.data!!
            }else{
                zoomType = "online"
            }


        }, 2000)


    }

    fun updateDataUser(id : Int, dataUser : UpdateResponse){
        val viewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        toastShown = false
        networkViewModel.isOnline.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline) {
                viewModel.updateProfileObserver.observe(viewLifecycleOwner) {
                    viewModel.updateUserResponseCode.observe(viewLifecycleOwner) { code ->
                        if (code == "200") {
                            if (!toastShown){
                                customToast.customSuccessToast(requireContext(),"Profile Updated")
                                toastShown = true
                            }

                            val userManager = UserManager(requireContext())
                            GlobalScope.launch {
                                userManager.updateProfile(newName, newAddress, newPhone)
                            }

                            getUserData()
                        } else {
                            if (!toastShown){
                                customToast.customFailureToast(requireContext(),"Failed to Update Profile")
                                toastShown = true
                            }
                        }
                    }
                }
                viewModel.updateUser(id, dataUser)

            } else {
                customToast.customFailureToast(requireContext(),"No Internet Connection")
            }
        }

    }


    private fun getContent(it : Uri) {


        val contentResolver = requireContext().contentResolver

        if (it != null) {
            val type = contentResolver.getType(it)
            imageUri = it
            binding.profileImage.setImageURI(it)
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