package com.niro.niroapp.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import carbon.dialog.ProgressDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.niro.niroapp.R
import com.niro.niroapp.activities.MainActivity
import com.niro.niroapp.databinding.FragmentAddNewSellOrderBinding
import com.niro.niroapp.firebase.FileType
import com.niro.niroapp.firebase.FileUploadResponseHandler
import com.niro.niroapp.firebase.FirebaseFileUploadService
import com.niro.niroapp.models.APIError
import com.niro.niroapp.models.APILoader
import com.niro.niroapp.models.Success
import com.niro.niroapp.models.responsemodels.User
import com.niro.niroapp.users.fragments.setReadOnly
import com.niro.niroapp.utils.*
import com.niro.niroapp.viewmodels.CreateSellOrderViewModel
import com.niro.niroapp.viewmodels.PaymentMode
import com.niro.niroapp.viewmodels.QuantityType
import com.niro.niroapp.viewmodels.factories.CreateSellOrderViewModelFactory
import java.io.File
import java.io.IOException
import java.util.*


class CreateNewSellOrderFragment : Fragment(), DateChangeListener, FileUploadResponseHandler,
    OnBackPressedListener {


    private lateinit var bindingFragmentAddNewSellOrder: FragmentAddNewSellOrderBinding
    private var createSellOrderViewModel: CreateSellOrderViewModel? = null

    private var mCurrentUser: User? = null
    private var mProgressDialog : ProgressDialog? = null

    private var mCapturedPath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            mCurrentUser = it.getParcelable(NiroAppConstants.ARG_CURRENT_USER) as? User


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingFragmentAddNewSellOrder = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_new_sell_order,
            container,
            false
        )

        requireActivity().viewModelStore.clear()
        bindingFragmentAddNewSellOrder.lifecycleOwner = viewLifecycleOwner

        return bindingFragmentAddNewSellOrder.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        createSellOrderViewModel = CreateSellOrderViewModelFactory(mCurrentUser).getViewModel(mCurrentUser,owner = requireActivity())

        bindingFragmentAddNewSellOrder.createSellOrderVM = createSellOrderViewModel

        setPageTitle()
        makeFieldsReadOnly()
        initializeCheckChangeListeners()
        initializeFocusChangeListeners()
        initializeClickListeners();
    }

    private fun makeFieldsReadOnly() {
        bindingFragmentAddNewSellOrder.etDispatchDate.setReadOnly(true)
        bindingFragmentAddNewSellOrder.etAddImage.setReadOnly(true)
    }

    private fun setPageTitle() {
        if(activity is MainActivity) (activity as? MainActivity)?.setToolbarTitleAndImage(getString(R.string.title_new_sell_commodities),-1)
    }


    private fun initializeCheckChangeListeners() {

        onKgTypeCheckChanged()
        onQuintalTypeCheckChanged()
        onTonTypeCheckChanged()
    }


    private fun onKgTypeCheckChanged() {
        bindingFragmentAddNewSellOrder.rbQuantityTypeKg.setOnCheckedChangeListener { buttonView, isChecked ->

            bindingFragmentAddNewSellOrder.ivQuantityKgSelected.visibility = if(isChecked) View.VISIBLE else View.INVISIBLE

            if(isChecked) {

                createSellOrderViewModel?.getQuantityTypeSelected()?.value = QuantityType.KG.type
                bindingFragmentAddNewSellOrder.rbQuantityTypeKg.isChecked = true
                bindingFragmentAddNewSellOrder.rbQuantityTypeQuintal.isChecked = false
                bindingFragmentAddNewSellOrder.rbQuantityTypeTon.isChecked = false
            }
        }
    }


    private fun onQuintalTypeCheckChanged() {
        bindingFragmentAddNewSellOrder.rbQuantityTypeQuintal.setOnCheckedChangeListener { buttonView, isChecked ->
            bindingFragmentAddNewSellOrder.ivQuantityQunitalSelected.visibility = if(isChecked) View.VISIBLE else View.INVISIBLE
            bindingFragmentAddNewSellOrder.rbQuantityTypeQuintal.isChecked = isChecked

            if(isChecked) {
                createSellOrderViewModel?.getQuantityTypeSelected()?.value = QuantityType.QUINTAL.type
                bindingFragmentAddNewSellOrder.rbQuantityTypeKg.isChecked = false
                bindingFragmentAddNewSellOrder.rbQuantityTypeTon.isChecked = false
            }


        }
    }


    private fun onTonTypeCheckChanged() {

        bindingFragmentAddNewSellOrder.rbQuantityTypeTon.setOnCheckedChangeListener { buttonView, isChecked ->
            bindingFragmentAddNewSellOrder.ivQuantityTonSelected.visibility = if(isChecked) View.VISIBLE else View.INVISIBLE
            bindingFragmentAddNewSellOrder.rbQuantityTypeTon.isChecked = isChecked

            if(isChecked){
                createSellOrderViewModel?.getQuantityTypeSelected()?.value  = QuantityType.TON.type
                bindingFragmentAddNewSellOrder.rbQuantityTypeKg.isChecked = false
                bindingFragmentAddNewSellOrder.rbQuantityTypeQuintal.isChecked = false
            }
        }
    }


    private fun initializeFocusChangeListeners() {
        bindingFragmentAddNewSellOrder.etEnterCommodity.setOnFocusChangeListener { v, hasFocus->
            if(!hasFocus) createSellOrderViewModel?.validateEnteredCommodity()
        }

        bindingFragmentAddNewSellOrder.etDispatchDate.setOnFocusChangeListener {v, hasFocus ->
            if(!hasFocus) createSellOrderViewModel?.validateDispatchDate()
        }


    }

    private fun initializeClickListeners() {
        NiroAppUtils.setBackPressedCallback(requireActivity(),viewLifecycleOwner,this)

        bindingFragmentAddNewSellOrder.etDispatchDate.setOnClickListener { openDateDialog() }

        bindingFragmentAddNewSellOrder.btnCreateSellOrder.setOnClickListener { createSellOrder() }

        bindingFragmentAddNewSellOrder.etAddImage.setOnClickListener { checkForCameraPermissionOrOpenCamera() }
    }


    private fun createSellOrder() {
        val validationMessage = createSellOrderViewModel?.validateAllFields() ?: 0
        if(validationMessage > 0) {
            NiroAppUtils.showSnackbar(getString(validationMessage),bindingFragmentAddNewSellOrder.root)
            return
        }

        createSellOrderViewModel?.createSellOrder(requireContext())?.observe(viewLifecycleOwner, Observer { response ->

            when(response) {
                is APILoader -> showProgress(getString(R.string.creating_sell_order),true)

                is APIError -> {
                  showProgress("",false)
                    NiroAppUtils.showSnackbar(response.errorMessage, bindingFragmentAddNewSellOrder.root)
                }

                is Success<*> -> {
                   showProgress("",false)
                    NiroAppUtils.showSnackbar(
                        getString(R.string.sell_order_created),
                        bindingFragmentAddNewSellOrder.root
                    )
                    createSellOrderViewModel?.resetAllFields()
                    goBackToHomeScreen()
                }
        }
        })
    }

    private fun goBackToHomeScreen() {
       navigateToHomeScreen()

    }

    private fun openDateDialog() {
        NiroAppUtils.hideKeyBoard(bindingFragmentAddNewSellOrder.root,context)
        activity?.let {
            DatePickerDialog(it, this, createSellOrderViewModel?.getDispatchDate()?.value,minDate = Date()).show(it.supportFragmentManager,
                NiroAppConstants.TAG_DIALOG_DATE) }

    }


    private fun showProgress(message : String,toShow : Boolean) {
        if(toShow) {
            mProgressDialog = context?.let {
                NiroAppUtils.showLoaderProgress(message, it)
            }
        }
        else {
            if(mProgressDialog?.isShowing == true) mProgressDialog?.dismiss()
        }
    }


    private fun askForCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {

        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CAMERA),
                NiroAppConstants.KEY_REQUEST_CAMERA_PERMISSION
            )
        }
    }


    private fun askForExternalStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                NiroAppConstants.KEY_REQUEST_WRITE_STORAGE_PERMISSION
            )
        }
    }




    private fun openCamera(requestId: Int, prefix: String) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (activity?.packageManager?.let { takePictureIntent.resolveActivity(it) } != null) {
            var photoFile: File? = null
            try {
                photoFile = context?.let { NiroAppUtils.createImageFile(prefix, it) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            mCapturedPath = photoFile?.absolutePath
            if (photoFile != null) {
                val photoURI = context?.let { context ->
                    FileProvider.getUriForFile(context,
                        "com.niro.niroapp.fileprovider", photoFile)
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(
                    takePictureIntent,
                    requestId
                )
            }
        }


    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            NiroAppConstants.KEY_REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        checkForStoragePermissionOrOpenCamera(
                            NiroAppConstants.KEY_REQUEST_CAPTURE_PHOTO, NiroAppConstants.PREFIX_ORDER_IMAGE)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            NiroAppConstants.KEY_REQUEST_WRITE_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkForCameraPermissionOrOpenCamera()
                }

            } else -> askForCameraPermission()
        }

    }

    private fun checkForCameraPermissionOrOpenCamera() {
        if (!PermissionUtils.isCameraPermissionGranted(context)) {
            askForCameraPermission()
            return
        }

        openCamera(NiroAppConstants.KEY_REQUEST_CAPTURE_PHOTO, NiroAppConstants.PREFIX_ORDER_IMAGE)

    }


    private fun checkForStoragePermissionOrOpenCamera(requestId: Int, prefix: String
    ) {
        if (!PermissionUtils.isStoragePermissionGranted(context)) {
            askForExternalStoragePermission()
            return
        }
        openCamera(requestId, prefix)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == NiroAppConstants.KEY_REQUEST_CAPTURE_PHOTO) {
            context?.let { FirebaseFileUploadService(it).uploadFile(mCapturedPath ?: "",mCurrentUser?.id ?: "",3,this,FileType.ORDER_IMAGE) }
            createSellOrderViewModel?.getOrderImages()?.value?.add( File(mCapturedPath ?: "").name)
            createSellOrderViewModel?.getOrderImagesAbsolutePath()?.value?.add(mCapturedPath ?: "")

            loadImage(mCapturedPath)
            return
        }


    }

    private fun loadImage(path : String?) {
        bindingFragmentAddNewSellOrder.ivOrderImage.visibility = if(!path.isNullOrEmpty()) View.VISIBLE else View.INVISIBLE
        context?.let {
            Glide.with(it).load(mCapturedPath)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.rice)
                .centerCrop()
                .into(bindingFragmentAddNewSellOrder.ivOrderImage)
        }
    }

    override fun onDateChanged(date: String?) {
        createSellOrderViewModel?.getDispatchDate()?.value = date
        bindingFragmentAddNewSellOrder.etDispatchDate.setText(createSellOrderViewModel?.getDispatchDateDisplayValue()?.value ?: "")

    }

    override fun onFileUploaded(fileType: FileType) {

    }

    override fun onFileUploadFailed(fileType: FileType) {

    }

    override fun onBackPressed() {
        navigateToHomeScreen()
    }

    private fun navigateToHomeScreen() {
        findNavController().popBackStack(R.id.navigation_home, false)
    }


}